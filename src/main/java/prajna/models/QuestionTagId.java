package prajna.models;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class QuestionTagId implements Serializable {
	private static final long serialVersionUID = 1L;
	int qid;
	String tag;
	
	public QuestionTagId() {
	}
	
	public QuestionTagId(int qid, String tag) {
		this.qid = qid;
		this.tag = tag;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof QuestionTagId) {
			QuestionTagId other = (QuestionTagId) obj;
			return tag.equalsIgnoreCase(other.tag) && qid == other.qid;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return tag.hashCode();
	}
}
