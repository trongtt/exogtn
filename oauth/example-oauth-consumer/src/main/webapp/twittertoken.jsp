<div>Paste your access token here</div>
<div>Access token can get from your OAuth provider</div>
<div>
<br />
<form action="<%=request.getContextPath() + "/Twitter2"%>" name="token">
<table>
	<tr>
		<label for="oauth_token">oauth access token</label>
		<input type="text" name="oauth_token" value="201186108-NpEYdCJmfLdv7DLI89PDO06jbZQhozQ0rW4ZEEE2" />
	</tr>
	<tr>
		<label for="oauth_token_secret">oauth access token secret</label>
		<input type="text" name="oauth_token_secret" value="67obGPor0PAq6hqgE7oU20xt8z4A9zhBQiq9BZrnoc" />
	</tr>
</table>
<button type="submit" name="submit">Submit</button>
</form>
</div>