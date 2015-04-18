package nl.shadowlink.shadowmapper.render.camera;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Camera used for free camera movement<br/>
 *
 * @author kilian<br/>
 * @date 18 Apr 2015.
 */
public class FreeCamera extends Camera implements KeyListener {

	/** Tag used for logging */
	private static final String LOG_TAG = "FreeCamera";

	/** boolean that indicates if the forward button is pressed */
	private boolean mIsForwardPressed;

	/** boolean that indicates if the backward button is pressed */
	private boolean mIsBackwardPressed;

	/** boolean that indicates if the left button is pressed */
	private boolean mIsLeftPressed;

	/** boolean that indicates if the right button is pressed */
	private boolean mIsRightPressed;

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
	 */
	public FreeCamera(final float posX, final float posY, final float posZ, final float viewX, final float viewY, final float viewZ, final float upX,
			final float upY, final float upZ) {
		super(posX, posY, posZ, viewX, viewY, viewZ, upX, upY, upZ);
	}

	@Override
	public void update() {
		if (mIsForwardPressed) {
			moveCamera(mCamSpeed);
		}
		if (mIsBackwardPressed) {
			moveCamera(-mCamSpeed);
		}
		if (mIsLeftPressed) {
			strafeCamera(-mCamSpeed);
		}
		if (mIsRightPressed) {
			strafeCamera(mCamSpeed);
		}
	}

	@Override
	public void keyTyped(final KeyEvent pKeyEvent) {
		// Do nothing
	}

	@Override
	public void keyPressed(final KeyEvent pKeyEvent) {
		switch (pKeyEvent.getKeyCode()) {
			case KeyEvent.VK_W:
				mIsForwardPressed = true;
				break;
			case KeyEvent.VK_S:
				mIsBackwardPressed = true;
				break;
			case KeyEvent.VK_A:
				mIsLeftPressed = true;
				break;
			case KeyEvent.VK_D:
				mIsRightPressed = true;
				break;
		}
	}

	@Override
	public void keyReleased(final KeyEvent pKeyEvent) {
		switch (pKeyEvent.getKeyCode()) {
			case KeyEvent.VK_W:
				mIsForwardPressed = false;
				break;
			case KeyEvent.VK_S:
				mIsBackwardPressed = false;
				break;
			case KeyEvent.VK_A:
				mIsLeftPressed = false;
				break;
			case KeyEvent.VK_D:
				mIsRightPressed = false;
				break;
		}
	}
}
