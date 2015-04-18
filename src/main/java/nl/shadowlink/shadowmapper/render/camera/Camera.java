package nl.shadowlink.shadowmapper.render.camera;

import nl.shadowlink.shadowgtalib.model.model.Vector3D;

/**
 * This class is used for the Camera movement
 *
 * @author Kilian Steenman
 * @version 1.0
 */
public abstract class Camera {

	public Vector3D mPosition = new Vector3D();
	public Vector3D view = new Vector3D();
	public Vector3D up = new Vector3D();
	private float camYaw = 0.0f;    // Yaw of the Cam
	private float camPitch = 0.0f;  // Pitch of the Cam

	// The next vars are used to store the current mCamera
	private Vector3D spos;
	private Vector3D sview;
	private Vector3D sup;
	private float scamYaw = 0.0f; // save
	private float scamPitch = 0.0f; // save
	private boolean mIsCameraSaved = false;  // Check if the mCamera is mIsCameraSaved

	/** Speed of the camera */
	protected float mCamSpeed = 0.5f;

	/** Listener that listens for mCamera updates */
	private CameraUpdatedListener mCameraUpdatedListener;

	/**
	 * Listener for mCamera updates
	 */
	public interface CameraUpdatedListener {

		/**
		 * OnCameraMoved listener, called when the mCamera moved
		 * 
		 * @param pPosition
		 *        the new position of the mCamera
		 */
		void onCameraMoved(final Vector3D pPosition);
	}

	/**
	 * Constructor of the mCamera.
	 *
	 * @param posX
	 *        The X Position of the mCamera
	 * @param posY
	 *        The Y Position of the mCamera
	 * @param posZ
	 *        The Z Position of the mCamera
	 * @param viewX
	 *        The X View of the mCamera
	 * @param viewY
	 *        The Y View of the mCamera
	 * @param viewZ
	 *        The Z View of the mCamera
	 * @param upX
	 *        The X Up of the mCamera
	 * @param upY
	 *        The Y Up of the mCamera
	 * @param upZ
	 *        The Z Up of the mCamera
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
	 * Set the mCamera position to a certain position Use this with pointCamera
	 *
	 * @param posX
	 *        The X Position of the mCamera
	 * @param posY
	 *        The Y Position of the mCamera
	 * @param posZ
	 *        The Z Position of the mCamera
	 */
	public void setCameraPosition(float posX, float posY, float posZ) {
		mPosition.x = posX;
		mPosition.y = posY;
		mPosition.z = posZ;
		onCameraMoved();
	}

	/**
	 * Change all vars from the mCamera at once
	 *
	 * @param posX
	 *        The X Position of the mCamera
	 * @param posY
	 *        The Y Position of the mCamera
	 * @param posZ
	 *        The Z Position of the mCamera
	 * @param viewX
	 *        The X View of the mCamera
	 * @param viewY
	 *        The Y View of the mCamera
	 * @param viewZ
	 *        The Z View of the mCamera
	 * @param upX
	 *        The X Up of the mCamera
	 * @param upY
	 *        The Y Up of the mCamera
	 * @param upZ
	 *        The Z Up of the mCamera
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
	 * Point the mCamera to a certain psoition
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
	 *        The speed of the mCamera
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
	 * Change the mCamera rotation
	 *
	 * @param speed
	 *        The speed of the mCamera
	 */
	public void rotateView(float speed) {
		float x = view.x - mPosition.x;
		float z = view.z - mPosition.z;
		view.z = (float) (mPosition.z + Math.sin(speed) * x + Math.cos(speed) * z);
		view.x = (float) (mPosition.x + Math.cos(speed) * x - Math.sin(speed) * z);
	}

	/**
	 * Strafe the mCamera
	 *
	 * @param speed
	 *        The speed of the mCamera
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
	 * @return The X Position of the mCamera
	 */
	public float getPosX() {
		return mPosition.x;
	}

	/**
	 * Returns the current Y position
	 *
	 * @return The Y Position of the mCamera
	 */
	public float getPosY() {
		return mPosition.y;
	}

	/**
	 * Returns the current Z position
	 *
	 * @return The Z position of the mCamera
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
	 * Reset the mCamera to the default position
	 */
	public void reset() {
		setAllCamera(-20, 45, -16, -15, 45, -12, 0, 1, 0);
		camPitch = 0.0f;
		camYaw = 0.0f;
	}

	/**
	 * Set the mCamera pitch
	 *
	 * @param pitch
	 *        Set the mCamera Pitch
	 */
	public void setPitch(float pitch) {
		this.camPitch = pitch;
	}

	/**
	 * Set the mCamera yaw
	 *
	 * @param yaw
	 *        Set the mCamera Yaw
	 */
	public void setYaw(float yaw) {
		this.camYaw = yaw;
	}

	/**
	 * Returns the mCamera pitch
	 *
	 * @return The mCamera pitch
	 */
	public float getPitch() {
		return camPitch;
	}

	/**
	 * Returns the mCamera yaw
	 *
	 * @return The mCamera yaw
	 */
	public float getYaw() {
		return camYaw;
	}

	/**
	 * Stores the current mCamera position
	 */
	public void saveCamera() {
		if (!mIsCameraSaved) {
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
			mIsCameraSaved = true;
		}
	}

	/**
	 * Loads the stored mCamera position
	 */
	public void loadCamera() {
		if (mIsCameraSaved) {
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
			mIsCameraSaved = false;
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

	public abstract void update();
}
