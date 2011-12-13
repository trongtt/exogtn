<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%
		String contextPath = request.getContextPath();
		String consumerName = (String)request.getAttribute("oauth_consumer_name");
    String consumerDesc = (String)request.getAttribute("oauth_consumer_description");
    String token = (String)request.getAttribute("oauth_token");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>OAuth authorization</title>
    </head>
    <body>
        
    <h3>"<%=consumerName%>" is trying to access your information.</h3>
   
		<br/>
		   
    <form name="authorization_form" action="<%= contextPath + "/authorize"%>" method="POST">
        <input type="hidden" name="oauth_token" value="<%= token %>"/>
        <input type="submit" name="oauth_authorized" value="Grant access"/>
        <input type="submit" name="oauth_authorized" value="Deny access"/>
    </form>
    
    </body>
</html>
