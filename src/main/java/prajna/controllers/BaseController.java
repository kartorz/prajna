package prajna.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import prajna.models.ClientSession;
import prajna.services.*;

@Controller
public class BaseController {
    private static final Logger logger = LogManager.getLogger(BaseController.class.getName() );
    private static final String SSID_COOKIE = "ssid";
    private static final String DEFAULT_COOKIE = "JSESSIONID";

	@Autowired	AccountService accountService;
	@Autowired	SessionService sessionService;
	
	BaseController() {
	}
	
	public static String sessionAccount() {
		String username;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	if (principal instanceof UserDetails) {
    		username = ((UserDetails)principal).getUsername();
    	} else {
    	   username = principal.toString();
    	}
    	
    	//logger.info("[sessionAccount], username:" + username);
    	if (username.equalsIgnoreCase("anonymousUser")) //ROLE_ANONYMOUS
    		username = "";
        return username;
	}
	
	public boolean isAdmin() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			UserDetails usr = (UserDetails)principal;
			for (GrantedAuthority role : usr.getAuthorities()) {
				if (role.getAuthority().equalsIgnoreCase("ROLE_ADMIN"))
					return true;
			}
		}
		return false;
	}

	/*protected String sessionId(HttpServletRequest req, String ssid) {
		//String ssid = (String) req.getSession().getAttribute(DEFAULT_COOKIE);
		if (ssid == null || ssid.isEmpty()) {
			ssid = Long.toHexString(Double.doubleToLongBits(Math.random()));
			req.getSession().setAttribute(SSID_COOKIE, ssid);
		}
		return ssid;
	}*/

	protected ClientSession sessionClient(String ssid) {
		return sessionService.getClientSeesion(ssid);
	}

	protected void sessionOut(String ssid) {
		if (ssid != null && ssid.isEmpty() == false) {
			accountService.signOut(ssid);
		}
	}

	public static void responseFile(HttpServletResponse resp, String filePath) {
		logger.debug("{responseFile}: filePath:" + filePath);
		File file = new File(filePath);
		String extName = SystemService.getFileExtension(filePath);
    	if (extName.equalsIgnoreCase(".pdf")) {
    		resp.setContentType("application/pdf");
    	} else if(extName.equalsIgnoreCase(".doc")) {
    		resp.setContentType("application/msword");
    	} else if(extName.equalsIgnoreCase(".docx")) {
    		resp.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
      	} else if(extName.equalsIgnoreCase(".ppt")) {
    		resp.setContentType("application/vnd.ms-powerpoint");
      	} else if(extName.equalsIgnoreCase(".pptx")) {
    		resp.setContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
      	} else if(extName.equalsIgnoreCase(".xls")) {
    		resp.setContentType("application/vnd.ms-excel");
      	} else if(extName.equalsIgnoreCase(".xlsx")) {
    		resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      	} else if(extName.equalsIgnoreCase(".vsd")) {
    		resp.setContentType("application/vnd.visio");
      	} else if(extName.equalsIgnoreCase(".odp")) {
    		resp.setContentType("application/vnd.oasis.opendocument.presentation");
    	} else if(extName.equalsIgnoreCase(".ods")) {
    		resp.setContentType("application/vnd.oasis.opendocument.spreadsheet");
    	} else if(extName.equalsIgnoreCase(".odt")) {
    		resp.setContentType("application/vnd.oasis.opendocument.text");
    	} else if(extName.equalsIgnoreCase(".txt")) {
    		resp.setContentType("text/plain");
    	} else if(extName.equalsIgnoreCase(".epub")) {
    		resp.setContentType("application/epub+zip");
    	} else {
    		resp.setContentType("application/octet-strea");
    		//resp.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
    	}
    	resp.setContentLength((int)file.length());
    	//logger.info("{responseFile}, file length:" + file.length());
    	InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
	        int nbytes;
	        while ((nbytes = inputStream.read()) != -1) {
	        	resp.getWriter().write(nbytes);
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error("{responseFile}, can't read file:" + filePath);
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
	
	public static void sendError(HttpServletResponse resp, String message) {
		resp.setContentType("text/html; charset=UTF-8");
    	try {
			resp.getWriter().println("error:" + message);
			resp.flushBuffer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getBaseUrl(HttpServletRequest request) {
		String scheme = request.getScheme() + "://";
		String serverName = request.getServerName();
		String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort();
		String contextPath = request.getContextPath();
	    return scheme + serverName + serverPort + contextPath;
	}
}
