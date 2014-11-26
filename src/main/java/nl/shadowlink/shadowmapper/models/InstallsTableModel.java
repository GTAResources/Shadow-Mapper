package nl.shadowlink.shadowmapper.models;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Installs table model
 * 
 * @author Kilian Steenman (Shadow-Link)
 */
public class InstallsTableModel extends AbstractTableModel {
	private static String[] sTableColumnNames = { "Type", "Name", "Path", "Version", "Valid" };

	/** Array of install that should be shown in this table */
	private ArrayList<Install> mInstalls;

	@Override
	public int getColumnCount() {
		return sTableColumnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		return sTableColumnNames[column];
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
				return mInstalls.get(row).getType();
			case 1:
				return mInstalls.get(row).getName();
			case 2:
				return mInstalls.get(row).getPath();
			case 3:
				return mInstalls.get(row).getVersionString();
			case 4:
				return mInstalls.get(row).isPathValid();
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
