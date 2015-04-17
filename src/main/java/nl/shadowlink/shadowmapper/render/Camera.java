package nl.shadowlink.shadowmapper.render;

import nl.shadowlink.shadowgtalib.model.model.Vector3D;
import javax.swing.JTextField;

/**
 * This class is used for the Camera movement
 *
 * @author Kilian Steenman
 * @version 1.0
 */
public class Camera {

	public Vector3D mPosition = new Vector3D();
	public Vector3D view = new Vector3D();
	public Vector3D up = new Vector3D();
	private float camYaw = 0.0f;    // Yaw of the Cam
	private float camPitch = 0.0f;  // Pitch of the Cam

	// The next vars are used to store the current camera
	private Vector3D spos;
	private Vector3D sview;
	private Vector3D sup;
	private float scamYaw = 0.0f; // save
	private float scamPitch = 0.0f; // save
	private boolean saved = false;  // Check if the camera is saved

	/** Listener that listens for camera updates */
	private CameraUpdatedListener mCameraUpdatedListener;

	/**
	 * Listener for camera updates
	 */
	public interface CameraUpdatedListener {

		/**
		 * OnCameraMoved listener, called when the camera moved
		 * 
		 * @param pPosition
		 *        the new position of the camera
		 */
		void onCameraMoved(final Vector3D pPosition);
	}

	/**
	 * Constructor of the camera.
	 *
	 * @param posX
	 *        The X Position of the camera
	 * @param posY
	 *        The Y Position of the camera
	 * @param posZ
	 *        The Z Position of the camera
	 * @param viewX
	 *        The X View of the camera
	 * @param viewY
	 *        The Y View of the camera
	 * @param viewZ
	 *        The Z View of the camera
	 * @param upX
	 *        The X Up of the camera
	 * @param upY
	 *        The Y Up of the camera
	 * @param upZ
	 *        The Z Up of the camera
	 */
	public Camera(float posX, float posY, float posZ, float viewX, float viewY, float viewZ, float upX, float upY, float upZ) {
		mPosition.x = posX;
		mPosition.y = posY;
		mPosition.z = posZ;
		view.x = viewX;
		view.y = viewY;
		view.z = viewZ;
		up.x = upX;
		up.y = upY;
		up.z = upZ;
		onCameraMoved();
	}

	/**
	 * Set the camera position to a certain position Use this with pointCamera
	 *
	 * @param posX
	 *        The X Position of the camera
	 * @param posY
	 *        The Y Position of the camera
	 * @param posZ
	 *        The Z Position of the camera
	 */
	public void setCameraPosition(float posX, float posY, float posZ) {
		mPosition.x = posX;
		mPosition.y = posY;
		mPosition.z = posZ;
		onCameraMoved();
	}

	/**
	 * Change all vars from the camera at once
	 *
	 * @param posX
	 *        The X Position of the camera
	 * @param posY
	 *        The Y Position of the camera
	 * @param posZ
	 *        The Z Position of the camera
	 * @param viewX
	 *        The X View of the camera
	 * @param viewY
	 *        The Y View of the camera
	 * @param viewZ
	 *        The Z View of the camera
	 * @param upX
	 *        The X Up of the camera
	 * @param upY
	 *        The Y Up of the camera
	 * @param upZ
	 *        The Z Up of the camera
	 */
	public void setAllCamera(float posX, float posY, float posZ, float viewX, float viewY, float viewZ, float upX, float upY, float upZ) {
		mPosition.x = posX;
		mPosition.y = posY;
		mPosition.z = posZ;
		view.x = viewX;
		view.y = viewY;
		view.z = viewZ;
		up.x = upX;
		up.y = upY;
		up.z = upZ;
		onCameraMoved();
	}

	/**
	 * Point the camera to a certain psoition
	 *
	 * @param viewX
	 * @param viewY
	 * @param viewZ
	 */
	public void pointCamera(float viewX, float viewY, float viewZ) {
		view.x = viewX;
		view.y = viewY;
		view.z = viewZ;
	}

	/**
	 * Move Camera in the direction you are viewing
	 *
	 * @param speed
	 *        The speed of the camera
	 */
	public void moveCamera(float speed) {
		float x = view.x - mPosition.x;
		float y = view.y - mPosition.y;
		float z = view.z - mPosition.z;

		mPosition.x = mPosition.x + x * speed;
		mPosition.y = mPosition.y + y * speed;
		mPosition.z = mPosition.z + z * speed;
		view.x = view.x + x * speed;
		view.y = view.y + y * speed;
		view.z = view.z + z * speed;
		onCameraMoved();
	}

	/**
	 * Change the camera rotation
	 *
	 * @param speed
	 *        The speed of the camera
	 */
	public void rotateView(float speed) {
		float x = view.x - mPosition.x;
		float z = view.z - mPosition.z;
		view.z = (float) (mPosition.z + Math.sin(speed) * x + Math.cos(speed) * z);
		view.x = (float) (mPosition.x + Math.cos(speed) * x - Math.sin(speed) * z);
	}

	/**
	 * Strafe the camera
	 *
	 * @param speed
	 *        The speed of the camera
	 */
	public void strafeCamera(float speed) {
		float x = view.x - mPosition.x;
		float z = view.z - mPosition.z;
		float oX;
		float oZ;

		oX = -z;
		oZ = x;

		mPosition.x = mPosition.x + oX * speed;
		mPosition.z = mPosition.z + oZ * speed;
		view.x = view.x + oX * speed;
		view.z = view.z + oZ * speed;
		onCameraMoved();
	}

	private void onCameraMoved() {
		if (mCameraUpdatedListener != null) {
			mCameraUpdatedListener.onCameraMoved(mPosition);
		}
	}

	/**
	 * Returns the current X position
	 *
	 * @return The X Position of the camera
	 */
	public float getPosX() {
		return mPosition.x;
	}

	/**
	 * Returns the current Y position
	 *
	 * @return The Y Position of the camera
	 */
	public float getPosY() {
		return mPosition.y;
	}

	/**
	 * Returns the current Z position
	 *
	 * @return The Z position of the camera
	 */
	public float getPosZ() {
		return mPosition.z;
	}

	/**
	 * Returns the current X view
	 *
	 * @return The X view
	 */
	public float getViewX() {
		return view.x;
	}

	/**
	 * Returns the current Y view
	 *
	 * @return The Y view
	 */
	public float getViewY() {
		return view.y;
	}

	/**
	 * Returns the current Z view
	 *
	 * @return The Z view
	 */
	public float getViewZ() {
		return view.z;
	}

	/**
	 * Returns the current X up
	 *
	 * @return The X up
	 */
	public float getUpX() {
		return up.x;
	}

	/**
	 * Returns the current Y up
	 *
	 * @return The Y up
	 */
	public float getUpY() {
		return up.y;
	}

	/**
	 * Returns the current Z up
	 *
	 * @return The Z up
	 */
	public float getUpZ() {
		return up.z;
	}

	/**
	 * Set X view
	 *
	 * @param x
	 *        Set the X view
	 */
	public void setViewX(float x) {
		this.view.x = x;
	}

	/**
	 * Set Y view
	 *
	 * @param y
	 *        Set the Y view
	 */
	public void setViewY(float y) {
		this.view.y = y;
	}

	/**
	 * Set Z view
	 *
	 * @param z
	 *        Set the Z view
	 */
	public void setViewZ(float z) {
		this.view.z = z;
	}

	/**
	 * Reset the camera to the default position
	 */
	public void reset() {
		setAllCamera(-20, 45, -16, -15, 45, -12, 0, 1, 0);
		camPitch = 0.0f;
		camYaw = 0.0f;
	}

	/**
	 * Set the camera pitch
	 *
	 * @param pitch
	 *        Set the camera Pitch
	 */
	public void setPitch(float pitch) {
		this.camPitch = pitch;
	}

	/**
	 * Set the camera yaw
	 *
	 * @param yaw
	 *        Set the camera Yaw
	 */
	public void setYaw(float yaw) {
		this.camYaw = yaw;
	}

	/**
	 * Returns the camera pitch
	 *
	 * @return The camera pitch
	 */
	public float getPitch() {
		return camPitch;
	}

	/**
	 * Returns the camera yaw
	 *
	 * @return The camera yaw
	 */
	public float getYaw() {
		return camYaw;
	}

	/**
	 * Stores the current camera position
	 */
	public void saveCamera() {
		if (!saved) {
			this.spos.x = mPosition.x;
			this.spos.y = mPosition.y;
			this.spos.z = mPosition.z;
			this.sview.x = view.x;
			this.sview.y = view.y;
			this.sview.z = view.z;
			this.sup.x = up.x;
			this.sup.y = up.y;
			this.sup.z = up.z;
			this.scamPitch = camPitch;
			this.scamYaw = camYaw;
			saved = true;
		}
	}

	/**
	 * Loads the stored camera position
	 */
	public void loadCamera() {
		if (saved) {
			this.mPosition.x = spos.x;
			this.mPosition.y = spos.y;
			this.mPosition.z = spos.z;
			this.view.x = sview.x;
			this.view.y = sview.y;
			this.view.z = sview.z;
			this.up.x = sup.x;
			this.up.y = sup.y;
			this.up.z = sup.z;
			this.camPitch = scamPitch;
			this.camYaw = scamYaw;
			saved = false;
		}
	}

	/**
	 * Set the CameraUpdatedListener
	 * 
	 * @param pCameraUpdatedListener
	 *        the CameraUpdatedListener
	 */
	public void setCameraUpdatedListener(final CameraUpdatedListener pCameraUpdatedListener) {
		mCameraUpdatedListener = pCameraUpdatedListener;
	}
}
