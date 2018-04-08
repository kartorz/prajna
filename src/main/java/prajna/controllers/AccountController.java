package prajna.controllers;

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

	@Autowired	AccountService accountService;
	@Autowired  SystemService  systemService;
 
	@RequestMapping("/account/info")
    public String getAccountInfo(HttpServletRequest req, HttpServletResponse resp, Model model) {
    	return "account/info";
    }

    @RequestMapping("/account/setting")
    public String getAccountStting(HttpServletRequest req, Model model,
					@RequestParam(value="tab", defaultValue = "0") int tabIndex) {
    	Account usrBean = accountService.getAccount(sessionAccount(req));
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
    	if (sessionAccount(req).equalsIgnoreCase(usrBean.getAccount())) {
        	if (action.equalsIgnoreCase("u")) {
        		if (!usrBean.getPassword().isEmpty()) {
        			logout(req);
        		}
        		accountService.saveAccount(usrBean);

        		return "redirect:/";
        	}

        	if (!accountService.deleteAccount(usrBean)) {
        		usrBean.setPassword("");
        		model.addAttribute("usr", usrBean);
        		model.addAttribute("err", systemService.getMessage("passwd.nonmatch"));
        		model.addAttribute("tabinx", 1);
        		return "account/setting";
        	}
        	logout(req);
    	}
    	return "redirect:/";
    }

    @RequestMapping("/signin/reset-password")
    public String resetPassword(HttpServletRequest req, Model model,
    							@RequestParam("account") String account) {
    	if (accountService.resetPassword(account)) {
    		logout(req);
    		return "redirect:/";
    	}

    	model.addAttribute("usr", new Account());
		model.addAttribute("account", account);
		model.addAttribute("tabinx", 0);
		model.addAttribute("err", systemService.getMessage("account.nonexist"));
    	return "register";
    }

    @RequestMapping(value="/signin", produces = "text/plain;charset=UTF-8")
	@ResponseBody
    public String login(HttpServletRequest req, HttpServletResponse resp, Model model,
    					@RequestParam("account") String account,
    					@RequestParam("passwd") String passwd) {
		if (accountService.checkPassword(account, passwd)) {
			req.getSession().setAttribute("account", account);
			return "success";
		}
        return "用户名不存在，或者密码出错";
    }

	@RequestMapping(value="/signout")
    public String logout(HttpServletRequest req) {
		req.getSession().removeAttribute("account");
	    return "redirect:/";
    }

	@RequestMapping(value="/signup", method = RequestMethod.GET)
	public String getSignup(HttpServletRequest req, Model model, 
							@RequestParam(value="tab", defaultValue = "0") int tabIndex) {
		//logger.info("getSignup");
		if (tabIndex == 1) {
			model.addAttribute("usr", accountService.getAccount(sessionAccount(req)));
		} else {
			model.addAttribute("usr", new Account());
		}

		model.addAttribute("account", sessionAccount(req));
		model.addAttribute("err", "");
		model.addAttribute("tabinx", tabIndex);
		return "register";
	}

	@RequestMapping(value="/signup", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public String postSignup(HttpServletRequest req,
    					   HttpServletResponse resp,
    					   Model model,
    					   Account usrBean,
    					   @RequestParam(value="tab", defaultValue = "0") int tabIndex) {
		//logger.info("signup " + usrBean.getAccount());
	    if (!accountService.signUp(usrBean)) {
	    	usrBean.setPassword("");
	    	if (usrBean.getAccount() != "") {
	    		model.addAttribute("usr", usrBean);
	    		model.addAttribute("err", "该帐号已注册，请选择其它帐号");
	    	} else {
	    		model.addAttribute("usr", usrBean);
	    		model.addAttribute("err", "");
	    	}
	    	model.addAttribute("account", sessionAccount(req));
    		model.addAttribute("tabinx", 0);
	    	return "register";
	    }
	    req.getSession().setAttribute("account", usrBean.getAccount());
	    return "redirect:/";
    }
}
