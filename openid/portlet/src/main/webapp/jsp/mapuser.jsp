<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>GateIn OpenID Map User</title>
</head>
<body>
	<div class="back">
		<a href="${routeAction}&form=register"><<</a>
	</div>
	<form name="openid-map-form" action="${processMappingOpenIdAction}"
		method="post" style="border: 1px">
		<div>
			<h2>Map with existing user</h2>
		</div>
		<div id="error" style="color: red">
			<p>${error}</p>
		</div>
		<div id="AccountInputSet" class="UIFormInputSet AccountInputSet">
			<table class="UIFormGrid">
				<tbody>
					<tr>
						<td class="FieldComponent"><input type="text" name="username" id="username" value="Username" /></td>
					</tr>
					<tr>
						<td class="FieldComponent"><input type="text" name="password" id="password" value="Password" /></td>
					</tr>
					<tr>
						<td class="FieldComponent"><input type="hidden" name="identifier" id="identifier" value=${identifier } readonly="readonly" /></td>
					</tr>
				</tbody>
			</table>
			<div class="action">
				<input type="submit" value="Map" />
			</div>
		</div>
	</form>
</body>
</html>