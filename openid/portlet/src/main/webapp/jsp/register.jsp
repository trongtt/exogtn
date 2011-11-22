<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>GateIn OpenID Register/Map User</title>
</head>
<body>
	<div class="back">
		<a href="${backAction}"><<</a>
	</div>
	<form name="openid-register-form" action="${registerAction}" method="post" accept-charset="utf-8" style="border: 1px">
		<div class="message">
			Register new user and map openID with this user
			<p>
				You had a GateIn user, using <a href="${mapOpenIdAction}">map user</a>
			</p>
		</div>
		<div class="error" id="error">${error}</div>
		<div id="AccountInputSet" class="UIFormInputSet">
			<table class="UIFormGrid">
				<tbody>
					<tr>
						<td class="FieldComponent"><input type="text" name="username" id="username" value="Username" /> *</td>
					</tr>
					<tr>
						<td class="FieldComponent"><input type="text" name="password" id="password" value="Password" /> *</td>
					</tr>
					<tr>
						<td class="FieldComponent"><input type="text" name="Confirmpassword" id="confirmpassword" value="Confirm password" /> *</td>
					</tr>
					<tr>
						<td class="FieldComponent"><input type="text" name="firstName" id="firstName" value="First name" /> *</td>
					</tr>
					<tr>
						<td class="FieldComponent"><input type="text" name="lastName" id="lastName" value="Last name" /> *</td>
					</tr>
					<tr>
						<td class="FieldComponent"><input type="text" name="email" type="text" id="email" value="Email" /> *</td>
					</tr>
					<tr>
						<td class="FieldComponent"><input name="identifier" type="hidden" id="identifier" value="${identifier}" readonly="readonly" /></td>
					</tr>
				</tbody>
			</table>
			<div class="action">
				<input type="submit" value="Submit"></input>
			</div>
		</div>
	</form>
</body>
</html>
