package com.optimyth.kiuwan.authentication;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class successfulAuthentication
 */
@WebServlet(urlPatterns = "/*")
public class SuccessfulAuthentication extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	
	private static String loginURL = "http://localhost:8080/kwAuth";
	private static String customer = "admin";
	private static String secretKey = "secret";
	private static String clientId = "auth1";
	
	private static String kiuwanURL = "https://www.kiuwan.com/saas/web/dashboard/dashboard";
	
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SuccessfulAuthentication() {

    	Properties properties = new Properties();
    	try {
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
			
			if (properties.containsKey("loginURL")) {
				loginURL = properties.getProperty("loginURL");
			}
			
			if (properties.containsKey("username")) {
				customer = properties.getProperty("username");
			}
			
			if (properties.containsKey("secretKey")) {
				secretKey = properties.getProperty("secretKey");
			}
			
			if (properties.containsKey("clientId")) {
				clientId = properties.getProperty("clientId");
			}
			
			if (properties.containsKey("kiuwanURL")) {
				kiuwanURL = properties.getProperty("kiuwanURL");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	Service.initSecret(secretKey);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doIt(request, response);
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doIt(request, response);
	}

	private void doIt(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Principal principal = request.getUserPrincipal();
		if (principal == null || principal.getName() == null || principal.getName().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		
		String username = principal.getName();
		User user = new User(username);
		
		String token = Service.createTokenForUser(user);
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		String tokenParam = Service.TOKEN_PARAM_NAME + "=" + token;
		String userParam = Service.USER_PARAM_NAME + "=" + URLEncoder.encode(customer, "UTF-8");
		String clientParam = Service.CLIENT_PARAM_NAME + "=" + URLEncoder.encode(clientId, "UTF-8");
		String loginUrlParam = Service.LOGIN_URL_PARAM_NAME + "=" + URLEncoder.encode(loginURL, "UTF-8");
		response.setHeader("Location", kiuwanURL + "?" + userParam + "&" + clientParam + "&" + loginUrlParam + "&" + tokenParam);
	}
	

}
