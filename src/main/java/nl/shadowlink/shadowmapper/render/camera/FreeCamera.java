package nl.shadowlink.shadowmapper.render.camera;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Camera used for free camera movement<br/>
 *
 * @author kilian<br/>
 * @date 18 Apr 2015.
 */
public class FreeCamera extends Camera implements KeyListener, MouseMotionListener, MouseListener {

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

	/** position of the mouse in the previous frame */
	private Point mOldMousePosition = new Point(0, 0);

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

	@Override
	public void mouseClicked(final MouseEvent pMouseEvent) {
		// Do nothing
	}

	@Override
	public void mousePressed(final MouseEvent pMouseEvent) {
		mOldMousePosition = pMouseEvent.getLocationOnScreen();
	}

	@Override
	public void mouseReleased(final MouseEvent pMouseEvent) {
		// Do nothing
	}

	@Override
	public void mouseEntered(final MouseEvent pMouseEvent) {
		// Do nothing
	}

	@Override
	public void mouseExited(final MouseEvent pMouseEvent) {
		// Do nothing
	}

	@Override
	public void mouseDragged(final MouseEvent pMouseEvent) {
		try {
			final Point newMousePosition = pMouseEvent.getLocationOnScreen();

			float angleY = (float) (mOldMousePosition.x - newMousePosition.x) / 500;
			float angleZ = (float) (mOldMousePosition.y - newMousePosition.y) / 500;

			double viewY = getViewY();

			setViewY((float) (viewY + angleZ));

			if ((getViewY() - getPosY()) > 8) {
				setViewY((getPosY() + 8));
			}
			if ((getViewY() - getViewY()) < -8) {
				setViewY(getPosY() - 8);
			}

			rotateView(-angleY);

			// Move mouse back to old position
			new Robot().mouseMove(mOldMousePosition.x, mOldMousePosition.y);

		} catch (final AWTException ex) {
			Logger.getGlobal().log(Level.SEVERE, "Error moving mouse " + ex.toString());
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		// Do nothing
	}
}
