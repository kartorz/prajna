package prajna.models;

import java.util.List;

public class EasyuiDataGrid {
	private int total;
	private List<FileItem> rows;
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<FileItem> getRows() {
		return rows;
	}
	public void setRows(List<FileItem> rows) {
		this.rows = rows;
		this.total = rows.size();
	}
}
