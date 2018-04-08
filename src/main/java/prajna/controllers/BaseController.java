package prajna.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	public static String sessionAccount(HttpServletRequest req) {
        Object ret = req.getSession().getAttribute("account");
        if (ret == null)
        	return "";
		
        return (String) ret;
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
