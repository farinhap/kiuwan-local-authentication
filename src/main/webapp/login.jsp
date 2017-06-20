<!--
	copyright (c) 2011-2017 Optimyth Software Technologies. All rights reserved.
	
	This is a sample application. Do not use as production code.
-->

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
		String errorCode = request.getParameter("error");
		if (errorCode != null && !errorCode.isEmpty()) {
		%>	
			<b>An error has occurred during the authentication process. (<%=errorCode%>)</b>
		<%
		}
		%>
		
		<div>
			<br/>
            <div>
                <button onclick="location.href = 'index.jsp'" type="button">Login in Kiuwan</button>
            </div>
        </div>
	</body>
</html>


