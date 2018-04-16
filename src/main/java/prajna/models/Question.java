package prajna.models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity(name = "questions")
public class Question {
	private static int INVITEES_MAX = 128;
	private static final Logger logger = LogManager.getLogger(Question.class.getName() );
	   
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int qid = 0;
	
    private String 		account = "";
    private String 		title = "";
	private String 		author = "";
	private String 		text = "";
	private String 		category = "";
	private Timestamp   cdate;
	private String   	invitees = "";
	@Transient String 	cdateStr = "";
	@Transient String 	tags = "";

	public Question() {
		cdate = new Timestamp(System.currentTimeMillis());
		cdateStr = new SimpleDateFormat("YYYY-MM-dd hh:mm").format(cdate);
	}
	
	public int getQid() {
		return qid;
	}
	
	public void setQid(int qid) {
		this.qid = qid;
	}

	public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
	
    public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	} 
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCdateStr() {
		return cdateStr;
	}
	public Timestamp getCdate() {
		return cdate;
	}
	
	public void setCdate(Timestamp cdate) {
		this.cdate = cdate;
		cdateStr = new SimpleDateFormat("YYYY-MM-dd hh:mm").format(cdate);
	}
	
	public String getTags() {
		return tags;
	}
	
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public String getInvitees() {
		if (invitees == null) {
			logger.info("invitees == null");
			invitees = "";
		}
		return invitees;
	}
	
	public void setInvitees(String invitees) {
		this.invitees = invitees;
	}
	
	public String addInvitee(String invitee) {
		if (invitees == null)
			invitees = "";
    	if (invitees.isEmpty()) {
    		invitees = invitee;
    	} else {
    		invitees += "," + invitee;
    		while (invitees.length() > INVITEES_MAX) {
    			invitees = invitees.substring(invitees.indexOf(',') + 1);
    		}
    	}
    	
    	return invitees;
	}
	
}
