package prajna.controllers;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import prajna.models.UserMessage;
import prajna.services.AccountService;
import prajna.services.SystemService;
import prajna.services.UserMessageService;

@Controller
public class AboutController extends BaseController {
    private static final Logger logger = LogManager.getLogger(AboutController.class.getName() );
	
    @Autowired	AccountService accountService;
	@Autowired	UserMessageService usrMsgService;
	@Autowired  SystemService  systemService;

    @RequestMapping(value="/about", method = RequestMethod.GET)
    public String getAbout(HttpServletRequest req, Model model) {
    	model.addAttribute("login", req.getSession().getAttribute("account"));
    	model.addAttribute("commentPgMeta", usrMsgService.getCommentPgMetaJson());
        model.addAttribute("comments",usrMsgService.retrieveMessages(
        		new PageRequest(0, SystemService.PG_SIZE, Sort.Direction.DESC, "cdate")));
 
        return "about";
    }
	
    @RequestMapping(value="/about/messages", method = RequestMethod.GET)
    public String getMessages(HttpServletRequest req,
    							HttpServletResponse resp,
    							Model model,
    							Pageable page,
    							@RequestParam("did") int did) {
    	//logger.info("getMessages");
    	model.addAttribute("login", sessionAccount(req));
        model.addAttribute("comments",usrMsgService.retrieveMessages(page));
        return  "ajax/commentlist :: comment-list" ;
    }

    @RequestMapping(value="/about/message", method = RequestMethod.POST)
    public String postMessage(HttpServletRequest req,
    							HttpServletResponse resp,
    							Model model,
    							@RequestParam("commtText") String text) throws IOException {
	    //logger.info("postMessage");
    	String usr = sessionAccount(req);
    	UserMessage msgBean = new UserMessage();
    	msgBean.setText(text);
    	msgBean.setAccount(usr);
    	msgBean.setAuthor(accountService.getAccount(usr).getName());

    	if (text.length() <= UserMessage.LEN_TEXT) {
    		usrMsgService.saveMessage(msgBean);

    		model.addAttribute("login", usr);
    		model.addAttribute("comments",usrMsgService.retrieveMessages(
    						new PageRequest(0, SystemService.PG_SIZE, Sort.Direction.DESC, "cdate")));
    		return  "ajax/commentlist :: comment-list" ;
    	}
    	sendError(resp, systemService.getMessage("input.oversize"));
    	return null;
    }
 
    @RequestMapping(value="/about/message", method = RequestMethod.PUT, produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String putMessage(HttpServletRequest req,
    							HttpServletResponse resp,
    							Model model,
    							@RequestParam("pk") int id,
    							@RequestParam("name") String col,
    							@RequestParam("value") String text) {
    	
    	//logger.info("putMessage");
    	String usr = sessionAccount(req);
	    if (text.length() <= UserMessage.LEN_TEXT && usrMsgService.updateMessage(id, usr, text) > 0) {
	    	return "";
	    }
	    return systemService.getMessage("input.oversize");
    }
    
    @RequestMapping(value="/about/message/{id}", method = RequestMethod.DELETE)
    public String deleteMessage( HttpServletRequest req,
    								Model model,
    								@PathVariable("id") int id,
    								@RequestParam("did") int did,
    								@RequestParam("p") int p) {
    	//logger.info("deleteMessage");
    	String usr = sessionAccount(req);
    	usrMsgService.deleteMessage(id, usr);
	    
    	model.addAttribute("login", usr);
		model.addAttribute("comments",usrMsgService.retrieveMessages(
				new PageRequest(0, SystemService.PG_SIZE, Sort.Direction.DESC, "cdate")));
        return  "ajax/commentlist :: comment-list" ;
    } 
}
