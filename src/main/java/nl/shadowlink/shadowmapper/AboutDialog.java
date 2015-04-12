package nl.shadowlink.shadowmapper;

import javax.swing.*;
import java.awt.event.*;

public class AboutDialog extends JDialog {
	private JPanel contentPane;

	public AboutDialog() {
		setContentPane(contentPane);
		setModal(true);

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onClose();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClose();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void onClose() {
		dispose();
	}
}
