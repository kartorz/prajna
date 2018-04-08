package prajna.models;


import javax.persistence.Entity;
import javax.persistence.Transient;

import javax.persistence.Id;

@Entity(name = "tags")
public class TagItem {
	@Id private String tag;
	@Transient private boolean selected = true;
	
	public TagItem() {}
	
	public TagItem(String tag) {
		this.tag =  tag;
	}
	
	public TagItem(String tag, boolean selected) {
		this.tag =  tag;
		this.selected = selected;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public String getTag() {
		return tag;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean getSelected() {
		return selected;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TagItem) {
			TagItem otherTag = (TagItem) obj;
			return tag.equalsIgnoreCase(otherTag.getTag());
		}
		return false;
	 }
	
	 @Override
	 public int hashCode() {
		 //BigInteger bigInt = new BigInteger(tag.getBytes());
		 //return bigInt.intValue();

		return getTag().hashCode();
	 }
}
