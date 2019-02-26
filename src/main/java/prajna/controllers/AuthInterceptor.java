package prajna.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import prajna.services.AccountService;

public class AuthInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LogManager.getLogger(AuthInterceptor.class.getName() );
	@Autowired	AccountService accountService;
	
	public boolean preHandle(
			HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		boolean bIntercept = false;
	
		if (request.getMethod() != "GET")
			bIntercept = true;
		else {
			if (request.getRequestURI().startsWith("/account/"))
				bIntercept = true;
		}
		bIntercept = false;
		if (bIntercept) {
			//logger.debug("preHandle check usr login");
			String usr = BaseController.sessionAccount();
			if (usr.isEmpty()) {
				BaseController.sendError(response, "请先登录系统");
				//response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "0");
				return  false;
			}
		}
		return true;
	}
	/*
	@Override
	public void afterCompletion(
			HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}*/
}
