<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html" import="java.util.ArrayList"%>

<head>
<link rel="stylesheet" type="text/css"
	href="/skin/DefaultStyleSheet.css">
</head>

<div class="OAuthConsumer"><portlet:defineObjects />
<div><h1>OAuth
Provider: Manage information of Consumer Applications</h1></div>
<div><c:if test="${not empty consumers}">
	<table border="1">
		<tr bgcolor="#99CCFF">
			<td valign="top"><b>Consumer Key</b></td>
			<td valign="top"><b>Consumer Secret</b></td>
			<td valign="top"><b>Callback URL</b></td>
			<td valign="top"><b>Name</b></td>
			<td valign="top"><b>Description</b></td>
			<td valign="top"><b>Website</b></td>
			<td valign="top"><b>Action</b></td>
		</tr>
		<c:forEach var="consumer" items="${consumers}">
			<tr>
				<td><c:out value="${consumer.consumerKey}"></c:out></td>
				<td><c:out value="${consumer.consumerSecret}"></c:out></td>
				<td><c:out value="${consumer.callbackURL}"></c:out></td>
				<td><c:out value="${consumer.properties['name']}"></c:out></td>
				<td><c:out value="${consumer.properties['description']}"></c:out></td>
				<td><c:out value="${consumer.properties['website']}"></c:out></td>
				<td><a
					href='<portlet:actionURL name="deleteConsumer"><portlet:param name="consumerKey" value="${consumer.consumerKey}" /></portlet:actionURL>')>Delete</a></td>
			</tr>
		</c:forEach>
	</table>
</c:if> <c:out value="${name}"></c:out></div>

<br />

<form action='<portlet:actionURL name="addConsumer" />' method="post">
<div class="AddConsumer">
<table align="center">
	<tr>
		<td>Consumer key*</td>
		<td><input type="text" name="consumerKey"
			value='<c:out value="${aNewConsumer.consumerKey}"></c:out>' /></td>
		<td class="errMsg"><c:out
			value="${requestScope.errorMsg.consumerKey}" /></td>
	</tr>
	<tr>
		<td>Consumer secret*</td>
		<td><input type="text" name="consumerSecret"
			value='<c:out value="${aNewConsumer.consumerSecret}"></c:out>' /></td>
		<td class="errMsg"><c:out
			value="${requestScope.errorMsg.consumerSecret}" /></td>
	</tr>
	<tr>
		<td>Callback Url*</td>
		<td><input type="text" name="callbackURL"
			value='<c:out value="${aNewConsumer.callbackURL}"></c:out>' /></td>
		<td class="errMsg"><c:out
			value="${requestScope.errorMsg.callbackURL}" /></td>
	</tr>
	<tr>
		<td>Name</td>
		<td><input type="text" name="consumerName"
			value='<c:out value="${aNewConsumer.properties['name']}"></c:out>' /></td>
	</tr>
	<tr>
		<td>Description</td>
		<td><input type="text" name="consumerDescription"
			value='<c:out value="${aNewConsumer.properties['description']}"></c:out>' /></td>
	</tr>
	<tr>
		<td>Website</td>
		<td><input type="text" name="consumerWebsite"
			value='<c:out value="${aNewConsumer.properties['website']}"></c:out>' /></td>
	</tr>
</table>
</div>
<input type="submit" value="Add" class="Submit" /></form>
<br />

</div>