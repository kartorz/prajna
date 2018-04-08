package prajna.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "Accounts")
public class Account {
    @Id
    private String account = "";
    private String password = "";

	private String name = "";
	private String nickName = "";
	private String email = "";
	private String tel = "";
	private String info = "";
	private String website= "";
	private int grade = 0;
	private Date regdate;
	private Date loginDate;
	private int gender = 0;
	private int privilege = 0;
	private boolean valid = true;

	public Account() {
		regdate = new Date();
	}

	public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
	
    public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
    public String getNickName() {
		return nickName;
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
    public String getWebsite() {
		return website;
	}
	
	public void setWebsite(String website) {
		this.website = website;
	}

	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public int getGender() {
		return gender;
	}
	
	public void setGender(int gender) {
		this.gender = gender;
	}
	
	public String getTel() {
		return tel;
	}
	
	public void setTel(String tel) {
		this.tel = tel;
	} 	
	
	public String getInfo() {
		return info;
	}
	
	public void setInfo(String info) {
		this.info = info;
	} 	

	public int getGrade() {
		return grade;
	}
	
	public void setGrade(int grade) {
		this.grade = grade;
	} 
	
	public Date getRegdate() {
		return regdate;
	}
	
	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}

	public Date getLoginDate() {
		return loginDate;
	}
	
	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}	

	public int getPrivilege() {
		return privilege;
	}
	
	public void setPrivilege(int privilege) {
		this.privilege = privilege;
	}
	
	public boolean getValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	} 
}
