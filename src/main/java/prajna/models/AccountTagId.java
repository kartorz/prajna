package prajna.models;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class AccountTagId implements Serializable {
	private static final long serialVersionUID = 1L;
	String account;
	String tag;

	public AccountTagId() {
	}
	
	public AccountTagId(String account, String tag) {
		this.account = account;
		this.tag = tag;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AccountTagId) {
			AccountTagId other = (AccountTagId) obj;
			return tag.equalsIgnoreCase(other.tag) && account.equals(other.account);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return tag.hashCode();
	}
}
