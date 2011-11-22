<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false" 
    import="java.util.*, java.text.*"%>
<% String message = (String) request.getAttribute("message"); %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
   function submit(openid) {
      if (openid) {
         $("#openid_identifier").val(openid);
      } else {
         var openid_identifier = $("#openid_identifier").val();
      }

      $("form").submit();
   }
   
   $(document).ready(function() {
      openid.init('openid_identifier');
   });
</script>
</head>
<body>
   <c:if test="${empty message }">
      <form action="${loginActionURL}" method="post" id="openid_form">
		   <input type="hidden" name="returnurl" value="${returnurl}" />
			   <div id="openid_choice">
				   <div class="message">Please click your account provider:</div>
				   <div id="openid_btns"></div>
			   </div>
			   <div id="openid_input_area">
				   <input id="openid_identifier" name="openid_identifier" type="text" value="http://" />
				   <input id="openid_submit" type="submit" value="Sign-In"/>
			   </div>
			   <noscript>
				   <p>OpenID is service that allows you to log-on to many different websites using a single indentity.
				   Find out <a href="http://openid.net/what/">more about OpenID</a> and <a href="http://openid.net/get/">how to get an OpenID enabled account</a>.</p>
			   </noscript>
	   </form>
	</c:if>
   <c:if test="${not empty message }">
     <div id="message" class="message"><%=message%></div>
   </c:if>
</body>
</html>
