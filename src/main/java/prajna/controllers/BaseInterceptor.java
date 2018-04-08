package prajna.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class BaseInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LogManager.getLogger(BaseInterceptor.class.getName() );
    
	public boolean preHandle(
			HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//logger.debug("preHandle");
		/*if (!request.isRequestedSessionIdFromCookie()) {
			request.getSession(true);
		}*/
	
		/*if (request.getRequestURI().startsWith("/signin")) {
			return false;
		}*/

		if (request.getMethod() == "POST" || request.getMethod() == "PUT") {
			return moderate(request, response);
		}
		return true;
	}
	
	/*
	@Override
	public void afterCompletion(
			HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}*/

	private boolean moderate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		long dt = System.currentTimeMillis() - request.getSession().getLastAccessedTime();
		if (dt < 3000 && dt > 0) {
			// FFirst time dt == 0
			//logger.info("dt:" + dt);
			BaseController.sendError(response, "操作太频繁了，先休息一下吧");
			return false;
		}
		return true;
	}
}
