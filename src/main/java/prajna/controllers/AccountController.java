package prajna.controllers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import prajna.models.Account;
import prajna.services.AccountService;
import prajna.services.SystemService;

@Controller
public class AccountController extends BaseController {
    private static final Logger logger = LogManager.getLogger(AccountController.class.getName() );

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private SystemService  systemService;
	
	@RequestMapping("/account/info")
    public String getAccountInfo(HttpServletRequest req, HttpServletResponse resp, Model model) {
    	return "account/info";
    }

    @RequestMapping("/account/setting")
    public String getAccountStting(HttpServletRequest req, Model model,
					@RequestParam(value="tab", defaultValue = "0") int tabIndex) {
    	Account usrBean = accountService.getAccount(sessionAccount());
    	usrBean.setPassword("");
		model.addAttribute("usr", usrBean);
		model.addAttribute("err", "");
		model.addAttribute("tabinx", tabIndex);
        return "account/setting";
    }

    @RequestMapping(value="/account", method = RequestMethod.POST)
    public String postAccount(HttpServletRequest req, Model model, Account usrBean,
    						@RequestParam(value="tab", defaultValue = "0") int tabIndex,
    						@RequestParam(value="action", defaultValue = "u") String action) {
    	//logger.info("postAccount:" + usrBean.getAccount() + " action:" + action + " passwd:" + usrBean.getPassword());
    	if (sessionAccount().equalsIgnoreCase(usrBean.getAccount())) {
        	if (action.equalsIgnoreCase("u")) {
        		if (!usrBean.getPassword().isEmpty()) {
            		accountService.saveAccount(usrBean);
        		}
        		return "redirect:/";
        	}

        	if (!accountService.deleteAccount(usrBean)) {
        		usrBean.setPassword("");
        		model.addAttribute("usr", usrBean);
        		model.addAttribute("err", systemService.getMessage("passwd.nonmatch"));
        		model.addAttribute("tabinx", 1);
        		return "account/setting";
        	}
    	}
    	return "redirect:/";
    }

    @RequestMapping("/signin/reset-password")
    public String resetPassword(HttpServletRequest req, Model model, @RequestParam("email") String email) {
    	if (accountService.resetPassword(email)) {
    		return "redirect:/";
    	}

    	model.addAttribute("usr", new Account());
		model.addAttribute("email", email);
		model.addAttribute("tabinx", 2);
		model.addAttribute("err", systemService.getMessage("account.nonexist"));
    	return "login";
    }

    @RequestMapping(value="/signin/success")
    @ResponseBody
    public String signinSuccess(HttpServletRequest req, HttpServletResponse resp, Model model) {
    	return "success";
    }
  
    @RequestMapping(value="/signin", method = RequestMethod.GET)
    public String getSignin(HttpServletRequest req, HttpServletResponse resp, Model model,
							@RequestParam(value="tab", defaultValue = "0") int tabIndex,
							@RequestParam(value = "error", required = false) String error,
							@RequestParam(value = "logout", required = false) String logout) {
    	//logger.info("[getSignin], tabIndex:" + tabIndex );
		if (error != null) {
			try {
				resp.setCharacterEncoding("utf-8");
				PrintWriter out = resp.getWriter();
				out.println("用户名不存在，或者密码出错");
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		if (logout != null) {
			return "redirect:/";
		}
 
		model.addAttribute("usr", new Account());
		model.addAttribute("account", "");
		model.addAttribute("tabinx", tabIndex);
		return "login";
    }
    
    // Spring security takes over.
    /*
    @RequestMapping(value="/signin", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	@ResponseBody
    public String postSignin(HttpServletRequest req, HttpServletResponse resp, Model model,
    					@RequestParam("account") String account,
    					@RequestParam("passwd") String passwd) {
		logger.info("[postSignin], return success");
		if (accountService.checkPassword(account, passwd)) {
			req.getSession().setAttribute("account", account);
			return "success";
		}
        return "用户名不存在，或者密码出错";
    }*/

	@RequestMapping(value="/signup", method = RequestMethod.GET)
	public String getSignup(HttpServletRequest req, Model model, 
							@RequestParam(value="tab", defaultValue = "1") int tabIndex) {
		//logger.info("getSignup");
		if (tabIndex == 1) {
			model.addAttribute("usr", accountService.getAccount(sessionAccount()));
		} else {
			model.addAttribute("usr", new Account());
		}

		model.addAttribute("account", sessionAccount());
		model.addAttribute("err", "");
		model.addAttribute("tabinx", tabIndex);
		return "login";
	}

	@RequestMapping(value="/signup", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public String postSignup(HttpServletRequest req,
    					   HttpServletResponse resp,
    					   Model model,
    					   Account usrBean,
    					   @RequestParam(value="tab", defaultValue = "1") int tabIndex) {
		//logger.info("postSignup " + usrBean.getAccount());
		if (!accountService.signUp(usrBean)) {
	    	usrBean.setPassword("");
	    	if (usrBean.getAccount() != "") {
	    		model.addAttribute("usr", usrBean);
	    		model.addAttribute("err", "该帐号已注册，请选择其它帐号");
	    	} else {
	    		model.addAttribute("usr", usrBean);
	    		model.addAttribute("err", "");
	    	}
	    	model.addAttribute("account", sessionAccount());
    		model.addAttribute("tabinx", tabIndex);
	    	return "login";
	    }
	    req.getSession().setAttribute("account", usrBean.getAccount());
	    return "redirect:/";
    }
}
