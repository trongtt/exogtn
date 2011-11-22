/*
	Simple OpenID Plugin
	http://code.google.com/p/openid-selector/
	
	This code is licensed under the New BSD License.
*/

var providers;
var openid;
(function ($) {
openid = {
	signin_text : null, // text on submit button on the form
	all_small : false, // output large providers w/ small icons
	no_sprite : false, // don't use sprite image
	image_title : '{provider}', // for image title

	input_id : null,
	provider_url : null,
	provider_id : null,

	/**
	 * Class constructor
	 * 
	 * @return {Void}
	 */
	init : function(input_id) {
		providers = $.extend({}, providers_large, providers_small);
		var openid_btns = $('#openid_btns');
		this.input_id = input_id;
		$('#openid_choice').show();
		$('#openid_input_area').empty();
		var i = 0;
		// add box for each provider
		for (id in providers_large) {
			box = this.getBoxHTML(id, providers_large[id], (this.all_small ? 'small' : 'large'), i++);
			openid_btns.append(box);
		}
		if (providers_small) {
			openid_btns.append('<br/>');
			for (id in providers_small) {
				box = this.getBoxHTML(id, providers_small[id], 'small');
				openid_btns.append(box);
			}
		}
		$('#openid_form').submit(this.submit);
	},

	/**
	 * @return {String}
	 */
	getBoxHTML : function(box_id, provider, box_size, index) {
		return '<a title="' + this.image_title.replace('{provider}', provider["name"]) + '" href="javascript:openid.signin(\'' + box_id + '\');"'
				+ 'class="' + box_id + ' openid_' + box_size + '_btn"></a>';
	},

	/**
	 * Provider image click
	 * 
	 * @return {Void}
	 */
	signin : function(box_id) {
		var provider = providers[box_id];
		if (!provider) {
			return;
		}
		this.highlight(box_id);
		this.provider_id = box_id;
		this.provider_url = provider['url'];
		// prompt user for input?
		if (provider['label']) {
			this.useInputBox(provider);
		} else {
			$('#openid_input_area').empty();
			$('#openid_form').submit();
		}
	},

	/**
	 * Sign-in button click
	 * 
	 * @return {Boolean}
	 */
	submit : function() {
		var url = openid.provider_url;
		if (url == null) return;
		
		if (url) {
			url = url.replace('{username}', $('#openid_username').val());
			openid.setOpenIdUrl(url);
		}
		if (url.indexOf("javascript:") == 0) {
			url = url.substr("javascript:".length);
			eval(url);
			return false;
		}
		return true;
	},

	/**
	 * @return {Void}
	 */
	setOpenIdUrl : function(url) {
		var hidden = document.getElementById(this.input_id);
		if (hidden != null) {
			hidden.value = url;
		} else {
			$('#openid_form').append('<input type="hidden" id="' + this.input_id + '" name="' + this.input_id + '" value="' + url + '"/>');
		}
	},

	/**
	 * @return {Void}
	 */
	highlight : function(box_id) {
		// remove previous highlight.
		var highlight = $('#openid_highlight');
		if (highlight) {
			highlight.replaceWith($('#openid_highlight a')[0]);
		}
		// add new highlight.
		$('.' + box_id).wrap('<div id="openid_highlight"></div>');
	},

	/**
	 * @return {Void}
	 */
	useInputBox : function(provider) {
		var input_area = $('#openid_input_area');
		var html = '';
		var id = 'openid_username';
		var value = '';
		var label = provider['label'];
		var style = 'identifier ';
		if (label) {
			html = '<div class="message">' + label + '</div>';
		}
		if (provider['name'] == 'OpenID') {
			id = this.input_id;
			value = 'http://';
         style += "openid";
		}
		html += '<input id="' + id + '" type="text" class="' + style + '" name="' + id + '" value="' + value + '" />'
				+ '<input id="openid_submit" type="submit" value="' + this.signin_text + '"/>';
		input_area.empty();
		input_area.append(html);
		$('#' + id).focus();
	},

};
})(jQuery);
