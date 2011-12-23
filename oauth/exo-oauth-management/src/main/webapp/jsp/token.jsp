<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html" import="java.util.ArrayList;"%>

<head>
<link rel="stylesheet" type="text/css"
	href="/skin/DefaultStyleSheet.css">
</head>

<portlet:defineObjects />

<div class="OAuthToken"><c:choose>
	<c:when test="${not empty accessors}">
		<div><h1>Your
		authorized tokens</h1></div>
		<br />
		<table align="center" border="1">
				<tr bgcolor="#99CCFF">
					<td valign="top"><b>Consumer</b></td>
					<td valign="top"><b>Website</b></td>
					<td valign="top"><b>Description</b></td>
					<td valign="top"><b>Action</b></td>
				</tr>
				<c:forEach var="entry" items="${accessors}">
					<tr>
						<td><c:out value="${entry.value.properties['name']}"></c:out></td>
						<td><c:out value="${entry.value.properties['website']}"></c:out></td>
						<td><c:out value="${entry.value.properties['description']}"></c:out></td>
						<td><a
							href='<portlet:actionURL name="revokeAccess"><portlet:param name="oauth_token" value="${entry.key.accessTokenID}" /></portlet:actionURL>'>Revoke
						Access</a></td>
					</tr>
			</c:forEach>
		</table>
	</c:when>
	<c:otherwise>
		<div><h1>You
		don't have any Authorized Tokens</h1></div>
	</c:otherwise>
</c:choose><br />

</div>