package prajna.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import prajna.models.*;
import prajna.services.*;
import prajna.repos.*;

@Controller
public class RootController extends BaseController {
    private static final Logger logger = LogManager.getLogger(RootController.class.getName() );
 
	@Autowired
	MenutreeRepo  menutreeRepository;

	@Autowired	
	AccountService accountService;
	
	@Autowired	
	DocService docService;
    
	@Autowired  SystemService  systemService;
	
    @RequestMapping("/")
    public String index(HttpServletRequest req, Model model,
    				@RequestParam(value="cid", defaultValue="1") int cid) {
		//Pageable page = new PageRequest(0, DocService.PG_SIZE, Sort.Direction.DESC, "mdate");
		model.addAttribute("pgmeta", docService.getPgMetaJson(cid));
        model.addAttribute("docs", docService.getDocList(cid, 0));

		model.addAttribute("login", sessionAccount());
		//model.addAttribute("title", systemService.getMessage("header.title"));

        return "index";
    }

    @RequestMapping(value = "/doctree.json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<DocTreeItem> getDocTreeJson() {
    	return docService.getDocTree().getChildren();
    }
    
    @RequestMapping(value = "/menutree", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getMenuTreeJson() {
    	List<MenuItem> mainMenu = menutreeRepository.findByLevel(1);
    	String json =  "[";
        for (int i = 0; i < mainMenu.size(); i++) {
            json += "{";
            json += "\"id\":" + mainMenu.get(i).getId() + ",";
            json += "\"text\":\"" + mainMenu.get(i).getName() + "\"";
            json += "}";
            if (i < mainMenu.size() - 1)
                json += ",";
        }
        json += "]";
        return json;
    }

    @RequestMapping(value = "/categorytree", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getCategoryTreeJson() {
    	List<MenuItem> mainMenu = menutreeRepository.findByLevel(1);
    	String json =  "[{";
    	json += "\"id\":1,\n";
    	json += "\"text\":\"Doc Category\",\n";
    	json += "\"children\":[";
        for (int i = 0; i < mainMenu.size(); i++) {
            json += "{\n";
            json += "\"id\":" + (10 + i + 1) + ",\n";
            json += "\"text\":\"" + mainMenu.get(i).getName() + "\"\n";
            json += "}";
            if (i < mainMenu.size() - 1)
                json += ",";
        }
        json += "]";
 
        json += "}]";
        return json;
    }

    /*@RequestMapping (value = "/**", method = {RequestMethod.GET, RequestMethod.POST})
    public String defaultPath(HttpServletRequest request) {
    	 String contextPath = request.getContextPath();
    	 String requestUri = request.getRequestURI();

    	logger.info("Unmapped request handling!: contextPath:" + contextPath + "  uri " + requestUri);
    	//return new ResponseEntity<String>("Unmapped request", HttpStatus.OK);
    	return "redirect: default";
    }*/
}
