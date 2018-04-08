package prajna.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


import prajna.services.AccountService;

@Controller
public class AdminController extends BaseController {
    private static final Logger logger = LogManager.getLogger(AdminController.class.getName() );

	@Autowired	
	AccountService accountService;

	@RequestMapping(value="/admin")
    public String admin(HttpServletRequest req, HttpServletResponse resp, Model model) {
		if (accountService.isAdmin(sessionAccount(req))) {
			return "admin";
		}
        return "redirect:/";
    }
}
