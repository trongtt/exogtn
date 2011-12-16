<div>Paste your access token here</div>
<div>Access token can get from your OAuth provider</div>
<div>
<br />
<form action="<%=request.getContextPath() + "/GateIn2"%>" name="token">
<table>
	<tr>
		<label for="oauth_token">oauth access token</label>
		<input type="text" name="oauth_token" value="" />
	</tr>
	<tr>
		<label for="oauth_token_secret">oauth access token secret</label>
		<input type="text" name="oauth_token_secret" value="" />
	</tr>
</table>
<button type="submit" name="submit">Submit</button>
</form>
</div>