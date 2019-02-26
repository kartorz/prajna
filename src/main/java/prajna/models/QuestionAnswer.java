package prajna.models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity(name = "QuestionAnswers")
public class QuestionAnswer implements CommentItem {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id=0; 

	private int    qid     = 0;
	private int    parent  = 0;
	private String author  = "";
	private String account = "";
	private String text    = "";
	private int    score   = 0;
	private Timestamp   cdate;
	//@Transient String cdateStr = "";

	public QuestionAnswer() {
		cdate = new Timestamp(System.currentTimeMillis());
	}

	public QuestionAnswer(int qid, String text, String account) {
		this.qid = qid;
		this.text = text;
		this.account = account;
		
		cdate = new Timestamp(System.currentTimeMillis());
	}
	
	public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    
	public Timestamp getCdate() {
		return cdate;
	}
	
	public void setCdate(Timestamp cdate) {
		this.cdate = cdate;
	}
	
    public int getScore() {
        return score;
    }
    public void getScore(int score) {
        this.score = score;
    }
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
    
	public void setParent(int parent) {
		this.parent = parent;
	}
	
	public int getParent() {
		return parent;
	}
   
	public String getText() {
        return text;
    }
    
    public void setText(String text ) {
        this.text = text;
    }
    
    public String getCdateStr() {
    	return new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(cdate);
    }

	@Override
	public int getDid() {
		return qid;
	}

	@Override
	public void setDid(int did) {
		qid = did;
	}
}
