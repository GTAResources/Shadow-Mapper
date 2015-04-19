/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.shadowlink.shadowmapper.render;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.GLU;
import nl.shadowlink.shadowmapper.FileManager;
import nl.shadowlink.shadowmapper.constants.Constants;
import nl.shadowlink.shadowmapper.render.camera.Camera;
import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.nio.IntBuffer;
import javax.swing.JLabel;

/**
 * @author Kilian
 */
public class GLListener implements GLEventListener {
	public RenderMap renderMap;
	public RenderWater renderWater;
	public RenderVehicles renderCars;
	public FileManager fm;

	public Camera mCamera;

	private boolean wireFrame = false;
	private boolean pick = false;

	public boolean displayWater = true;
	public boolean displayMap = true;
	public boolean displayZones = false;
	public boolean displayCars = true;

	private Point mousePos = new Point(0, 0);

	private boolean mShouldTakeScreenshot = false;

	// Picking stuff
	private int selectBuf[];
	private IntBuffer selectBuffer;

	// used for FPS
	private float fps = 0.0f;
	private long previousTime;

	public interface FPSListener {
		void onFPSUpdated(final float pFPS);
	}

	private FPSListener mFPSListener;

	private void takeScreenshot(GL gl) {
		// TODO: Fix screenshot if we want to
		// try {
		// int[] viewport = { 0, 0, 0, 0 };
		// gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		// int screenNumber = 1;
		// File file = new File("Shadow" + screenNumber + ".png");
		// while (file.exists()) {
		// screenNumber++;
		// file = new File("Shadow" + screenNumber + ".png");
		// }
		// Screenshot.writeToFile(file, viewport[2], viewport[3]);
		// file = null;
		// } catch (IOException ex) {
		// Logger.getLogger(mGLListener.class.getName()).log(Level.SEVERE, null, ex);
		// } catch (GLException ex) {
		// Logger.getLogger(mGLListener.class.getName()).log(Level.SEVERE, null, ex);
		// }
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// Update camera
		mCamera.update();

		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		if (pick) {
			startPicking(gl, mousePos.x, mousePos.y);
		}

		// Reset the current matrix to the "identity"
		gl.glLoadIdentity();

		if (mCamera != null) {
			glu.gluLookAt(mCamera.getPosX(), mCamera.getPosY(), mCamera.getPosZ(), mCamera.getViewX(), mCamera.getViewY(), mCamera.getViewZ(),
					mCamera.getUpX(), mCamera.getUpY(), mCamera.getUpZ());
		}

		if (wireFrame) {
			gl.glPolygonMode(GL.GL_FRONT, GL2.GL_LINE);
		} else {
			gl.glPolygonMode(GL.GL_FRONT, GL2.GL_FILL);
		}

		gl.glRotatef(270, 1.0f, 0.0f, 0.0f);

		if (displayMap) {
			renderMap.display(gl);
		}
		if (displayWater) {
			// renderWater.display(gl);
		}
		if (displayCars) {
			// renderCars.display(gl);
		}

		if (pick) {
			stopPicking(gl);
			pick = false;
		}

		if (mShouldTakeScreenshot) {
			takeScreenshot(gl);
			mShouldTakeScreenshot = false;
		}

		updateFPS();
	}

	public void startPicking(GL2 gl, int mouseX, int mouseY) {
		GLU glu = new GLU();
		int viewport[] = new int[4];
		float ratio;

		gl.glSelectBuffer(512, selectBuffer);

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		gl.glRenderMode(GL2.GL_SELECT);

		gl.glInitNames();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		glu.gluPickMatrix(mouseX, viewport[3] - mouseY, 5, 5, viewport, 0);
		ratio = (float) (viewport[2] + 0.0) / viewport[3];
		glu.gluPerspective(45, ratio, 0.1, 1000);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	public void stopPicking(GL2 gl) {

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glFlush();
		int hits = gl.glRenderMode(GL2.GL_RENDER);
		if (hits != 0) {
			System.out.println("HITS: " + hits);
			selectBuffer.get(selectBuf);
			processHits(hits, selectBuf);
			selectBuf = new int[512];
			selectBuffer = Buffers.newDirectIntBuffer(512);
		} else {
			fm.setSelection(-1, -1, -1);
		}
	}

	private void processHits(int hits, int buffer[]) {
		int names, ptr = 0;
		int cPickType = -1, cParam2 = -1, cParam3 = -1;
		int minZ = 0xffffffff;

		for (int i = 0; i < hits; i++) { /* for each hit */
			names = buffer[ptr];
			System.out.println("Number of names for hit: " + names);
			ptr++;
			int z1 = buffer[ptr];
			System.out.println("z1 is " + buffer[ptr]);
			ptr++;
			System.out.println("z2 is " + buffer[ptr]);
			ptr++;
			if (names != 0) {
				int pickType = buffer[ptr];
				int param2 = -1, param3 = -1;
				ptr++;
				System.out.println("PickType: " + pickType);
				switch (pickType) {
					case Constants.pickMap:
						param2 = buffer[ptr];
						ptr++;
						param3 = buffer[ptr];
						ptr++;
						System.out.println("IPL: " + param2);
						System.out.println("Item: " + param3);
						break;
					case Constants.pickWater:
						param2 = buffer[ptr];
						ptr++;
						System.out.println("Plane: " + param2);
						break;
					case Constants.pickCar:
						param2 = buffer[ptr];
						ptr++;
						param3 = buffer[ptr];
						ptr++;
						System.out.println("IPL: " + param2);
						System.out.println("CarID: " + param3);
						break;
					default:
						System.out.println("Picked nothing usefull");
				}
				if (z1 < minZ) {
					minZ = z1;
					cPickType = pickType;
					cParam2 = param2;
					cParam3 = param3;
				}
			}
		}
		fm.setSelection(cPickType, cParam2, cParam3);
		// TODO: Fix selection changed
		// mainForm.selectionChanged();
	}

	public void drawCube(GL2 gl, float size, float red, float green, float blue) {
		gl.glColor3f(red, green, blue); // Set The Color To Green
		gl.glBegin(GL2.GL_QUADS); // Start Drawing The Cube

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

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();

		if (height <= 0) { // avoid a divide by zero error!

			height = 1;
		}
		final float h = (float) width / (float) height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, h, 0.1, 1000.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities caps = new GLCapabilities(profile);
		caps.setHardwareAccelerated(true);
		caps.setDoubleBuffered(true);

		GL2 gl = drawable.getGL().getGL2();

		gl.glClearColor(0.0f, 0.693f, 1.0f, 0.0f);

		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glCullFace(GL.GL_BACK);

		gl.glPolygonMode(GL.GL_FRONT, GL2.GL_FILL);

		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL.GL_BLEND);
		// gl.glDisable(gl.GL_COLOR_MATERIAL);

		renderMap = new RenderMap();
		renderMap.init(mCamera, fm);

		renderWater = new RenderWater();
		renderWater.init(fm);

		renderCars = new RenderVehicles();
		renderCars.init(gl, fm);

		// setup the selection buffer
		selectBuf = new int[512];
		selectBuffer = Buffers.newDirectIntBuffer(512);
	}

	private void updateFPS() {
		long currentTime = (System.currentTimeMillis());

		++fps;

		if (currentTime - previousTime >= 1000) {
			previousTime = currentTime;
			if (mFPSListener != null) {
				mFPSListener.onFPSUpdated(fps);
			}
			fps = 0.0f;
		}
	}

	public void setFPSListener(final FPSListener pFPSListener) {
		mFPSListener = pFPSListener;
	}

	public void setWireFrame(boolean wireFrame) {
		this.wireFrame = wireFrame;
	}

	public void setPick() {
		System.out.println("We gaan pikken");
		pick = true;
	}

	public void setCurrentMousePos(Point pos) {
		this.mousePos = pos;
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
		renderMap.mFileManager = fm;
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	public void setCamera(final Camera pCamera) {
		mCamera = pCamera;
	}

}
