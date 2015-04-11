package nl.shadowlink.shadowmapper.forms;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nl.shadowlink.shadowgtalib.utils.Constants.GameType;
import nl.shadowlink.shadowgtalib.utils.Filter;
import nl.shadowlink.shadowgtalib.utils.Utils;
import nl.shadowlink.shadowmapper.FileManager;
import nl.shadowlink.shadowmapper.Main;
import nl.shadowlink.shadowmapper.constants.Constants;
import nl.shadowlink.shadowmapper.LoadingBar;
import nl.shadowlink.shadowmapper.models.Install;
import nl.shadowlink.shadowmapper.models.InstallsTableModel;
import nl.shadowlink.shadowmapper.models.Settings;
import nl.shadowlink.shadowmapper.utils.EncryptionUtils;
import nl.shadowlink.shadowmapper.utils.GuiUtils;

/**
 * Shows the select form to select the installation the user wants to use
 *
 * @author Kilian Steenman (Shadow-Link)
 */
public class FormSelect implements ListSelectionListener {

	/** Array of exe names of supported games */
	private static final String[] mExeNames = { "gtaiv.exe" };

	private JFrame frame;
	private JTable table;
	private JButton btnAddInstall;
	private JButton btnRemoveInstall;
	private JButton btnSelect;

	/** Settings used in the application */
	private Settings mSettings;

	/**
	 * Create the application.
	 */
	public FormSelect() {
		GuiUtils.setLookAndFeel();
		initialize();
		mSettings = Settings.loadSettings();
		((InstallsTableModel) table.getModel()).setInstalls(mSettings.getInstalls());

		if (EncryptionUtils.isUnlimitedStrenthInstalled()) {
			System.out.println("Installed");
		} else {
			System.out.println("Not installed");
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 290);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		btnSelect = new JButton("Select");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectClicked();
			}
		});
		btnSelect.setBounds(345, 230, 100, 30);
		btnSelect.setEnabled(false);
		frame.getContentPane().add(btnSelect);

		btnAddInstall = new JButton("Add");
		btnAddInstall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				addInstallClicked();
			}
		});
		btnAddInstall.setBounds(240, 230, 100, 30);
		frame.getContentPane().add(btnAddInstall);

		btnRemoveInstall = new JButton("Remove");
		btnRemoveInstall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeInstallClicked();
			}
		});
		btnRemoveInstall.setBounds(135, 230, 100, 30);
		btnRemoveInstall.setEnabled(false);
		frame.getContentPane().add(btnRemoveInstall);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 430, 210);
		frame.getContentPane().add(scrollPane);

		table = new JTable(new InstallsTableModel());
		table.setBounds(10, 10, 430, 210);
		table.getColumnModel().getColumn(0).setPreferredWidth(10);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(200);
		table.getColumnModel().getColumn(3).setPreferredWidth(50);
		table.getColumnModel().getColumn(4).setMinWidth(44);
		table.getSelectionModel().addListSelectionListener(this);
		scrollPane.setViewportView(table);
		frame.setVisible(true);
	}

	/**
	 * Clicked on the add install button
	 */
	private void addInstallClicked() {
		File file = Utils.fileChooser(null, Constants.fileOpen, new Filter(mExeNames, "gtaiv.exe", true));

		if (file != null) {
			Install install = new Install(file.getAbsolutePath(), "", 3);
			mSettings.addInstall(install);
			((InstallsTableModel) table.getModel()).setInstalls(mSettings.getInstalls());
		}
	}

	/**
	 * Clicked on the remove install button
	 */
	private void removeInstallClicked() {
		mSettings.removeInstall(table.getSelectedRow());
		((InstallsTableModel) table.getModel()).setInstalls(mSettings.getInstalls());
	}

	/**
	 * Clicked on the select button
	 */
	private void selectClicked() {
		// TODO: Change to LoadingBar
//		new LoadingBar("D:\\Games\\Rockstar Games\\Grand Theft Auto IV\\", GameType.GTA_IV, new byte[] { 1, 1 }/* findKey(
//																								 * "D:\\Games\\Rockstar Games\\Grand Theft Auto IV\\"
//																								 * ) */);
		// this.set.setVisible(false);
		System.out.println("Select clicked");
	}

	@Override
	public void valueChanged(ListSelectionEvent listSelectionEvent) {
		int selectedRow = listSelectionEvent.getFirstIndex();
		if (selectedRow != -1) {
			btnRemoveInstall.setEnabled(true);
			btnSelect.setEnabled(true);
		} else {
			btnRemoveInstall.setEnabled(false);
			btnSelect.setEnabled(false);
		}
	}
}
