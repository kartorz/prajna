package prajna.models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity(name = "UserMessages")
public class UserMessage implements CommentItem {
	public static final int LEN_TEXT = 512;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id=0; 

	private String author  = "";
	private String account = "";
	private String text    = "";
	private Timestamp   cdate;
	@Transient String cdateStr = "";
	@Transient int did;
	
	public UserMessage() {
		cdate = new Timestamp(System.currentTimeMillis());
		cdateStr = new SimpleDateFormat("YYYY-MM-dd hh:mm").format(cdate);

	}
	
	public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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
	
   
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
   
	public String getText() {
        return text;
    }
    
    public void setText(String text ) {
        this.text = text;
    }
    
    public String getCdateStr() {
    	return cdateStr;
    }

	@Override
	public int getDid() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDid(int pid) {
		// TODO Auto-generated method stub
		
	}
}
