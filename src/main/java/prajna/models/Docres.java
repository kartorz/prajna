package prajna.models;

//import java.io.InputStream;
import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Docres {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id=0; 

	private int    did = 0;
	private int   draftId = 0;
	private String ftype = "";
	private String fname = "";
	private Blob data = null;
	private String url = "";
	
	public Docres() {
	}
   
	public Docres(int draftId) {
		this.draftId = draftId;
	}
	
	public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }

    public int getDraftId() {
        return draftId;
    }
    public void setDraftId(int draftId) {
        this.draftId = draftId;
    }
   
    public String getFtype() {
        return ftype;
    }
    
    public void setFtype(String ftype) {
        this.ftype = ftype;
    }
	
    public String getFname() {
        return fname;
    }
    
    public void setFname(String fname) {
        this.fname = fname;
    }
 
    public Blob getData() {
        return data;
    }
    
    public void setData(Blob data ) {
        this.data = data;
    }
    
    public String geturl() {
    	return url;
    }

    public void seturl(String url) {
    	this.url = url;
    }
}
