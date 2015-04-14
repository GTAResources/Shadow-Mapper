package nl.shadowlink.shadowmapper.models;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Installs table model
 * 
 * @author Kilian Steenman (Shadow-Link)
 */
public class InstallsTableModel extends AbstractTableModel {

	/** Array of Strings used for the column names */
	private static String[] COLUMN_NAMES = { "Type", "Path" };

	/** Array of install that should be shown in this table */
	private ArrayList<Install> mInstalls;

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	@Override
	public int getRowCount() {
		if (mInstalls != null) {
			return mInstalls.size();
		}
		return 0;
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
			case 0:
				return mInstalls.get(row).getType().getGameName();
			case 1:
				return mInstalls.get(row).getPath();
			default:
				return "-";
		}
	}

	/**
	 * Sets the installs
	 * 
	 * @param pInstalls
	 *        The installs to set
	 */
	public void setInstalls(ArrayList<Install> pInstalls) {
		mInstalls = pInstalls;
		fireTableStructureChanged();
	}
}
