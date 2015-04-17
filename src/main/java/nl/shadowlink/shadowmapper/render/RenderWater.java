/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.shadowlink.shadowmapper.render;

import com.jogamp.opengl.GL2;
import nl.shadowlink.shadowgtalib.model.model.Vector3D;
import nl.shadowlink.shadowgtalib.texturedic.TextureDic;
import nl.shadowlink.shadowgtalib.water.WaterPlane;
import nl.shadowlink.shadowmapper.FileManager;
import nl.shadowlink.shadowmapper.constants.Constants;
import java.io.File;

/**
 * @author Kilian Steenman (Shadow-Link)
 */
public class RenderWater {
	public FileManager mFileManager;
	private int[] waterTex = null;

	public void init(final FileManager pFileManager) {
		mFileManager = pFileManager;

		// TODO: Fallback to default texture/color?
		// TODO: Init water texture
		// Check if the required texture file exists
		// final String waterTextureFilePath = pFileManager.getGameDir() + "/pc/textures/water.wtd";
		// if(new File(waterTextureFilePath).exists()) {
		// TextureDic WTD = new TextureDic(waterTextureFilePath, null, Constants.gIV, 23655);
		// waterTex = WTD.mTextureIds;
		// }
	}

	public void display(GL2 gl) {
		if (mFileManager != null && waterTex != null) {
			gl.glBindTexture(GL2.GL_TEXTURE_2D, waterTex[0]);
			drawWater(gl);
		}
	}

	public void drawWater(GL2 gl) {
		gl.glPushName(Constants.pickWater);
		gl.glBegin(GL2.GL_QUADS);

		for (int i = 0; i < mFileManager.waters[0].planes.size(); i++) {
			WaterPlane plane = mFileManager.waters[0].planes.get(i);
			if (plane.selected)
				gl.glColor3f(0.9f, 0, 0);
			else
				gl.glColor4f(0, 0.4f, 1.0f, 0.5f);

			Vector3D p0 = plane.points[0].coord;
			Vector3D p1 = plane.points[1].coord;
			Vector3D p2 = plane.points[2].coord;
			Vector3D p3 = plane.points[3].coord;

			gl.glPushName(i);
			gl.glTexCoord2f(plane.u, plane.u);
			gl.glVertex3f(p0.x, p0.y, p0.z);
			gl.glTexCoord2f(plane.u, plane.v);
			gl.glVertex3f(p2.x, p2.y, p2.z);
			gl.glTexCoord2f(plane.v, plane.v);
			gl.glVertex3f(p3.x, p3.y, p3.z);
			gl.glTexCoord2f(plane.v, plane.u);
			gl.glVertex3f(p1.x, p1.y, p1.z);
			gl.glPopName();
		}

		gl.glEnd();
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glPopName();
	}
}
