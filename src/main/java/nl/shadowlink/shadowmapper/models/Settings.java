package nl.shadowlink.shadowmapper.models;

import java.io.*;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.sun.media.jfxmedia.logging.Logger;
import nl.shadowlink.shadowgtalib.utils.Constants.GameType;
import nl.shadowlink.shadowmapper.utils.typeadapters.GameTypeSerializer;

/**
 * Settings class contains settings for the app.<br/>
 * - Installs<br/>
 * <p/>
 * Created by Kilian 26/11/2014 19:48
 */
public class Settings {

	/** FileName of the settings file */
	private static final String FILE_NAME_SETTINGS = "settings.json";

	/** Custom Gson deserializer used to deserialze the settings.json */
	private static final Gson GSON_SERIALIZER = new GsonBuilder().registerTypeAdapter(GameType.class, new GameTypeSerializer()).create();

	/** Array of installs */
	@SerializedName("install")
	private ArrayList<Install> mInstalls = new ArrayList<>();

	/**
	 * Returns an ArrayList of installs
	 * 
	 * @return ArrayList of install
	 */
	public ArrayList<Install> getInstalls() {
		return mInstalls;
	}

	/**
	 * Adds a new install and saves the settings
	 * 
	 * @param pInstall
	 *        The install that should be added
	 */
	public void addInstall(final Install pInstall) {
		mInstalls.add(pInstall);
		saveSettings();
	}

	/**
	 * Removes an install from the saved installs
	 * 
	 * @param pInstallIndex
	 *        The index of the install that should be removed
	 */
	public void removeInstall(final int pInstallIndex) {
		mInstalls.remove(pInstallIndex);
		saveSettings();
	}

	/**
	 * Saves the current settings to the settings file
	 */
	private void saveSettings() {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(FILE_NAME_SETTINGS));
			GSON_SERIALIZER.toJson(this, bw);
		} catch (Exception pException) {
			System.out.println("Unable to save Settings file: " + pException.getMessage());
			pException.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Loads a settings file
	 * 
	 * @return Settings loaded from file
	 */
	public static Settings loadSettings() {
		File file = new File(FILE_NAME_SETTINGS);

		// Make sure the settings file exists before reading it
		if (file.exists()) {
			BufferedReader br = null;
			try {
				FileReader fileReader = new FileReader(FILE_NAME_SETTINGS);
				br = new BufferedReader(fileReader);
				return GSON_SERIALIZER.fromJson(br, Settings.class);
			} catch (FileNotFoundException pException) {
				Logger.logMsg(Logger.ERROR, "Settings file doesn't exist: " + pException.getMessage());
			} catch (Exception pException) {
				Logger.logMsg(Logger.ERROR, "Unable to load settings: " + pException.getMessage());
			} finally {
				try {
					if (br != null) {
						br.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// If no settings are available, create empty settings object
		return new Settings();
	}
}
