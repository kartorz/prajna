package prajna.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity(name = "DocTree")
public class DocTreeItem {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private int pid;

	private String state;
	private String text;
	
	private boolean checked;
	private String iconCls;
	private String attributes; 
	
	private boolean dirFlag;
	
	private int did;
	private int cid;
	
	@Transient
	private List<DocTreeItem> children = null;
	
	public DocTreeItem(int id, int pid) {
		this.id = id;
		this.pid = pid;	
	}
	
	public DocTreeItem(int id, int pid, String text) {
		this.id = id;
		this.pid = pid;
		this.text = text;
	}
	
	public DocTreeItem() {
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public List<DocTreeItem> getChildren() {
		return children;
	}
	public void setChildren(List<DocTreeItem> children) {
		this.children = children;
	}
	
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	public boolean getChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getDid() {
		return did;
	}

	public void setDid(int did) {
		this.did = did;
	}

	public boolean isDirFlag() {
		return dirFlag;
	}

	public void setDirFlag(boolean dirFlag) {
		this.dirFlag = dirFlag;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}
}
