/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.shadowlink.shadowmapper;

import nl.shadowlink.file_io.ReadFunctions;
import nl.shadowlink.shadowgtalib.dat.GTA_DAT;
import nl.shadowlink.shadowgtalib.ide.IDE;
import nl.shadowlink.shadowgtalib.ide.Item_OBJS;
import nl.shadowlink.shadowgtalib.img.IMG;
import nl.shadowlink.shadowgtalib.ipl.IPL;
import nl.shadowlink.shadowgtalib.ipl.Item_INST;
import nl.shadowlink.shadowgtalib.model.model.Vector3D;
import nl.shadowlink.shadowgtalib.utils.Constants.GameType;
import nl.shadowlink.shadowgtalib.water.Water;
import nl.shadowlink.shadowmapper.constants.Constants;
import java.io.File;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 * FileManager that takes care of managing GTA files<br/>
 * <lu><li>Loading install</li><li>Saving install</li></lu>
 *
 * @author Kilian
 */
public class FileManager {

	/**
	 * Interface used to communicate loading statuses
	 */
	public interface LoadingStatusChangedListener {
		/**
		 * The amount of items that will be loaded
		 *
		 * @param pMaxItemsToLoad
		 *        The maximum amount of items that will be loaded
		 */
		void onMaxItemsToLoadChanged(final int pMaxItemsToLoad);

		/**
		 * The text that identifies what part will be loaded
		 *
		 * @param pLoadingStatusText
		 *        The text that identifies the current loading state
		 */
		void onLoadingStatusTextChanged(final String pLoadingStatusText);

		void onLoadingProgressChanged(final int pLoadingProgress);

		void onLoadingProgressIncreased();

		void onLoadingFailed();

		void onLoadingFinished();

	}

	/**
	 * Listener that listens for loading status changes
	 */
	private LoadingStatusChangedListener mLoadingStatusChangedListener;

	public GTA_DAT mGTADat; // object of the gta.dat file
	public IPL[] mIPLFiles; // objects of the ipl files
	public IDE[] mIDEFiles; // objects of the ide files
	public IMG[] mIMGFiles; // objects of the img files
	public Water[] waters; // objects of the water.dat files
	public IDE vehicles; // object of the vehicles.ide file

	public DefaultListModel modelIPL = new DefaultListModel(); // contains mIPLFiles
	public DefaultListModel modelIDE = new DefaultListModel(); // contains mIDEFiles
	public DefaultListModel modelIPLItems = new DefaultListModel();
	public DefaultListModel modelIDEItems = new DefaultListModel();
	public DefaultComboBoxModel modelVehicles = new DefaultComboBoxModel();

	public int selType = -1;
	public int selParam1 = -1;
	public int selParam2 = -1;

	/**
	 * The encryption key used to decrypt encrypted files
	 */
	private byte[] mEncryptionKey;

	/**
	 * The directory of the install
	 */
	private String mGameDir;

	/**
	 * The GameType of the install
	 */
	private final GameType mGameType;

	/**
	 * Set the LoadingStatusChangedListener
	 *
	 * @param pLoadingStatusChangedListener
	 *        the listener that should be set
	 */
	public void setLoadStatusChangedListener(final LoadingStatusChangedListener pLoadingStatusChangedListener) {
		mLoadingStatusChangedListener = pLoadingStatusChangedListener;
	}

	public FileManager(final String pGameDir, final GameType pGameType, final byte[] pEncryptionKey) {
		mGameDir = pGameDir;
		mGameType = pGameType;
		mEncryptionKey = pEncryptionKey;
	}

	/**
	 * Load the GTA files
	 */
	public void load() {
		setLoadingStatusText("Loading started");
		mGTADat = new GTA_DAT(mGameDir, mGameType);
		vehicles = new IDE(mGameDir + "common/data/vehicles.ide", 3, true);

		final int totalItemsToLoad = mGTADat.ide.size() + mGTADat.img.size() + mGTADat.water.size() + mGTADat.ipl.size();
		setMaxLoadingProgress(totalItemsToLoad);

		mIDEFiles = loadIDEFiles(mGTADat);
		mIMGFiles = loadIMGFiles(mGTADat);
		waters = new Water[mGTADat.water.size()];
		ArrayList<IPL> iplList = new ArrayList();

		loadWPLFiles(iplList);
		loadWater();
		loadBinaryWPLFiles(iplList);

		// Put the vehicle names into a combobox model for later use
		for (int i = 0; i < vehicles.items_cars.size(); i++) {
			this.modelVehicles.addElement(vehicles.items_cars.get(i).modelName);
		}
		loadingFinished();
	}

	/**
	 * Loads the IDE's that are defined in the gta.dat file
	 *
	 * @param pGtaDat
	 *        The gta.dat file that references the .ide files
	 * @return Array of .ide files
	 */
	private IDE[] loadIDEFiles(final GTA_DAT pGtaDat) {
		final IDE[] ideFiles = new IDE[pGtaDat.ide.size()];

		// load IDE files from GTA.dat
		for (int i = 0; i < mGTADat.ide.size(); i++) {
			setLoadingStatusText("<IDE> " + mGTADat.ide.get(i));
			ideFiles[i] = new IDE(mGameDir + mGTADat.ide.get(i), Constants.gIV, true);
			modelIDE.addElement(mGTADat.ide.get(i));
			increaseLoadingProgress();
		}

		return ideFiles;
	}

	/**
	 * Load the IMG files that are defined in the gta.dat
	 *
	 * @param pGtaDat
	 *        The gta.dat file that references the .img files
	 * @return Array of .img files
	 */
	private IMG[] loadIMGFiles(final GTA_DAT pGtaDat) {
		final IMG[] imgFiles = new IMG[pGtaDat.img.size()];

		// load IMG files from GTA.dat
		for (int i = 0; i < pGtaDat.img.size(); i++) {
			setLoadingStatusText("<IMG> " + pGtaDat.img.get(i));
			String line = mGameDir + pGtaDat.img.get(i);
			boolean containsProps = line.endsWith("1");
			line = line.substring(0, line.length() - 1);
			line = line + ".img";
			mIMGFiles[i] = new IMG(line, mGameType, mEncryptionKey, true, containsProps);
			increaseLoadingProgress();
		}

		return imgFiles;
	}

	private void loadWPLFiles(final ArrayList<IPL> pIplList) {
		// load WPL files from GTA.dat
		for (int i = 0; i < mGTADat.ipl.size(); i++) {
			setLoadingStatusText("<IPL> " + mGTADat.ipl.get(i));
			IPL tempIPL = new IPL(mGameDir + mGTADat.ipl.get(i), Constants.gIV, true);
			pIplList.add(tempIPL);
			modelIPL.addElement(mGTADat.ipl.get(i));
			increaseLoadingProgress();
		}
	}

	private void loadBinaryWPLFiles(final ArrayList<IPL> pIplList) {
		// count total wpl files in mIMGFiles
		int imgWPLCount = 0;
		for (int i = 0; i < mIMGFiles.length; i++) {
			imgWPLCount += mIMGFiles[i].wplCount;
		}

		// Restart the progress
		setLoadingProgress(0);
		setMaxLoadingProgress(imgWPLCount);

		// load WPL files from IMG files
		for (int i = 0; i < mIMGFiles.length; i++) {
			if (mIMGFiles[i].wplCount > 0) {
				ReadFunctions rf = new ReadFunctions(); // open the img file
				if (rf.openFile(mIMGFiles[i].getFileName())) {
					for (int j = 0; j < mIMGFiles[i].Items.size(); j++) {
						if (mIMGFiles[i].Items.get(j).getName().toLowerCase().endsWith(".wpl")) {
							rf.seek(mIMGFiles[i].Items.get(j).getOffset());
							IPL tempIPL = new IPL(rf, Constants.gIV, true, mIMGFiles[i], mIMGFiles[i].Items.get(j));
							tempIPL.setFileName(mIMGFiles[i].Items.get(j).getName());
							setLoadingStatusText("<WPL> " + mIMGFiles[i].Items.get(j).getName());
							pIplList.add(tempIPL);
							modelIPL.addElement(mIMGFiles[i].Items.get(j).getName());
							increaseLoadingProgress();
						}
					}
				}
				rf.closeFile();
			}
		}

		// Put the wpl files into an array
		mIPLFiles = new IPL[pIplList.size()];
		for (int i = 0; i < pIplList.size(); i++) {
			pIplList.get(i).lodWPL = i;
			mIPLFiles[i] = pIplList.get(i);
		}
	}

	private void loadWater() {
		// load water.dat files from gta.dat
		for (int i = 0; i < mGTADat.water.size(); i++) {
			setLoadingStatusText("<WATER> " + mGTADat.ipl.get(i));
			waters[i] = new Water(mGameDir + mGTADat.water.get(i), Constants.gIV);
			increaseLoadingProgress();
		}
	}

	private void setMaxLoadingProgress(final int pMaxLoadingProgress) {
		if (mLoadingStatusChangedListener != null) {
			mLoadingStatusChangedListener.onMaxItemsToLoadChanged(pMaxLoadingProgress);
		}
	}

	/**
	 * Updates the loading status
	 *
	 * @param pLoadingStatusText
	 *        The status text of what we are currently loading
	 */
	private void setLoadingStatusText(final String pLoadingStatusText) {
		if (mLoadingStatusChangedListener != null) {
			mLoadingStatusChangedListener.onLoadingStatusTextChanged(pLoadingStatusText);
		}
	}

	/**
	 * Called when loading progress increased by 1
	 */
	private void increaseLoadingProgress() {
		if (mLoadingStatusChangedListener != null) {
			mLoadingStatusChangedListener.onLoadingProgressIncreased();
		}
	}

	/**
	 * Set the loading progress
	 *
	 * @param pLoadingProgress
	 *        The loading progress
	 */
	private void setLoadingProgress(final int pLoadingProgress) {
		if (mLoadingStatusChangedListener != null) {
			mLoadingStatusChangedListener.onLoadingProgressChanged(pLoadingProgress);
		}
	}

	/**
	 * Called when loading is finished
	 */
	private void loadingFinished() {
		if (mLoadingStatusChangedListener != null) {
			mLoadingStatusChangedListener.onLoadingFinished();
		}
	}

	public void save() {
		if (mGTADat.changed) {
			System.out.println("Saving gta.dat");
			mGTADat.save();
		}
		for (int i = 0; i < mIDEFiles.length; i++) {
			if (mIDEFiles[i].changed) {
				mIDEFiles[i].save();
				mIDEFiles[i].changed = false;
				System.out.println("Saving ide " + i);
			}
		}
		for (int i = 0; i < mIPLFiles.length; i++) {
			if (mIPLFiles[i].changed) {
				mIPLFiles[i].save();
				mIPLFiles[i].changed = false;
				System.out.println("Saving ipl " + i);
			}
		}
		for (int i = 0; i < mIMGFiles.length; i++) {
			if (mIMGFiles[i].changed) {
				mIMGFiles[i].save();
				mIMGFiles[i].changed = false;
				System.out.println("Saving img " + i);
			}
		}
	}

	public DefaultListModel getSaveModel() {
		DefaultListModel saveModel = new DefaultListModel();

		if (mGTADat.changed)
			saveModel.addElement("gta.dat");

		System.out.println(mIDEFiles.length);
		System.out.println(mIPLFiles.length);
		System.out.println(mIMGFiles.length);
		for (int i = 0; i < mIDEFiles.length; i++) {
			if (mIDEFiles[i].changed) {
				saveModel.addElement(mIDEFiles[i].getFileName());
			}
		}
		for (int i = 0; i < mIPLFiles.length; i++) {
			if (mIPLFiles[i].changed) {
				saveModel.addElement(mIPLFiles[i].getFileName());
			}
		}
		for (int i = 0; i < mIMGFiles.length; i++) {
			if (mIMGFiles[i].changed) {
				saveModel.addElement(mIMGFiles[i].getFileName());
			}
		}

		return saveModel;
	}

	public void addIPLItem(String name, int iplID, Vector3D pos) {
		Item_INST iplItem = new Item_INST(Constants.gIV);
		iplItem.name = name;
		iplItem.interior = 0;
		iplItem.lod = -1;
		iplItem.position.x = pos.x;
		iplItem.position.y = 0 - pos.z;
		iplItem.position.z = pos.y;
		mIPLFiles[iplID].items_inst.add(iplItem);
		mIPLFiles[iplID].changed = true;
		modelIPLItems.addElement(name);
		// addHashToIni(name);
		iplItem = null;
	}

	public int addIDEItem(Item_OBJS tmp, int ideID) {
		mIDEFiles[ideID].items_objs.add(tmp);
		mIDEFiles[ideID].changed = true;
		modelIDEItems.addElement(tmp.modelName);
		return mIDEFiles[ideID].items_objs.size() - 1;
	}

	public void updateIDEItemList(int ideID, int type) {
		modelIDEItems.clear();
		switch (type) {
			case 0:
				for (int i = 0; i < mIDEFiles[ideID].items_objs.size(); i++) {
					modelIDEItems.addElement(mIDEFiles[ideID].items_objs.get(i).modelName);
				}
				break;
		}
	}

	public void updateIPLItemList(int iplID, int type) {
		modelIPLItems.clear();
		switch (type) {
			case 0:
				for (int i = 0; i < mIPLFiles[iplID].items_inst.size(); i++) {
					modelIPLItems.addElement(mIPLFiles[iplID].items_inst.get(i).name);
				}
				break;
			case 1:
				for (int i = 0; i < mIPLFiles[iplID].items_grge.size(); i++) {
					modelIPLItems.addElement(mIPLFiles[iplID].items_grge.get(i).name);
				}
				break;
			case 2:
				for (int i = 0; i < mIPLFiles[iplID].items_cars.size(); i++) {
					if (!mIPLFiles[iplID].items_cars.get(i).name.equals("")) {
						modelIPLItems.addElement(mIPLFiles[iplID].items_cars.get(i).name);
					} else {
						modelIPLItems.addElement("Random");
					}
				}
				break;
			case 3:
				for (int i = 0; i < mIPLFiles[iplID].items_cull.size(); i++) {
					modelIPLItems.addElement(mIPLFiles[iplID].items_cull.get(i).name);
				}
				break;
			case 4:
				for (int i = 0; i < mIPLFiles[iplID].items_strbig.size(); i++) {
					modelIPLItems.addElement(mIPLFiles[iplID].items_strbig.get(i).modelName);
				}
				break;
			case 5:
				for (int i = 0; i < mIPLFiles[iplID].items_lcul.size(); i++) {
					modelIPLItems.addElement(mIPLFiles[iplID].items_lcul.get(i).name1);
				}
				break;
			case 6:
				for (int i = 0; i < mIPLFiles[iplID].items_zone.size(); i++) {
					modelIPLItems.addElement(mIPLFiles[iplID].items_zone.get(i));
				}
				break;
			case 7:
				for (int i = 0; i < mIPLFiles[iplID].items_blok.size(); i++) {
					modelIPLItems.addElement(mIPLFiles[iplID].items_blok.get(i));
				}
				break;
		}
	}

	public void addNewIDE(File file) {
		if (file != null) {
			if (file.exists()) {
				JOptionPane.showMessageDialog(null, "File already exists");
			} else {
				IDE tempIDE = new IDE(file.getAbsolutePath(), Constants.gIV, true);
				tempIDE.changed = true;
				IDE[] tempIDES = new IDE[mIDEFiles.length + 1];
				for (int i = 0; i < mIDEFiles.length; i++) {
					tempIDES[i] = mIDEFiles[i];
				}
				tempIDES[mIDEFiles.length] = tempIDE;
				mIDEFiles = tempIDES;
				modelIDE.addElement(file.getName());
				tempIDES = null;
				tempIDE = null;
			}
		}
	}

	public void addNewIPL(File file) {
		if (file != null) {
			if (file.exists()) {
				JOptionPane.showMessageDialog(null, "File already exists");
			} else {
				IPL tempIPL = new IPL(file.getAbsolutePath(), Constants.gIV, false);
				tempIPL.changed = true;
				IPL[] tempIPLS = new IPL[mIPLFiles.length + 1];
				for (int i = 0; i < mIPLFiles.length; i++) {
					tempIPLS[i] = mIPLFiles[i];
				}
				tempIPLS[mIPLFiles.length] = tempIPL;
				mIPLFiles = tempIPLS;
				// checkList.setIPLS(tempIPLS);
				modelIPL.addElement(file.getName());
				tempIPLS = null;
				tempIPL = null;
				String fixedIplPath = file.getPath().toLowerCase().replace(mGameDir.toLowerCase(), "");
				mGTADat.ipl.add(fixedIplPath);
				mGTADat.changed = true;
			}
		}
	}

	public void setSelection(int selType, int selParam1, int selParam2) {
		if (this.selType != -1) {
			switch (this.selType) {
				case Constants.pickMap:
					mIPLFiles[this.selParam1].items_inst.get(this.selParam2).selected = false;
					break;
				case Constants.pickWater:
					waters[0].planes.get(this.selParam1).selected = false;
					break;
				case Constants.pickCar:
					mIPLFiles[this.selParam1].items_cars.get(this.selParam2).selected = false;
					break;
				default:
					System.out.println("--Something went wrong--");
					System.out.println("SelType: " + selType);
					System.out.println("SelParam1: " + selParam1);
					System.out.println("SelParam2: " + selParam2);
			}
		}
		this.selType = selType;
		this.selParam1 = selParam1;
		this.selParam2 = selParam2;
		if (this.selType != -1) {
			switch (selType) {
				case Constants.pickMap:
					mIPLFiles[selParam1].items_inst.get(selParam2).selected = true;
					break;
				case Constants.pickWater:
					waters[0].planes.get(selParam1).selected = true;
					break;
				case Constants.pickCar:
					mIPLFiles[selParam1].items_cars.get(selParam2).selected = true;
					break;
				default:
					System.out.println("--Something went wrong--");
					System.out.println("SelType: " + selType);
					System.out.println("SelParam1: " + selParam1);
					System.out.println("SelParam2: " + selParam2);
			}
		}
	}

	public void addIMG(String file) {
		IMG tempIMG = new IMG(file, mGameType, null, false, true);
		tempIMG.changed = true;
		IMG[] tempIMGS = new IMG[mIMGFiles.length + 1];
		for (int i = 0; i < mIMGFiles.length; i++) {
			tempIMGS[i] = mIMGFiles[i];
		}
		tempIMGS[mIMGFiles.length] = tempIMG;
		mIMGFiles = tempIMGS;
		tempIMGS = null;
		tempIMG = null;
	}

	public String getGameDir() {
		return mGameDir;
	}
}
