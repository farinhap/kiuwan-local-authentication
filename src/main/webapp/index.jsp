<!--
	copyright (c) 2011-2017 Optimyth Software Technologies. All rights reserved.
	
	This is a sample application. Do not use as production code.
-->

<%@page import="java.net.URLEncoder"%>
<%@page import="java.security.Principal"%>
<%@page import="com.kiuwan.authentication.User"%>
<%@page import="com.kiuwan.authentication.TokenHandler"%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
	/* User configuration section */
	/* Edit according your kiuwan user preferences */	
	String ownerUsername = "john.fearless@gmail.com";
	String clientId = "auth_1";
	String secretKey = "2chpi17khvun90i343rrrse2e2276v64sj1pi9guh7ls544g3pjjiiv87763cfhqg62n6lvf7g51iuvpteisr4lntnnh6q3dsik3j5j";
	String kiuwanURL = "https://www.kiuwan.com/saas/web/dashboard/dashboard";

	String loginURL = "http://localhost:8080/kiuwan-auth/login.jsp";
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf8">
        <title>Kiuwan Authentication Service</title>

		<link rel="shortcut icon" type="image/x-icon" href="favicon.png"/>
        <link rel="stylesheet" type="text/css" href="styles.css"/>
    </head>
    
    <body>
		<%
		System.out.println("index.jsp ...");
    	String username = null;
    	
        Principal principal = (Principal)request.getUserPrincipal();
 
        if (null != principal) {
            username = principal.getName();
        } else {
        	System.out.println("ERROR: Principal is null.");        	
        }

        if (username == null ||  username.isEmpty()) {
			System.out.println("'username' is null or empty. Remote Address: " + request.getRemoteAddr());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.sendRedirect("index.jsp?error=USERNAME_IS_NULL_OR_EMPTY");
			return;
		}
		
		User user = new User(username);
		System.out.println("Authentication successfully: " + user.toString() + "(" + request.getRemoteAddr() + ")");
			
		TokenHandler.initSecret(secretKey);		
		String token = TokenHandler.createTokenForUser(user);
		
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		
		String tokenParam = TokenHandler.TOKEN_PARAM_NAME + "=" + URLEncoder.encode(token, "UTF-8");
		String userParam = TokenHandler.USER_PARAM_NAME + "=" + URLEncoder.encode(ownerUsername, "UTF-8");
		String clientParam = TokenHandler.CLIENT_PARAM_NAME + "=" + URLEncoder.encode(clientId, "UTF-8");
		String loginUrlParam = TokenHandler.LOGIN_URL_PARAM_NAME + "=" + URLEncoder.encode(loginURL, "UTF-8");
		
		String location = kiuwanURL + "?" + userParam + "&" + clientParam + "&" + loginUrlParam + "&" + tokenParam;
		System.out.println("Redirecting to: " + location);
		
		response.setHeader("Location", location);
		%>

    </body>
</html>
