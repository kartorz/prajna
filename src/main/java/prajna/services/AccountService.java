package prajna.services;


import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import prajna.models.Account;
import prajna.repos.AccountRepo;

@Service
@Scope("application")
public class AccountService {
	private static final Logger logger = LogManager.getLogger(AccountService.class.getName());
	private static final int PrivilegeAdmin = 5;
	private static final int PrivilegeDelete = 3;

	//private HashMap<String, String>  ssidMap;

	@Autowired
	AccountRepo  accountRepo;  
	
	@Autowired EmailService  emailService;
	
	public AccountService() {
		//ssidMap = new HashMap<String, String>();
	}

	public void saveAccount(Account usr) {
		if (!usr.getPassword().isEmpty()) {
			usr.setPassword(SystemService.getStrMd5(usr.getPassword()));
		} else {
			usr.setPassword(getAccount(usr.getAccount()).getPassword());
		}
		accountRepo.save(usr);	
	}

	public boolean deleteAccount(Account usr) {
		if (checkPassword(usr.getAccount(), usr.getPassword())) {
			accountRepo.delete(usr.getAccount());
			return true;
		}
		return false;
	}
	
	public boolean signUp(Account usr) {
		if (usr.getAccount() == "" || usr.getPassword() == "")
			return false;

		usr.setAccount(usr.getAccount().toLowerCase());
		if (accountRepo.findByAccount(usr.getAccount()) == null) {
			String passMd5 = SystemService.getStrMd5(usr.getPassword());
			if (passMd5 != "") {
				usr.setPassword(passMd5);

				String accountStr = usr.getAccount();
				int atInx = accountStr.indexOf('@');
				if (atInx != -1) {
					if (usr.getNickName() == "")
						usr.setNickName(accountStr.substring(0, atInx));
					usr.setEmail(accountStr);
				} else {
					if (usr.getNickName() == "")
						usr.setNickName(accountStr);
				}
				usr.setName(usr.getNickName());
				accountRepo.save(usr);
				//logger.info("sign up success!");
				return true;
			}
		}

		return false;
	}

	public boolean checkPassword(String account, String passwd) {
		if (account == "" || passwd == "")
			return false;

		Account usr = accountRepo.findByAccount(account);
		if (usr != null) {
			String passMd5 = SystemService.getStrMd5(passwd);
			if (passMd5.equalsIgnoreCase(usr.getPassword()))
				return true;
		}
		return false;
	}

	public void signOut(String ssid) {
		//ssidMap.remove(ssid);
	}
	
	public String getAccountBySsid(String ssid) {
		String ret = "";
		/*if (ssid != null && ssidMap.get(ssid) != null)
			ret = ssidMap.get(ssid);*/

		return ret;
	}
	
	public void mapAccountBySsid(String ssid, String account) {
		/*if (ssidMap.get(ssid) == null)
			ssidMap.put(ssid, account);*/
	}
	
	public boolean isAdmin(String account) {
		if (account != null && account != "") { 
			Account usr = accountRepo.findByAccount(account);
			if (usr != null)
				return (usr.getPrivilege() >= PrivilegeAdmin);
		}
		return false;
	}
	
	public boolean canDeleteDocByUsr(String login, String docAccount) {
		if (login.isEmpty())
			return false;

		if (login.equalsIgnoreCase(docAccount))
			return true;
	
		Account usr = accountRepo.findByAccount(login);
		if (usr != null) {
			//privilege
			return (usr.getPrivilege() >= PrivilegeDelete);
		}
		return false;
	}
	
	public Account getAccount(String account) {
		Account usr = null;
		if (!account.isEmpty()) {
			usr = accountRepo.findByAccount(account);
		}
		
		if (usr == null) {
			usr = new Account();
		}
		return usr;
	}

	public boolean resetPassword(String account) {
		int ret = 0;
		if (account != "") {
			String passwd = Integer.toString((int)((Math.random()*9+1)*100000));
			String passMd5 = SystemService.getStrMd5(passwd);
			ret = accountRepo.updatePasswordByAccount(account, passMd5);
			if (ret > 0) {
				emailService.resetPassword(account, passwd);
			}
		}
		//logger.info("resetPassword: account:" + account);
		return (ret > 0);
	}

	public boolean updatePassword(String account, String passwd) {
		int ret = 0;
		if (account != "") {
			String passMd5 = SystemService.getStrMd5(passwd);
			ret = accountRepo.updatePasswordByAccount(account, passMd5);
		}
		return (ret > 0);
	}
}
