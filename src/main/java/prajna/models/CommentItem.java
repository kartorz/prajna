package prajna.models;


public interface CommentItem {

	public  int getId();
	
    public  void setId(int id);

    public int getDid();

    public void setDid(int pid); 
    
    public String getAccount();
    public void setAccount(String account);
 
    public String getAuthor();
    public void setAuthor(String author); 
    
	public String getText();
    public void setText(String text);
}
