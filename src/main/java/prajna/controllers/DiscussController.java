package prajna.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import prajna.models.*;
import prajna.services.*;
import prajna.repos.*;

@Controller
public class DiscussController extends BaseController {
    private static final Logger logger = LogManager.getLogger(DiscussController.class.getName() );
 
	@Autowired
	MenutreeRepo  menutreeRepository;

	@Autowired	
	AccountService accountService;

	@Autowired	
	DiscussService discussService;

	@RequestMapping("/discuss")
    public String discuss(HttpServletRequest req, Model model, @CookieValue("JSESSIONID") String ssid) {
		Pageable page = new PageRequest(0, SystemService.PG_SIZE, Sort.Direction.DESC, "cdate");
		//model.addAttribute("pgmeta", docService.getPgMetaJson(1));
	    model.addAttribute("tags", discussService.getMyTagList(sessionAccount(req), sessionClient(ssid)));
		Page<Question> qusPage = discussService.getQuestionPage(sessionAccount(req), sessionClient(ssid), page);
	    model.addAttribute("questions", qusPage);
	    model.addAttribute("pgmeta", discussService.getPgMetaJson(qusPage));
		model.addAttribute("login", sessionAccount(req));
	    return "discuss";
    }
  
	@RequestMapping( method=RequestMethod.GET, value="/questions")
    public String getQuestionListView(HttpServletRequest req, Model model, Pageable page, @CookieValue("JSESSIONID") String ssid) {
		Page<Question> qusPage = discussService.getQuestionPage(sessionAccount(req), sessionClient(ssid), page);
        model.addAttribute("questions", qusPage);

        return  "ajax/questionlist :: question-list" ;
    }
    
	@RequestMapping(value="/question/editor/{id}", method = RequestMethod.GET)
    public String getQuestionEditor(HttpServletRequest req,
    							HttpServletResponse resp,
    							Model model,
    							@CookieValue("JSESSIONID") String ssid,
    							@PathVariable("id") int qid ) {
		String usr = sessionAccount(req);
		if (usr.isEmpty()) {
			sendError(resp, "请先登录系统");
			return null;
		}
		model.addAttribute("qstnBean", discussService.getQuestion(qid));
		return "ajax/questioneditor :: dialog";
    }
	
	@RequestMapping(value="/question/{id}", method = RequestMethod.GET)
    public String getQuestion(HttpServletRequest req,
    							HttpServletResponse resp,
    						  Model model,
    						  @CookieValue("JSESSIONID") String ssid,
    						  @PathVariable("id") int qid ) {
		Question qstnBean = discussService.getQuestion(qid);
		model.addAttribute("login", sessionAccount(req));
		model.addAttribute("qstnBean", qstnBean);
	    model.addAttribute("commentPgMeta", discussService.getCommentPgMetaJson(qid));
		model.addAttribute("comments", discussService.getAnswers(qid, new PageRequest(0, SystemService.COMMT_PG_SIZE, Sort.Direction.DESC, "cdate")));
		model.addAttribute("invitees", qstnBean.getInvitees().split(","));
		return "question";
    }
	
	@RequestMapping(value="/question/{id}", method = RequestMethod.POST)
    public String postQuestion(HttpServletRequest req,
    							HttpServletResponse resp,
			  				   Model model,
			  				   @CookieValue("JSESSIONID") String ssid,
			  				   Question qstnBean,
			  				   @PathVariable("id") int qid) {
		String url;
		String usr = sessionAccount(req);
		if (usr.isEmpty()) {
			sendError(resp, "请先登录系统");
			return null;
		}
	
		if (qid == 0) {
			qstnBean.setAccount(usr);
			url = "discuss";
		} else {
			url = "question/" + qid;
		}
		
		discussService.saveQuestion(qstnBean);
		return "redirect:/" + url;
    }
	
	@RequestMapping(value="/question/{id}", method = RequestMethod.DELETE)
	@ResponseBody
    public String deleteQuestion(HttpServletRequest req,HttpServletResponse resp,
    						  Model model,
    						  @CookieValue("JSESSIONID") String ssid,
    						  @PathVariable("id") int qid ) {
	
		String usr = sessionAccount(req);
		if (usr.isEmpty()) {
			sendError(resp, "请先登录系统");
			return null;
		}
		
		discussService.deleteQuestion(qid);

		return "discuss";
    }
	
    @RequestMapping(value="/answers", method = RequestMethod.GET)
    public String getAnswers(HttpServletRequest req,HttpServletResponse resp,
    							Model model,
    							Pageable page,
    							@CookieValue("JSESSIONID") String ssid,
    							@RequestParam("did") int did) {
    	model.addAttribute("login",  sessionAccount(req));
        model.addAttribute("comments", discussService.getAnswers(did, page));
        return  "ajax/commentlist :: comment-list";
    }
    
    @RequestMapping(value="/answer/{id}", method = RequestMethod.DELETE)
    public String deleteAnswer(HttpServletRequest req,Model model,
    								@CookieValue("JSESSIONID") String ssid,
    								@PathVariable("id") int id,
    								@RequestParam("qid") int qid,
    								@RequestParam("p") int p) {
    	String usr = sessionAccount(req);
	    if (usr.isEmpty()) {
	    	return  null;
	    }
	    discussService.deleteAnswer(id);
    	model.addAttribute("login", usr);
        model.addAttribute("comments",discussService.getAnswers(qid, new PageRequest(p-1, SystemService.COMMT_PG_SIZE, Sort.Direction.DESC, "cdate")));
        return  "ajax/commentlist :: comment-list" ;
    }
    
    @RequestMapping(value="/answer", method = RequestMethod.PUT)
    public void putAnswer(HttpServletRequest req,HttpServletResponse resp,
    							Model model,
    							@CookieValue("JSESSIONID") String ssid,
    							@RequestParam("pk") int id,
    							@RequestParam("name") String col,
    							@RequestParam("value") String text) throws IOException{
    	String usr = sessionAccount(req);
	    if (usr.isEmpty()) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No login");
			return;
	    }
	    text  = SystemService.toHtml(text);
	    discussService.editAnswer(id, text);
    }
    
    @RequestMapping(value="/answer", method = RequestMethod.POST)
    public String postAnswer(HttpServletRequest req,HttpServletResponse resp,
    							Model model,
    							@CookieValue("JSESSIONID") String ssid,
    							@RequestParam("qid") int qid,
    							@RequestParam("commtText") String text) throws IOException {
    	String usr = sessionAccount(req);
	    if (usr.isEmpty()) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "0");
			return null;
	    }
	    text  = SystemService.toHtml(text);
	    QuestionAnswer answer = new QuestionAnswer(qid, text, usr);
	    discussService.saveAnswer(answer);
	    
    	model.addAttribute("login", usr);
	    model.addAttribute("commentPgMeta", discussService.getCommentPgMetaJson(qid));
        model.addAttribute("comments",discussService.getAnswers(qid, new PageRequest(0, SystemService.COMMT_PG_SIZE, Sort.Direction.DESC, "cdate")));
        return  "ajax/commentlist :: comment-list" ;
    }
	
	@RequestMapping(value="/tag/{name}", method = RequestMethod.PUT)
	@ResponseBody
    public String toggleTag(HttpServletRequest req,
    							Model model,
    							@CookieValue("JSESSIONID") String ssid,
    							@PathVariable("name") String tag,
    							@RequestParam("selected") boolean selected) {
		discussService.setTagSelected(tag, selected, sessionAccount(req), sessionClient(ssid));
		return "discuss";
    }
	
	@RequestMapping(value="/tagjson", method = RequestMethod.GET)
	@ResponseBody
    public String getTagJson() {
	    String json = "[";
	    
		List<String> tagList = discussService.getTagList();
		for (int i=0; i<tagList.size()-1; i++) {
			json += "{\"id\":" + (i + 1) + ",\"text\":\"" + tagList.get(i) + "\"},";
		}
		json += "{\"id\":" + tagList.size() + ",\"text\":\"" + tagList.get(tagList.size()-1) + "\"}";
		json += "]";
		return json;
	}
	
    @RequestMapping(value="/invitee", method = RequestMethod.POST)
    public String postInviter(HttpServletResponse resp,
    							HttpServletRequest req,
    							Model model,
    							@CookieValue("JSESSIONID") String ssid,
    							@RequestParam("qid") int qid,
    							@RequestParam("invitee") String invitee) throws IOException  {
    	
    	//logger.info("joni postInviter :" + getBaseUrl(req));
    	String usr = sessionAccount(req);
	    if (usr.isEmpty()) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "0");
			return null;
	    }
	    
	    if (!discussService.checkInviterPermission(qid, usr)) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "1");
			return null;
	    }
	    
	    String qustnUrl =  getBaseUrl(req) + "/question/" + qid;
	    String invitees = discussService.sendInvite(qid, usr, invitee, qustnUrl);
	    if (!invitees.isEmpty()) {
	    	model.addAttribute("invitees", invitees.split(","));
        	return  "ajax/invitee :: invitee-list" ;
	    }
	    
	    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "2");
    	return null;
    }
}
