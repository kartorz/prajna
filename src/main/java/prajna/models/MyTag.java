package prajna.models;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class MyTag {
	@EmbeddedId AccountTagId  id;
	
	public MyTag() {
	}
	
	public MyTag(AccountTagId id) {
		this.id = id;
	}
	
	public void setId(AccountTagId id) {
		this.id = id;
	}
	
	public void setAccount(String account) {
		this.id.account = account;
	}
	
	public String getAccount() {
		return this.id.account;
	}
	
	public void setTag(String tag) {
		this.id.tag = tag;
	}
	
	public String getTag() {
		return this.id.tag;
	}
}
