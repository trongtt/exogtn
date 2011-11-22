<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"
	import="java.util.*, java.text.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript">
   function confirmRemove() {
	   return confirm("Do you want to delete the selected openid ?");
	}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<c:if test="${empty openids}">
		<div>There is no openid</div>
	</c:if>

	<c:if test="${not empty openids}">
		<table border="1">
			<tr>
				<th>OpenID</th>
				<th>User</th>
				<th>Action</th>
			</tr>
			<c:forEach var="openid" items="${openids}">
				<tr>
					<td><c:out value="${openid.key}"></c:out></td>
					<td><c:out value="${openid.value}"></c:out></td>
					<td align="center" valign="top">
                  <a href="
                     <portlet:actionURL>
                        <portlet:param name="javax.portlet.action" value="removeOpenIdAction" />
                        <portlet:param name="openId" value="${openid.key}" />
                     </portlet:actionURL>
                  " onclick="javascript: return confirmRemove()">Remove</a>
                  
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	<a href="
                     <portlet:actionURL>
                        <portlet:param name="javax.portlet.action" value="test" />
                     </portlet:actionURL>
                  ">Test</a>
</body>
</html>