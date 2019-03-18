package prajna.models;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class FileItem {
	public static String LEAF_FOLDER_ICON = "icon-folder";
	private int id;
	private String text;  //compatible easyui tree
	private String state;
	private String iconCls;
	private String size;
	private long lastMdate;
	private String mdate;
	private String path;
	private static int idCounter=1;
	
	private List<FileItem> children = new ArrayList<FileItem>();
	
	public FileItem(File f) {
		this.text = f.getName();
		this.id = idCounter++;
		this.size = String.valueOf((double)(f.length()/(1024*1024))) + "m";
		this.lastMdate = f.lastModified() ;
		mdate = new SimpleDateFormat("YYYY-MM-dd")
					.format(new Timestamp(lastMdate));

	}
	
	public FileItem(String name) {
		this.id = idCounter++;
		this.text = name;
	}
	
	public FileItem() {
	}

	public static void resetId() {
		idCounter = 1;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public List<FileItem> getChildren() {
		return children;
	}
	public void setChildren(List<FileItem> children) {
		this.children = children;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getMdate() {
		return mdate;
	}

	public void setMdate(String mdate) {
		this.mdate = mdate;
	}

	public long getLastMdate() {
		return lastMdate;
	}

	public void setLastMdate(long lastMdate) {
		this.lastMdate = lastMdate;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
