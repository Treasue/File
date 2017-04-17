package model;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class filter implements Filter{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
		httpResponse.setContentType("text/html;charset=UTF-8");  
		httpResponse.addHeader("Access-Control-Allow-Origin", "*");  
		httpResponse.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");  
		httpResponse.setHeader("Access-Control-Max-Age", "0");  
		httpResponse.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token");  
		httpResponse.setHeader("Access-Control-Allow-Credentials", "true");  
		httpResponse.setHeader("XDomainRequestAllowed","1");  
        filterChain.doFilter(servletRequest, servletResponse);
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
