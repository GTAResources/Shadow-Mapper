/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.shadowlink.shadowmapper.render;


import com.jogamp.opengl.GL;
import nl.shadowlink.shadowmapper.FileManager;
import nl.shadowlink.shadowmapper.render.camera.Camera;

/**
 *
 * @author Kilian
 */
public class RenderCollision {
    private Camera camera;
    private FileManager fm;

    public RenderCollision(){
    }

    public void init(GL gl, Camera camera, FileManager fm){
        this.fm = fm;
        this.camera = camera;
    }

    public void display(GL gl){

        gl.glFlush();
    }

}
