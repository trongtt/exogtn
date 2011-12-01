<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html" import="java.util.ArrayList;"%>

<portlet:defineObjects />

<div class="OAuthSession"><c:choose>
	<c:when test="${not empty accessors}">
		<div>Your authorized tokens</div>
		<br />
		<table align="center">
				<tr bgcolor="#99CCFF">
					<td valign="top"><b>Consumer</b></td>
					<td valign="top"><b>Consumer Url</b></td>
					<td valign="top"><b>Description</b></td>
					<td valign="top"><b>Action</b></td>
				</tr>
				<c:forEach var="entry" items="${accessors}">
					<tr>
						<td><c:out value="${entry.value.properties['name']}"></c:out></td>
						<td><c:out value="${entry.value.properties['website']}"></c:out></td>
						<td><c:out value="${entry.value.properties['description']}"></c:out></td>
						<td><a
							href='<portlet:actionURL name="revokeAccess"><portlet:param name="oauth_token" value="${entry.key.accessToken}" /></portlet:actionURL>'>Revoke
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