package prajna.models;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity(name = "QuestionTags")
public class QuestionTag {
	@EmbeddedId  QuestionTagId id;
	
	//@MapsId("qid")
	//@ManyToOne Question question;
	public QuestionTag() {}
	
	public QuestionTag(int qid, String tag) {
		this.id = new QuestionTagId(qid, tag);
	}
	
	public void setId(QuestionTagId id) {
		this.id = id;
	}
}
