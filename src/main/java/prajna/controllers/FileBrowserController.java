package prajna.controllers;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import prajna.models.EasyuiDataGrid;
import prajna.models.FileItem;
import prajna.services.FileBrowserService;

@Controller
public class FileBrowserController extends BaseController {
    private static final Logger logger = LogManager.getLogger(FileBrowserController.class.getName() );
	
	@Autowired	
	FileBrowserService fileBrowserService;
	
	@RequestMapping("/filebrowser")
	 public String filebrowser(HttpServletRequest req) {
		return "filebrowser";
	}
	
    @RequestMapping(value = "/filebrowser/foldertree.json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<FileItem> getDocTreeJson() {
    	List<FileItem> list = new ArrayList<FileItem>();
    	list.add(fileBrowserService.getDirTree(true));
    	return list;
    }
    
    @RequestMapping(value = "/filebrowser/d/**", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public EasyuiDataGrid getDirectory(HttpServletRequest req, HttpServletResponse resp, Model model) {
    	String pattern = "/filebrowser/d";
    	String uri = req.getRequestURI();
    	String dirPath = FileBrowserService.edocDir + uri.substring(uri.indexOf(pattern) +  pattern.length());
        logger.debug("[getDirectory], folder:"  + dirPath);	
    	EasyuiDataGrid jsonObj = new EasyuiDataGrid();
    	File file = new File(URI.create("file://" + dirPath)); // Parse URI String. eg: folder:/OS%E9%83%A8/%E5%94%90
    	
    	jsonObj.setRows(fileBrowserService.getFileList(file, false));
    	return jsonObj;
    }
    
    @RequestMapping(value="/filebrowser/f/**", method = RequestMethod.GET)
    public String getFile(HttpServletRequest req, HttpServletResponse resp, Model model) {
    	String pattern = "/filebrowser/f";
    	String uri = req.getRequestURI();
    	String uriPath = uri.substring(uri.indexOf(pattern) +  pattern.length());
    	String filePath = FileBrowserService.edocDir + uriPath;
        //String filePath = uri.replaceFirst("/filebrowser/f", FileBrowserService.edocDir);
    	logger.debug("[getFile], uri: " + uri + ",\n uriPath:" + uriPath +",\n filePath:" + filePath);
    	File file = new File(URI.create("file://" + filePath));
		if (file.exists()) {
			if (file.isDirectory()) {
				//String dirUri = uri.replaceFirst("/filebrowser/f", "/filebrowser/d");
				String dirUri = "/filebrowser/d" + uriPath;
				logger.info("redirect to URI:" + dirUri);
				return "redirect:/" + dirUri;
			}
			responseFile(resp, file.getPath());
			return null;
		} else {
			logger.error("{FileBrowserController}[responseFile], can't read file: " + filePath);
			return null;
		}
    }
}
