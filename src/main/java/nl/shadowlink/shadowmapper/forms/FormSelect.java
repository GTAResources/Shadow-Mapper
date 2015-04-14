package nl.shadowlink.shadowmapper.forms;

import java.io.File;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import nl.shadowlink.shadowgtalib.utils.Constants.GameType;
import nl.shadowlink.shadowgtalib.utils.EncryptionUtils;
import nl.shadowlink.shadowgtalib.utils.Filter;
import nl.shadowlink.shadowgtalib.utils.Utils;
import nl.shadowlink.shadowmapper.FileManager;
import nl.shadowlink.shadowmapper.FileManager.LoadingStatusChangedListener;
import nl.shadowlink.shadowmapper.constants.Constants;
import nl.shadowlink.shadowmapper.models.Install;
import nl.shadowlink.shadowmapper.models.InstallsTableModel;
import nl.shadowlink.shadowmapper.models.Settings;
import nl.shadowlink.shadowmapper.utils.GuiUtils;

/**
 * Shows the select form to select the installation the user wants to use
 *
 * @author Kilian Steenman (Shadow-Link)
 */
public class FormSelect extends JDialog implements ListSelectionListener, LoadingStatusChangedListener {

	/** Array of exe names of supported games */
	private static final String[] EXE_NAMES = { "gtaiv.exe" };

	private JFrame frame;
	private JTable mTableInstalls;
	private JButton mButtonAddInstall;
	private JButton mButtonRemoveInstall;
	private JButton mButtonSelectInstall;
	private JProgressBar mProgressBar;

	/** Settings used in the application */
	private Settings mSettings;

	/** Listener for Select callbacks */
	private final SelectCallbacks mSelectCallbacksListener;

	public interface SelectCallbacks {
		void onInstallLoaded(final FileManager pFileManager);
	}

	/**
	 * Create the application.
	 */
	public FormSelect(final SelectCallbacks pSelectCallbacksListener) {
		mSelectCallbacksListener = pSelectCallbacksListener;
		GuiUtils.setLookAndFeel();
		initialize();

		// Load saved settings
		mSettings = Settings.loadSettings();
		((InstallsTableModel) mTableInstalls.getModel()).setInstalls(mSettings.getInstalls());

		// If there is at least 1 install pre-select it
		if (mSettings.getInstalls().size() > 0) {
			// TODO: Pre-select row
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 320);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);

		mButtonSelectInstall = new JButton("Select");
		mButtonSelectInstall.addActionListener(e -> selectClicked());
		mButtonSelectInstall.setBounds(345, 230, 100, 30);
		mButtonSelectInstall.setEnabled(false);
		frame.getContentPane().add(mButtonSelectInstall);

		mButtonAddInstall = new JButton("Add");
		mButtonAddInstall.addActionListener(actionEvent -> addInstallClicked());
		mButtonAddInstall.setBounds(240, 230, 100, 30);
		frame.getContentPane().add(mButtonAddInstall);

		mButtonRemoveInstall = new JButton("Remove");
		mButtonRemoveInstall.addActionListener(e -> removeInstallClicked());
		mButtonRemoveInstall.setBounds(135, 230, 100, 30);
		mButtonRemoveInstall.setEnabled(false);
		frame.getContentPane().add(mButtonRemoveInstall);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 430, 210);
		frame.getContentPane().add(scrollPane);

		mTableInstalls = new JTable(new InstallsTableModel());
		mTableInstalls.setBounds(10, 10, 430, 210);
		mTableInstalls.getSelectionModel().addListSelectionListener(this);
		scrollPane.setViewportView(mTableInstalls);

		mProgressBar = new JProgressBar();
		mProgressBar.setEnabled(false);
		mProgressBar.setStringPainted(true);
		mProgressBar.setBounds(10, 260, 430, 30);
		frame.getContentPane().add(mProgressBar);

		frame.setVisible(true);
	}

	/**
	 * Clicked on the add install button
	 */
	private void addInstallClicked() {
		File file = Utils.fileChooser(null, Constants.fileOpen, new Filter(EXE_NAMES, "gtaiv.exe", true));

		if (file != null) {
			final String filePath = file.getParentFile().getAbsolutePath() + File.separator;
			Install install = new Install(filePath, GameType.GTA_IV);
			mSettings.addInstall(install);
			((InstallsTableModel) mTableInstalls.getModel()).setInstalls(mSettings.getInstalls());
		}
	}

	/**
	 * Clicked on the remove install button
	 */
	private void removeInstallClicked() {
		mSettings.removeInstall(mTableInstalls.getSelectedRow());
		((InstallsTableModel) mTableInstalls.getModel()).setInstalls(mSettings.getInstalls());
	}

	/**
	 * Clicked on the select button
	 */
	private void selectClicked() {
		Install selectedInstall = getSelectedInstall();

		// If the path to the install is invalid notify the user
		if (!selectedInstall.isPathValid()) {
			JOptionPane.showMessageDialog(frame, "Path to " + selectedInstall.getType().getGameName() + " is invalid", "Unable to load install",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// If the game is GTA IV the unlimited strength encryption should be installed
		if (selectedInstall.getType() == GameType.GTA_IV && !EncryptionUtils.isUnlimitedStrengthInstalled()) {
			JOptionPane.showMessageDialog(frame, "Unlimited strength is not installed",
					"Unlimited strength is not installed for JRE " + System.getProperty("java.version"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		mButtonAddInstall.setEnabled(false);
		mButtonRemoveInstall.setEnabled(false);
		mButtonSelectInstall.setEnabled(false);
		mProgressBar.setEnabled(true);
		mTableInstalls.setEnabled(false);

		FileManager fileManager = new FileManager(selectedInstall);
		fileManager.setLoadStatusChangedListener(this);
		Thread queryThread = new Thread() {
			public void run() {
				fileManager.load();
			}
		};
		queryThread.start();
	}

	@Override
	public void valueChanged(ListSelectionEvent listSelectionEvent) {
		int selectedRow = listSelectionEvent.getFirstIndex();
		if (selectedRow != -1) {
			mButtonRemoveInstall.setEnabled(true);
			mButtonSelectInstall.setEnabled(true);
		} else {
			mButtonRemoveInstall.setEnabled(false);
			mButtonSelectInstall.setEnabled(false);
		}
	}

	@Override
	public void onMaxItemsToLoadChanged(final int pMaxItemsToLoad) {
		mProgressBar.setMinimum(0);
		mProgressBar.setValue(0);
		mProgressBar.setMaximum(pMaxItemsToLoad);
	}

	@Override
	public void onLoadingStatusTextChanged(final String pLoadingStatusText) {
		mProgressBar.setString(pLoadingStatusText);
	}

	@Override
	public void onLoadingProgressChanged(final int pLoadingProgress) {
		mProgressBar.setValue(pLoadingProgress);
	}

	@Override
	public void onLoadingProgressIncreased() {
		mProgressBar.setValue(mProgressBar.getValue() + 1);
	}

	@Override
	public void onLoadingFailed() {
		mButtonRemoveInstall.setEnabled(true);
		mButtonAddInstall.setEnabled(true);
		mButtonSelectInstall.setEnabled(true);
		mTableInstalls.setEnabled(true);
		mProgressBar.setEnabled(false);
		mProgressBar.setValue(0);

		// TODO: Show failed
	}

	@Override
	public void onLoadingFinished(final FileManager pFileManager) {
		// TODO: Pass the FileManager back to MainForm
		mProgressBar.setString("Loading finished");

		if (mSelectCallbacksListener != null) {
			mSelectCallbacksListener.onInstallLoaded(pFileManager);
		}
	}

	private Install getSelectedInstall() {
		return mSettings.getInstalls().get(mTableInstalls.getSelectedRow());
	}
}
