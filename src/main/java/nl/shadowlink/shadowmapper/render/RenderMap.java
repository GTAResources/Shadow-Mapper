/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.shadowlink.shadowmapper.render;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import nl.shadowlink.file_io.ByteReader;
import nl.shadowlink.file_io.ReadFunctions;
import nl.shadowlink.shadowgtalib.ide.Item_OBJS;
import nl.shadowlink.shadowgtalib.img.IMG_Item;
import nl.shadowlink.shadowgtalib.ipl.Item_INST;
import nl.shadowlink.shadowgtalib.model.model.*;
import nl.shadowlink.shadowgtalib.texturedic.Texture;
import nl.shadowlink.shadowgtalib.texturedic.TextureDic;
import nl.shadowlink.shadowmapper.FileManager;
import nl.shadowlink.shadowmapper.constants.Constants;
import nl.shadowlink.shadowmapper.render.camera.Camera;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kilian
 */
public class RenderMap {
	private int glDisplayList[] = new int[0];

	private Camera mCamera;

	public FileManager mFileManager;

	public boolean pick = false;
	public boolean reload = false;
	public boolean loading = false;
	public boolean added = false;

	private int gameType = 3;

	public Item_OBJS tempIDE = null;
	public Item_INST tempIPL = null;

	private HashMap<String, HashMap<String, Integer>> mTextureDicHashMap = new HashMap();

	public void init(Camera pCamera, FileManager pFileManager) {
		mFileManager = pFileManager;
		mCamera = pCamera;
	}

	private void loadMap(GL2 gl) {

		for (int i = 0; i < glDisplayList.length; i++) {
			gl.glDeleteLists(glDisplayList[i], 1);
		}

		glDisplayList = null;
		ArrayList<Boolean> boolList = new ArrayList();
		ArrayList<Item_OBJS> ideList = new ArrayList();
		// Loop through all IPL's
		for (int iplCount = 0; iplCount < mFileManager.mIPLFiles.length; iplCount++) {
			// Check if the IPL has been selected for rendering
			if (mFileManager.mIPLFiles[iplCount].selected) {
				// Go through all IPL instances
				ArrayList<Item_INST> itemsInstances = mFileManager.mIPLFiles[iplCount].items_inst;
				for (Item_INST itemInstance : itemsInstances) { //
					int ideNumber = 0;
					Item_OBJS ideItem = (Item_OBJS) mFileManager.mIDEFiles[ideNumber].findItem(itemInstance.name);
					while (ideItem == null) {
						ideNumber++;
						if (ideNumber < mFileManager.mIDEFiles.length) {
							ideItem = (Item_OBJS) mFileManager.mIDEFiles[ideNumber].findItem(itemInstance.name);
						} else {
							Logger.getGlobal().log(Level.WARNING, "Unable to find {0} in IDE", itemInstance.name);
							break;
						}
					}
					if (ideItem != null) {
						boolean found = false;
						for (int i = 0; i < ideList.size(); i++) {
							if (ideList.get(i).equals(ideItem)) {
								found = true;
								itemInstance.glListID = i + 1;
								itemInstance.drawDistance = ideItem.drawDistance[0];
							}
						}
						if (!found) {
							itemInstance.glListID = ideList.size() + 1;
							itemInstance.drawDistance = ideItem.drawDistance[0];
							ideList.add(ideItem);
							boolList.add(false);
						}
					} else {
						itemInstance.glListID = 0;
					}
				}
				mFileManager.mIPLFiles[iplCount].itemsLoaded = true;
			}
		}
		glDisplayList = new int[ideList.size() + 1];
		System.out.println("ideList Size: " + ideList.size());

		for (int imgNumber = 0; imgNumber < mFileManager.mIMGFiles.length; imgNumber++) {
			ReadFunctions rf = new ReadFunctions(); // open the img file
			if (rf.openFile(mFileManager.mIMGFiles[imgNumber].getFileName())) {
				System.out.println("Opened: " + mFileManager.mIMGFiles[imgNumber].getFileName());
				for (int i = 0; i < ideList.size(); i++) {
					if (!boolList.get(i)) {
						String modelName = "";
						IMG_Item item = null;
						if (!ideList.get(i).WDD.equals("null")) {
							modelName = ideList.get(i).WDD + ".wdd";
							item = mFileManager.mIMGFiles[imgNumber].findItem(modelName);
						} else {
							modelName = ideList.get(i).modelName + ".wdr";
							item = mFileManager.mIMGFiles[imgNumber].findItem(modelName);
							if (item == null)
								item = mFileManager.mIMGFiles[imgNumber].findItem(ideList.get(i).modelName + ".wft");
						}
						if (item != null) {
							rf.seek(item.getOffset());
							ByteReader br = rf.getByteReader(item.getSize());
							Model mdl = null;
							if (item.getName().endsWith(".wdr")) {
								System.out.println(item.getName());
								// Load the model from img
								mdl = new Model().loadWDR(br, item.getSize());
							} else if (item.getName().endsWith(".wdd")) {
								mdl = new Model().loadWDD(br, item.getSize(), ideList.get(i).modelName);
							} else if (item.getName().endsWith(".wft")) {
								System.out.println("Loading WFT: " + item.getName());
								mdl = new Model().loadWFT(br, item.getSize());
							}
							String texName = ideList.get(i).textureName + ".wtd";
							item = mFileManager.mIMGFiles[imgNumber].findItem(texName);
							TextureDic txd = null;
							if (item != null) {
								rf.seek(item.getOffset());
								br = rf.getByteReader(item.getSize());
								txd = new TextureDic(texName, br, gameType, item.getSize());
								loadTextureDic(gl, txd);
							}
							glDisplayList[i + 1] = gl.glGenLists(1);
							gl.glNewList(glDisplayList[i + 1], GL2.GL_COMPILE);
							if (mdl != null) {
								drawModel(gl, mdl, txd);
							} else {
								drawCube(gl, 10, 0.1f, 0.5f, 0.9f);
							}
							gl.glEndList();
							mdl.reset();
							boolList.set(i, true);
						}
					}
				}
				rf.closeFile();
			}
		}
	}

	private void loadTextureDic(final GL2 gl, final TextureDic pTextureDic) {
		if (pTextureDic.getTextureCount() == 0) {
			return;
		}

		int[] textureIds = new int[pTextureDic.getTextureCount()];
		gl.glGenTextures(pTextureDic.getTextureCount(), textureIds, 0);

		HashMap<String, Integer> textureHashMap = new HashMap<>();
		for (int i = 0; i < pTextureDic.getTextureCount(); i++) {
			Texture texture = pTextureDic.textures.get(i);
			gl.glBindTexture(GL.GL_TEXTURE_2D, textureIds[i]);

			// TODO: Check if we want to move this to a util class
			ByteBuffer byteBuffer = ByteBuffer.wrap(texture.data);
			byteBuffer.rewind();

			switch (texture.compression) {
				case DXT1:
					gl.glCompressedTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_COMPRESSED_RGB_S3TC_DXT1_EXT, texture.width, texture.height, 0,
							texture.dataSize, byteBuffer);
					break;
				case DXT3:
					gl.glCompressedTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT, texture.width, texture.height, 0,
							texture.dataSize, byteBuffer);
					break;
				case DXT5:
					gl.glCompressedTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT, texture.width, texture.height, 0,
							texture.dataSize, byteBuffer);
					break;
			}

			// gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, 3, pTexture.width, pTexture.height, 0, GL2.GL_RGBA,
			// GL2.GL_UNSIGNED_BYTE, byteBuffer);

			gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);

			textureHashMap.put(texture.difTexName, textureIds[i]);
		}
		mTextureDicHashMap.put(pTextureDic.getFileName(), textureHashMap);
	}

	private void drawModel(final GL2 pGl, final Model pModel, final TextureDic pTextureDic) {
		for (Element element : pModel.getElements()) {
			for (Strip strip : element.getStrips()) {
				if (element.getShaderCount() > strip.getShaderNumber() && pTextureDic != null) {
					HashMap<String, Integer> textureDicHashMap = mTextureDicHashMap.get(pTextureDic.getFileName());
					if (textureDicHashMap != null) {
						Integer glTexId = textureDicHashMap.get(element.getShader(strip.getShaderNumber()).getTextureName());
						if (glTexId != null) {
							pGl.glBindTexture(GL2.GL_TEXTURE_2D, glTexId);// element.getShader(strip.getShaderNumber()).getGLTex());
						}
					}
				}
				renderStrip(pGl, strip);
			}
		}
	}

	/**
	 * Render this strip
	 * 
	 * @param gl
	 *        used to render this strip
	 */
	public void renderStrip(final GL2 pGl, final Strip pStrip) {

		pGl.glBegin(GL.GL_TRIANGLES); // begin triangle object
		for (Polygon polygon : pStrip.getPolygons()) {
			Vertex verta = pStrip.getVertex(polygon.a);
			Vertex vertb = pStrip.getVertex(polygon.b);
			Vertex vertc = pStrip.getVertex(polygon.c);

			// Render first vertex
			pGl.glTexCoord2f(verta.u, verta.v);
			pGl.glVertex3f(verta.x, verta.y, verta.z);

			// Render second vertex
			pGl.glTexCoord2f(vertb.u, vertb.v);
			pGl.glVertex3f(vertb.x, vertb.y, vertb.z);

			// Render third vertex
			pGl.glTexCoord2f(vertc.u, vertc.v);
			pGl.glVertex3f(vertc.x, vertc.y, vertc.z);
		}
		pGl.glEnd();
	}

	private void loadAddedModel(GL2 gl) {
		int tempList[] = glDisplayList; // store all glInts to a temp array
		glDisplayList = new int[tempList.length + 1]; // make the displaylist
														// one bigger

		for (int i = 0; i < tempList.length; i++) {
			glDisplayList[i] = tempList[i];
		}

		IMG_Item item = null;
		int imgID = -1;
		int i = 0;
		while (item == null || i < mFileManager.mIMGFiles.length) {
			if (!tempIDE.WDD.equals("null"))
				item = mFileManager.mIMGFiles[i].findItem(tempIDE.WDD + ".wdd");
			else {
				item = mFileManager.mIMGFiles[i].findItem(tempIDE.modelName + ".wdr");
				if (item == null)
					item = mFileManager.mIMGFiles[i].findItem(tempIDE.modelName + ".wft");
			}
			if (item != null)
				imgID = i;
			i++;
		}

		if (item != null) {
			ReadFunctions rf = new ReadFunctions();
			rf.openFile(mFileManager.mIMGFiles[imgID].getFileName());
			rf.seek(item.getOffset());
			ByteReader br = rf.getByteReader(item.getSize());
			Model mdl = null;
			if (item.getName().endsWith(".wdr")) {
				System.out.println(item.getName());
				mdl = new Model().loadWDR(br, item.getSize()); // load the
																// model
																// from img
			} else if (item.getName().endsWith(".wdd")) {
				mdl = new Model().loadWDD(br, item.getSize(), tempIDE.modelName);
			} else if (item.getName().endsWith(".wft")) {
				System.out.println("Loading WFT: " + item.getName());
				mdl = new Model().loadWFT(br, item.getSize());
			}
			br = null;
			item = mFileManager.mIMGFiles[imgID].findItem(tempIDE.textureName + ".wtd");
			TextureDic txd = null;
			if (item != null) {
				rf.seek(item.getOffset());
				br = rf.getByteReader(item.getSize());
				txd = new TextureDic(tempIDE.textureName + ".wtd", br, gameType, item.getSize());

				if (mdl != null) {
					mdl.attachTXD(txd.texName, txd.mTextureIds);
				}
			}

			glDisplayList[tempList.length] = gl.glGenLists(1);
			gl.glNewList(glDisplayList[tempList.length], GL2.GL_COMPILE);
			if (mdl != null) {
				drawModel(gl, mdl, txd);
			} else {
				drawCube(gl, 10, 0.1f, 0.5f, 0.9f);
			}
			gl.glEndList();
			mdl.reset();
			rf.closeFile();

			tempIPL.glListID = tempList.length;
		} else {
			tempIPL.glListID = 0;
		}

		tempIDE = null;
		tempIPL = null;
	}

	public void display(GL2 gl) {
		if (!loading && mFileManager != null) {
			if (reload) {
				loading = true;
				System.out.println("Started loading");
				loadMap(gl);
				reload = false;
				loading = false;
				System.out.println("Loading finished");
			}
			if (added) {
				loading = true;
				System.out.println("Loading added model");
				loadAddedModel(gl);
				loading = false;
				added = false;
				System.out.println("Loading added model finished");
			}
			gl.glPushName(Constants.pickMap);
			for (int j = 0; j < mFileManager.mIPLFiles.length; j++) {
				if (mFileManager.mIPLFiles[j].selected && mFileManager.mIPLFiles[j].itemsLoaded) {
					gl.glPushName(j);
					for (int i = 0; i < mFileManager.mIPLFiles[j].items_inst.size(); i++) {// instances
						// rendering
						Item_INST item = mFileManager.mIPLFiles[j].items_inst.get(i);
						if (!item.name.toLowerCase().contains("lod")) {
							int drawID = i;
							/* while(getDistance(mCamera.mPosition, item.position) > item.drawDistance){ if(item.lod ==
							 * -1){ drawID = -1; break; }else{ drawID = item.lod; item = mFileManager.mIPLFiles
							 * [mFileManager.mIPLFiles[j].lodWPL].items_inst.get(item.lod); } } */
							// TODO: Drawdistance check (if we even want it)
							// if (getDistance(mCamera.mPosition, item.position) < item.drawDistance) {
							if (drawID != -1) {
								gl.glPushName(drawID);
								if (item.selected) {
									gl.glColor3f(0.9f, 0, 0);
								} else {
									gl.glColor3f(1, 1, 1);
								}

								gl.glPushMatrix();

								gl.glTranslatef(item.position.x, item.position.y, item.position.z);
								gl.glRotatef(item.axisAngle.w, item.axisAngle.x, item.axisAngle.y, item.axisAngle.z);

								gl.glCallList(glDisplayList[item.glListID]);

								gl.glPopMatrix();
								gl.glPopName();
							}
							// }
						}
					}
					gl.glPopName();
				}
			}
			gl.glPopName();
		}
		gl.glFlush();
	}

	public float getDistance(Vector3D test1, Vector3D test2) {
		float dx = test1.x - test2.x;
		float dy = (0 - test1.z) - test2.y;
		float dz = test1.y - test2.z;
		float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
		return distance;
	}

	public void drawCube(GL2 gl, float size, float red, float green, float blue) {
		gl.glColor3f(red, green, blue); // Set The Color To Green
		gl.glBegin(gl.GL_QUADS); // Start Drawing The Cube

		gl.glVertex3f(size, size, -size); // Top Right Of The Quad (Top)
		gl.glVertex3f(-size, size, -size); // Top Left Of The Quad (Top)
		gl.glVertex3f(-size, size, size); // Bottom Left Of The Quad (Top)
		gl.glVertex3f(size, size, size); // Bottom Right Of The Quad (Top)

		gl.glVertex3f(size, -size, size); // Top Right Of The Quad (Bottom)
		gl.glVertex3f(-size, -size, size); // Top Left Of The Quad (Bottom)
		gl.glVertex3f(-size, -size, -size); // Bottom Left Of The Quad (Bottom)
		gl.glVertex3f(size, -size, -size); // Bottom Right Of The Quad (Bottom)

		gl.glVertex3f(size, size, size); // Top Right Of The Quad (Front)
		gl.glVertex3f(-size, size, size); // Top Left Of The Quad (Front)
		gl.glVertex3f(-size, -size, size); // Bottom Left Of The Quad (Front)
		gl.glVertex3f(size, -size, size); // Bottom Right Of The Quad (Front)

		gl.glVertex3f(size, -size, -size); // Bottom Left Of The Quad (Back)
		gl.glVertex3f(-size, -size, -size); // Bottom Right Of The Quad (Back)
		gl.glVertex3f(-size, size, -size); // Top Right Of The Quad (Back)
		gl.glVertex3f(size, size, -size); // Top Left Of The Quad (Back)

		gl.glVertex3f(-size, size, size); // Top Right Of The Quad (Left)
		gl.glVertex3f(-size, size, -size); // Top Left Of The Quad (Left)
		gl.glVertex3f(-size, -size, -size); // Bottom Left Of The Quad (Left)
		gl.glVertex3f(-size, -size, size); // Bottom Right Of The Quad (Left)

		gl.glVertex3f(size, size, -size); // Top Right Of The Quad (Right)
		gl.glVertex3f(size, size, size); // Top Left Of The Quad (Right)
		gl.glVertex3f(size, -size, size); // Bottom Left Of The Quad (Right)
		gl.glVertex3f(size, -size, -size); // Bottom Right Of The Quad (Right)

		gl.glEnd();

		gl.glColor3f(1.0f, 1.0f, 1.0f);
	}

	public void addLoadModelToLoad(Item_INST tempIPL, Item_OBJS tempIDE) {
		this.tempIPL = tempIPL;
		this.tempIDE = tempIDE;
		added = true;
	}

}
