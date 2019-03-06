package prajna.models;

//import java.io.Reader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import prajna.repos.projection.DocOpaque;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity(name = "Docs")
public class Doc {
	private static final Logger logger = LogManager.getLogger(Doc.class.getName());

	public static final int BRIEF_STRLEN = 100;
	public static final int CID_MAINPAGE = 1;
	static public String[] CidToString = {"No Category", "主页", "编程", "Linux", "Android", "电子文档"};  // cid start from 1
	public static final int CID_EDOC = 5;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String title;
	private String author;
	private String email;
	private String category;
	private String brief;
	private boolean repost = false;
	private boolean onTop = true;
	private Timestamp cdate;
	private Timestamp mdate;
	private int score = 0;
	private int positive = 0;
	private int negative = 0;
	private int cid = 0;
	private int resid = 0;
	private int views = 0;
	private String text = "";
	private String account = "";
	private String url = "";
	
	public Doc() {
		cdate = new Timestamp(System.currentTimeMillis());
		mdate = new Timestamp(System.currentTimeMillis());
	}

	public Doc(Draft draft) {
		this();
		
		this.id = draft.getDid();
		this.title = draft.getTitle();
		//this.category =  draft.getCategory();
		this.repost = draft.getRepost();
		this.account = draft.getAccount();
		this.cid = draft.getCid();
		this.resid = draft.getResid();
		this.text = draft.getText();
		this.author = draft.getAuthor();
		this.category = CidToString[cid];
		this.onTop = draft.getOnTop();
		this.url = draft.getUrl();
		this.mdate = new Timestamp(System.currentTimeMillis());
	}

	public Doc(int id, String title, String category, boolean repost) {
		this();
	
		this.id = id;
		this.title = title;
		this.category = category;
		this.repost = repost;
	}
	
	// The column without in 'Draft'.
	public void setOpaque(DocOpaque opa ) {
		if (opa != null) {
			this.views = opa.getViews();
			this.negative =  opa.getNegative();
			this.positive = opa.getPositive();
			this.score = opa.getScore();
			this.cdate = opa.getCdate();
		}
	}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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
		
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
		
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public int getCid() {
        return cid;
    }
    public void setCid(int cid) {
    	category = CidToString[cid];
        this.cid = cid;
    }
    
    public String getBrief() {
        return brief;
    }
    public void setBrief(String brief) {
        this.brief = brief;
    }

    public boolean getRepost() {
        return repost;
    }
    public void setRepost(boolean repost) {
        this.repost = repost;
    }
    public int getViews() {
        return views;
    }
    public void setViews(int views) {
        this.views = views;
    } 
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public int getPositive() {
        return positive;
    }
    public void setPositive(int positive) {
        this.positive = positive;
    }
    
    public int getNegative() {
        return negative;
    }

    public void setNegative(int negative) {
        this.negative = negative;
    }
 
    public String getText() {
        return text;
    }
    
    public void setText(String text ) {
        this.text = text;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
    
	public boolean getOnTop() {
		return onTop;
	}
	public void setOnTop(boolean onTop) {
		this.onTop = onTop;
	}
	
	public Timestamp getCdate() {
		return cdate;
	}
	
	public void setCdate(Timestamp cdate) {
		this.cdate = cdate;
	}
	
	public Timestamp getMdate() {
		return mdate;
	}
	
	public void setMdate(Timestamp mdate) {
		this.mdate = mdate;
	}
	
	public String getMdateStr() {
		return new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(mdate);
	}

    public String getCdateStr() {
		return new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(cdate);
    }
    public int getResid() {
        return resid;
    }
    public void setResid(int resid) {
        this.resid = resid;
    }
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url ) {
        this.url = url;
    }
}
