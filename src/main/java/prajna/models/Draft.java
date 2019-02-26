package prajna.models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Entity
public class Draft {
	private static final Logger logger = LogManager.getLogger(Draft.class.getName());
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private int    did;
	private String account;
	private String title;
	private String category;
	private boolean repost;
	private String text;
	private int cid = 1;
	private int resid;
	private String author; // Author's account or name
	private String url = "";
	private boolean onTop = false;
	
	public Draft() {
		clear();
	}
	
	public Draft(String account, int did) {
		clear();
		this.account = account;
		this.did = did;
	}
	
	public Draft(String account, Doc doc) {
		this.id = 0;
		this.account = account;
		this.did =  doc.getId();
		
		this.title = doc.getTitle();
		this.category =  doc.getCategory();
		this.repost = doc.getRepost();
		this.onTop = doc.getOnTop();

		//Draft.account is 'login' usr, But doc's author is somebody's account or name. 
		this.author = doc.getAccount().isEmpty() ? doc.getAuthor() : doc.getAccount();

		this.cid = doc.getCid();
		this.resid = doc.getResid();
		this.text = doc.getText();
	}
	
	public void clear() {
		this.id = 0;
		this.account = "";
		this.did = 0;
		this.cid = 1;
		this.resid = 0;
		this.title = "";
		this.category = "";
		this.repost = false;
		this.text = "";
		this.author = "";
		this.onTop = false;
	}
   
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

	public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }
    
    //Category Id
    public int getCid() {
        return cid;
    }
    public void setCid(int cid) {
        this.cid = cid;
    }
   
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
	
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }

    public boolean getRepost() {
        return repost;
    }
    
    public void setRepost(boolean repost) {
        this.repost = repost;
    }
 
    public String getText() {
        return text;
    }
    
    public void setText(String text ) {
        this.text = text;
    }
    
    public int getResid() {
        return resid;
    }
    public void setResid(int resid) {
        this.resid = resid;
    }
   
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url ) {
        this.url = url;
    }
    
	public boolean getOnTop() {
		return onTop;
	}
	public void setOnTop(boolean onTop) {
		this.onTop = onTop;
	}
}
