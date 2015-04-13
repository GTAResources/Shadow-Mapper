package nl.shadowlink.shadowmapper.models;

import java.io.File;
import com.google.gson.annotations.SerializedName;
import com.nikhaldimann.inieditor.IniEditor;
import nl.shadowlink.shadowgtalib.utils.Constants.GameType;

/**
 * An object of an installation
 *
 * @author Kilian Steenman (Shadow-Link)
 */
public class Install {
	@SerializedName("path")
	private String mPath;
	@SerializedName("type")
	private GameType mGameType;

	public Install(final String pPath, final GameType pGameType) {
		mPath = pPath;
		mGameType = pGameType;
	}

	/**
	 * Returns the path for the installation
	 *
	 * @return
	 */
	public String getPath() {
		return mPath;
	}

	/**
	 * Sets the path for the installation
	 *
	 * @param path
	 */
	public void setPath(String path) {
		mPath = path;
	}

	/**
	 * Gets the type of this install
	 *
	 * @return
	 */
	public GameType getType() {
		return mGameType;
	}

	/**
	 * Sets the type of this install
	 *
	 * @param type
	 */
	public void setType(final GameType type) {
		mGameType = type;
	}

	/**
	 * Returns TRUE if the path is valid
	 *
	 * @return
	 */
	public boolean isPathValid() {
		File file = new File(mPath);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}
}
