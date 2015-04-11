///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package nl.shadowlink.shadowmapper.render;
//
//import javax.media.opengl.GL2;
//
//import model.Model;
//import shadowmapper.FileManager;
//import shadowmapper.Constants;
//import IPL.Item_CARS;
//import Texturedic.TextureDic;
//
///**
// *
// * @author Kilian
// */
//public class RenderVehicles {
//	public FileManager fm;
//	private int[] glCarList = new int[5];
//
//	public void init(GL2 gl, FileManager fm) {
//		this.fm = fm;
//
//		// load the car model and texture
//		TextureDic TXD = new TextureDic("resources/vehicles.txd", null, Constants.gSA, gl, 23655);
//		Model tempCar = new Model();
//		tempCar.loadDFF("resources/car.dff");
//		tempCar.attachTXD(TXD.texName, TXD.textureId);
//		createGenList(gl, 0, tempCar);
//		tempCar.reset();
//		tempCar.loadDFF("resources/boat.dff");
//		tempCar.attachTXD(TXD.texName, TXD.textureId);
//		createGenList(gl, 1, tempCar);
//		tempCar.reset();
//		tempCar.loadDFF("resources/plane.dff");
//		tempCar.attachTXD(TXD.texName, TXD.textureId);
//		createGenList(gl, 2, tempCar);
//		tempCar.reset();
//		tempCar.loadDFF("resources/heli.dff");
//		tempCar.attachTXD(TXD.texName, TXD.textureId);
//		createGenList(gl, 3, tempCar);
//		tempCar.reset();
//		tempCar.loadDFF("resources/bike.dff");
//		tempCar.attachTXD(TXD.texName, TXD.textureId);
//		createGenList(gl, 4, tempCar);
//	}
//
//	public void createGenList(GL2 gl, int id, Model mdl) {
//		glCarList[id] = gl.glGenLists(1);
//		gl.glNewList(glCarList[id], GL2.GL_COMPILE);
//		mdl.render(gl);
//		gl.glEndList();
//	}
//
//	public void display(GL2 gl) {
//		if (fm != null) {
//			gl.glPushName(Constants.pickCar);
//			for (int i = 0; i < fm.mIPLFiles.length; i++) {
//				gl.glPushName(i);
//				if (fm.mIPLFiles[i].selected) {
//					for (int j = 0; j < fm.mIPLFiles[i].items_cars.size(); j++) {
//						gl.glPushName(j);
//						Item_CARS car = fm.mIPLFiles[i].items_cars.get(j);
//						gl.glPushMatrix();
//						if (car.selected)
//							gl.glColor3f(0.9f, 0, 0);
//						else
//							gl.glColor3f(1, 1, 1);
//
//						// Vector4D rot = item.rotation.getAxisAngle();
//						gl.glTranslatef(car.position.x, car.position.y, car.position.z);
//						gl.glRotatef(1.0f, car.rotation.x, car.rotation.y, car.rotation.z);
//
//						gl.glCallList(glCarList[0]);
//
//						gl.glPopMatrix();
//						gl.glPopName();
//					}
//				}
//				gl.glPopName();
//			}
//			gl.glPopName();
//		}
//	}
//}
