package prajna.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import prajna.models.Account;
import prajna.repos.AccountRepo;

@Service
@Scope("application")
public class AccountService  {
	private static final Logger logger = LogManager.getLogger(AccountService.class.getName());
	private static final int PrivilegeAdmin = 5;
	private static final int PrivilegeDelete = 3;
	public  static final int BCryptStrength = 4; 
	
	//private HashMap<String, String>  ssidMap;

	@Autowired
	AccountRepo  accountRepo;  
	
	@Autowired 
	private EmailService  emailService;

	@Autowired
	private SecurityUserService securityUserService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public AccountService() {
		//ssidMap = new HashMap<String, String>();
	}
	
	public void saveAccount(Account usr) {
		if (!usr.getPassword().isEmpty()) {
			usr.setPassword( passwordEncoder.encode(usr.getPassword()));
		} else {
			usr.setPassword(getAccount(usr.getAccount()).getPassword());
		}
		accountRepo.save(usr);	
	}

	public boolean isAdmin(String usr) {
		if (usr != null && usr != "") { 
			Account account = accountRepo.findByAccount(usr);
			if (account != null) {
				for (String role: account.getRoles()) {
					if (role.equalsIgnoreCase("ROLE_ADMIN"))
                        return true;
				}
			}
        }
		return false;
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

		if (accountRepo.findByAccountIgnoreCase(usr.getAccount()) == null) {
			usr.setPassword(passwordEncoder.encode(usr.getPassword()));
			
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
			
			securityUserService.signIn(usr);
			return true;
		}

		return false;
	}

	public boolean checkPassword(String account, String passwd) {
		if (account == "" || passwd == "")
			return false;

		Account usr = accountRepo.findByAccount(account);
		if (usr != null) {
			String passEncoder = passwordEncoder.encode(passwd);
			if (passEncoder.equalsIgnoreCase(usr.getPassword()))
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
			String passEncoder = passwordEncoder.encode(passwd);
			ret = accountRepo.updatePasswordByAccount(account, passEncoder);
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
			String passEncoder = passwordEncoder.encode(passwd);
			ret = accountRepo.updatePasswordByAccount(account, passEncoder);
		}
		return (ret > 0);
	}
}
