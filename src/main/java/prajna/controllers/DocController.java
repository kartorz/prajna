package prajna.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.multipart.MultipartFile;

import prajna.models.*;
import prajna.services.*;
import prajna.repos.*;

@Controller
public class DocController extends BaseController {
    private static final Logger logger = LogManager.getLogger(DocController.class.getName() );
 
	@Autowired
	MenutreeRepo  menutreeRepository;

	@Autowired	
	DocService docService;

	@Autowired
	SessionService sessionService;
	
	@Autowired
	AccountService accountService;
	
	//@ModelAttribute("allCatetorys") 
	private List<MenuItem> getCategorys() {
		List<MenuItem> items = menutreeRepository.findByLevel(1);
		return items;
	}
 
	@RequestMapping(value="/doctree", method = RequestMethod.POST)
    @ResponseBody
    public String postDocTree(HttpServletRequest req, Model model, DocTreeItem itemBean) {
		logger.info("itemBea:" + itemBean.getId() + " text:" + itemBean.getText());
		docService.saveDocTree(itemBean);
		return "success";
	}

    //  '/doc/list/TV?p=1&s=20&q=score&q=mdate,desc'
    @RequestMapping( method=RequestMethod.GET, value="/doc/list/{cid}")
    public String getDocListView(Model model, Pageable page, @PathVariable("cid") int cid) {
        //logger.info("getDocListView category:"  + "pageable:" + page);
 
        List<Doc> docList = docService.getDocList(cid, page);
        if (docList != null) {
        	model.addAttribute("docs", docList.toArray());
        }

        return  "ajax/doclist :: doc-list" ;
    }
    
	@RequestMapping(method=RequestMethod.GET, value="/doc/editor/{id}")
    public String editDoc(HttpServletRequest req, HttpServletResponse resp, Model model,
    					  @PathVariable("id") int docId) {
    	String usr = sessionAccount(req);
	    if (usr.isEmpty()) {
	    	sendError(resp, "请先登录系统");
	    	return  null;
	    }
	    if (!accountService.isAdmin(usr)) {
	    	sendError(resp, "请用管理员帐号登录");
	    	return  null;
	    }

	    Draft draftBean = docService.getDraft(usr, docId);
	    if (draftBean == null) {
	    	Doc doc = docService.getDoc(docId);
	    	if (doc == null)
	    		draftBean = new Draft(usr, docId);
	    	else {
	    		draftBean = new Draft(usr, doc);
	    	}
	    	docService.saveDraft(draftBean);
	    }

		model.addAttribute("login", usr);
		model.addAttribute("allCatetorys", getCategorys());
	    model.addAttribute("docId", docId);
	    model.addAttribute("draftBean", draftBean);
        model.addAttribute("isAdmin", accountService.isAdmin(usr));
    
        logger.debug("editDoc docid:" + docId + " draftid:" + draftBean.getId());
	    return "doc/edit";
	}

	@RequestMapping(value="/draft", method = RequestMethod.POST)
    @ResponseBody
    public String postDraft(HttpServletRequest req, Model model, Draft draftBean) {
		if (draftBean.getId() == 0)
			return "error";
 
		//logger.debug("post draft: docid" + draftBean.getDid()  + "draft id:" + draftBean.getId());
		docService.saveDraft(draftBean);   
		return "success";
    }
	//
    @RequestMapping(value="/doc/{id}", method = RequestMethod.POST)
    public String postDoc(HttpServletRequest req,
    					HttpServletResponse resp,
    					Model model,
    					Draft draftBean,
    					@PathVariable("id") int id,
    					@RequestParam("action") String action) {
  
    	logger.debug("post doc: " + id + " draft id:" + draftBean.getId());

    	if(action.equalsIgnoreCase("publish")) {
		    docService.saveDoc(draftBean);
		    return "redirect:/";
    	}

    	docService.deleteDraft(draftBean);
	    return "redirect:/doc/editor/" + id;
    }

    @RequestMapping(value="/doc/e/{id}",  method = RequestMethod.GET)
    public void getEdoc(HttpServletRequest req, HttpServletResponse resp, Model model, @PathVariable("id") int id) {
    	Doc docBean = docService.getDoc(id);
    	if (docBean == null) {
    		logger.error("getEdoc, doc id:" + id + "is non-exist");
    		
    		sendError(resp, "文档不存在");
    		return;
    	}
    		
    	String fileName = docBean.getText();
    	Path docPath = SystemService.edocPath.resolve(fileName);
    	File docFile = new File(docPath.toString());
    	logger.info("fileName:" + fileName + ", docPath:" + docPath.toString());
   
    	String extName = SystemService.getFileExtension(fileName);
    	if (extName.equalsIgnoreCase(".pdf")) {
    		resp.setContentType("application/pdf");
    	} else {
    		resp.setContentType("application/octet-strea");
    		resp.setHeader("Content-Disposition", "attachment; filename=\"demo.pdf\"");
    	}
    	resp.setContentLengthLong(docFile.length());
 
    	InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(docFile);
	        int nbytes;
	        while ((nbytes = inputStream.read()) != -1) {
	        	resp.getWriter().write(nbytes);
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
		    if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
    }
    
    @RequestMapping(value="/doc/{cid}/{id}",  method = RequestMethod.GET)
    public String getDoc(HttpServletRequest req, Model model, @PathVariable("cid") int cid,  @PathVariable("id") int id) {
    	if (cid == Doc.CID_EDOC)
        	return "redirect:/doc/e/" + id;
    	
    	Doc docBean = docService.getDoc(id);
        if (docBean == null)
        	docBean = new Doc();
    	String usr = sessionAccount(req);
	    model.addAttribute("login", usr);
	    model.addAttribute("docBean", docBean);
        model.addAttribute("isOwner", accountService.canDeleteDocByUsr(usr, docBean.getAccount()));
        model.addAttribute("commentPgMeta", docService.getCommentPgMetaJson(id));
        model.addAttribute("comments",docService.getCommentList(id, new PageRequest(0, DocService.COMMT_PG_SIZE, Sort.Direction.DESC, "cdate")));
 
        return "doc/content";
    }

    @RequestMapping(value="/doc/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public String delDoc(@PathVariable("id") int id) {
    	logger.info("delete id:" + id);
        docService.deleteDoc(id);
        return "";
    }
    
    @RequestMapping("/uploader/upload")
    @ResponseBody
    public String upload(HttpServletRequest req,
    					@RequestParam(value="draftid", defaultValue="0") int draftId,
    					@RequestParam(value="type", defaultValue="image") String type) throws IOException, ServletException {

    	//logger.info("upload draft id:" + draftId);
    	try {
    		Part file = req.getPart("upload");
    		return docService.uploadRes(sessionAccount(req), draftId, file);
    	} catch (Exception e) {
    		logger.error("upload getPart error:" + e.toString());
    		e.printStackTrace();
    		return e.toString();
    	}
    }

    @RequestMapping(value="**/uploader/{resid}/{filename:.+}")
    @ResponseBody
    public byte[] getUploaderFile(HttpServletRequest req,
    							@PathVariable("resid") String resid,
    							@PathVariable("filename") String fileName) throws IOException {
    	//logger.info("getUploaderFile resid:" + resid);
    	return docService.getUploaderFile(resid, fileName);
    }
    
    @RequestMapping(value="/doc/comment", method = RequestMethod.GET)
    public String getDocComment(HttpServletRequest req,
    							HttpServletResponse resp,
    							Model model,
    							Pageable page,
    							@RequestParam("did") int did) {
    	//logger.info("sort:" + page.getSort().toString());
   
    	model.addAttribute("login",  sessionAccount(req));
        model.addAttribute("comments",docService.getCommentList(did, page));
        return  "ajax/commentlist :: comment-list" ;
    }
    
    @RequestMapping(value="/doc/comment", method = RequestMethod.POST)
    public String postDocComment(HttpServletRequest req,
    							HttpServletResponse resp,
    							Model model,
    							@RequestParam("did") int did,
    							@RequestParam("commtText") String text) throws IOException {
    	
    	String usr = sessionAccount(req);
	    if (usr.isEmpty()) {
	    	sendError(resp, "请先登录系统");
	    	return  null;
	    }
	    //logger.debug("post postDocComment: " + did);
    	DocComment docComment = new DocComment(did);
    	docComment.setText(text);
    	docComment.setAccount(usr);
    	docComment.setAuthor(accountService.getAccount(usr).getName());

    	if (text.length() <= DocComment.LEN_TEXT && docService.saveDocComment(docComment)) {
    		model.addAttribute("login", usr);
    		model.addAttribute("comments",docService.getCommentList(did, new PageRequest(0, DocService.COMMT_PG_SIZE, Sort.Direction.DESC, "cdate")));
    		return  "ajax/commentlist :: comment-list" ;
    	}
    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "3");
    	return null;
    }
    
    @RequestMapping(value="/doc/comment", method = RequestMethod.PUT, produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String putDocComment(HttpServletRequest req,
    							HttpServletResponse resp,
    							Model model,
    							@RequestParam("pk") int id,
    							@RequestParam("name") String col,
    							@RequestParam("value") String text) {
    	
    	//logger.info("putDocComment");
    	String usr = sessionAccount(req);
	    if (usr.isEmpty()) {
	    	sendError(resp, "请先登录系统");
	    	return  null;
	    }
	    if (text.length() <= DocComment.LEN_TEXT && docService.editTextOfDocComment(id, usr, text) > 0) {
	    	return "";
	    }
	    return "长度超出限制";
    }
    
    @RequestMapping(value="/doc/comment/{id}", method = RequestMethod.DELETE)
    public String deleteDocComment( HttpServletRequest req,
    								HttpServletResponse resp,
    								Model model,
    								@PathVariable("id") int id,
    								@RequestParam("did") int did,
    								@RequestParam("p") int p) {
    	//logger.info("deleteDocComment");
    	String usr = sessionAccount(req);
	    if (usr.isEmpty()) {
	    	sendError(resp, "请先登录系统");
	    	return  null;
	    }
	    docService.deleteDocComment(id, usr);
	    
    	model.addAttribute("login", usr);
        model.addAttribute("comments",docService.getCommentList(did, new PageRequest(p-1, DocService.COMMT_PG_SIZE, Sort.Direction.DESC, "cdate")));
        return  "ajax/commentlist :: comment-list" ;
    }
    
    
    @RequestMapping(value="/edoc", method = RequestMethod.POST)
    @ResponseBody
    public String postEdoc(HttpServletRequest req, HttpServletResponse resp, Model model,
    						@RequestParam("doc") MultipartFile doc,
    						@RequestParam("title") String title,
    						@RequestParam("pid")  int pid,
    						@RequestParam("cid")  int cid) {
    	logger.info("postEdoc: title:" + title + " pid:" + pid + " cid:" + cid);
    	String usr = sessionAccount(req);
	    if (!usr.isEmpty()) {
	    	docService.uploadEdoc(usr, 0, doc, title, pid, cid);
	    	return "success";
	    } else {
	    	sendError(resp, "请先登录系统");
	    	return null;
	    }

    }
}
