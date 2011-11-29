<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html" import="java.util.ArrayList"%>

<portlet:defineObjects />

<div class="OAuthSession"><c:choose>
	<c:when test="${not empty consumers}">
		<div>Your authorized tokens</div>
		<br />
		<table align="center">
			<c:forEach var="consumer" items="${consumers}">
				<tr bgcolor="#99CCFF">
					<td valign="top"><b>Consumer</b></td>
					<td valign="top"><b>Consumer Url</b></td>
					<td valign="top"><b>Description</b></td>
					<td valign="top"><b>Action</b></td>
				</tr>
				<tr>
					<td><c:out value="${consumer.properties['name']}"></c:out></td>
					<td><c:out value="${consumer.properties['website']}"></c:out></td>
					<td><c:out value="${consumer.properties['description']}"></c:out></td>
					<td><a
						href='<portlet:actionURL name="revokeAccess"><portlet:param name="oauth_token" value="${consumer.properties['accessToken']}" /></portlet:actionURL>'>Revoke
					Access</a></td>
				</tr>
			</c:forEach>
		</table>
	</c:when>
	<c:otherwise>
		<div>You don't have any Authorized Tokens</div>
	</c:otherwise>
</c:choose><br />

</div>