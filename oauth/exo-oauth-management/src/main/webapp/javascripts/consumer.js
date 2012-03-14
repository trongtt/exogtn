$(function() {
  /* Search consumer with give information */
  $(".search-form").on("click", ".submit", function() {
	searchConsumer(this);
  });
  
  $(".search-form").find(".text").keyup(function(event) {
	if(event.keyCode == 13) {
		searchConsumer(this);
	}
  });
  
  var searchConsumer = function(elt) {
	var action = elt.ConsumerApplication().search();
    elt.$find(".result").load(action, 
    						{value : elt.$find('.text').val(), type : elt.$find('.type').val()}, 
    						function() {});
  }
  
  /* Navigation hover event */
  $(".navigation-normal").hover(
  	function() {
  		$(this).removeClass().addClass("navigation-over");
  	},
  	function() {
  		$(this).removeClass().addClass("navigation-normal");
  	}
  );
  
  /* table row hover events */
  $(".all-consumer tr").live("mouseover",
  	function() {
  		$(this).removeClass().addClass("row-over");
  });
  
  $(".all-consumer tr").live("mouseout",
  	function() {
  		$(this).removeClass().addClass("row-normal");
  	}
  );
  
  $(".consumer-detail tr").live("mouseover",
  	function() {
  		$(this).removeClass().addClass("row-over");
  });
  
  $(".consumer-detail tr").live("mouseout",
  	function() {
  		$(this).removeClass().addClass("row-normal");
  	}
  );
  
  /* edit consumer information */
  $(".edit-icon").click(
  	function() {
  		edit(this);
  	}
  );
  
  var edit = function(elt) {
	var action = elt.ConsumerApplication().editAction();
	elt.$find(".info-consumer").load(action, 
		    						{key: elt.$find("table.info-table td.key").html()}, 
		    						function() {});
  }
  
  $(".consumer-inputs").find(".submit").live("click", function() {
	submitSaveConsumer(this);
  });
  
  var submitSaveConsumer = function(elt) {
	var action = elt.ConsumerApplication().submitConsumerAction();
	var params = {};
	var inputs = elt.$find("table :input");
	inputs.each(function() {
		params[this.name] = $(this).val();
	});
    elt.$find(".info-consumer").load(action, 
    						params, 
    						function() {});
  }
  
  /* refresh token*/
  $(".refresh-icon").click(
  	function() {
  		refreshToken(this);
  	}
  );
  
  var refreshToken = function(elt) {
	var action = elt.ConsumerApplication().refreshTokenAction();
	elt.$find(".info-token").load(action, 
		    						{key: elt.$find("table.info-table td.key").html()}, 
		    						function() {});
  }
  
  /* view help information */ 
  $(".help-icon").click(function() {	  
	  $('#help-detail').html("Input full information of your consumer:" +
				"				\n Consumer key is represent as identifier of consumer in system." +
				"				\n Consumer secret is string that consumer will use to authenticate in system." +
				"				\n Callback URL is used by system when consumer authenticate sucessfully, " +
				"				\n system will return to this URL for consumer").dialog();
  	}
  );
  
});