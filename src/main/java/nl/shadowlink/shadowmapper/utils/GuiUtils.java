package nl.shadowlink.shadowmapper.utils;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GuiUtils {

	/** Tag used for logging */
	private static final String LOG_TAG = "GuiUtils";

	/**
	 * Sets the look and feel of the GUI.<br/>
	 * By default it tries to use the OS LookAndFeel
	 */
	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ex) {
			Logger.getLogger(LOG_TAG).log(Level.SEVERE, null, ex);
		}
	}
}
