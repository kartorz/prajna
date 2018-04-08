package prajna.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity(name = "Menutree")
public class MenuItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private int level;
	private int parent;
	private String name;
	
	public MenuItem() {
		
	}
	
	public int getId() {
		return id;
	}

    public void setId(int id) {
		this.id = id;
	}

    public void setLevel(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setParent(int parent) {
		this.parent = parent;
	}
	
	public int getParent() {
		return parent;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
