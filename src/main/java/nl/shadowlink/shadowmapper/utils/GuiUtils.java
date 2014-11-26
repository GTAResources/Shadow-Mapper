package nl.shadowlink.shadowmapper.utils;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GuiUtils {

	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException ex) {
//			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
//			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
//			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedLookAndFeelException ex) {
			System.out.println("Can't find system LookAndFeel\nSetting LookAndFeel to crossplatform");
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception ex1) {
				System.out.println("Unable to set the LookAndFeel");
			}
		}
	}
}
