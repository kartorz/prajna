package prajna.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import prajna.services.AccountService;

public class AdminInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LogManager.getLogger(AdminInterceptor.class.getName() );
	@Autowired	AccountService accountService;
	
	public boolean preHandle(
			HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//logger.info("preHandle");
		if (request.getMethod() != "GET") {
			//request.getRequestURI(): /doc/comment/0
			//request.getRequestURL() : http://127.0.0.1:8080/doc/comment/0
			//logger.debug("preHandle check if Admin:" + request.getRequestURI());
			String usr = BaseController.sessionAccount(request);
			if (!accountService.isAdmin(usr)) {
				BaseController.sendError(response, "请用管理员帐号登录");
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
