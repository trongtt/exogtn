$(function() {
  var search = function(elt) {
	var action = elt.ConsumerApplication().search();
    elt.$find(".result").load(action, 
    						{value : elt.$find('.text').val(), type : elt.$find('.type').val()}, 
    						function() {});
  }
  
  var edit = function(elt) {
		var action = elt.ConsumerApplication().editAction();
	    elt.$find(".info-consumer").load(action, 
	    						{key: elt.$find("table.info-table td.key").html()}, 
	    						function() {});
  }
  
  var submit = function(elt) {
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

  function hideBox() {
			setTimeout(function() {
				$(".add-consumer .help-notification").removeAttr("style").fadeOut();
			}, 1000 );
  }
	
  $(".search-form").on("click", ".submit", function() {
		search(this);
  });
  
  $(".search-form").find(".text").keyup(function(event) {
		if(event.keyCode == 13) {
			search(this);
		}
  });
  
  $(".help").click(function() {
  	$(".add-consumer .help-notification").show("Fold", { to: { width: 280, height: 185 }}, 500, hideBox);
  });
  
  $(".navigation-normal").hover(
  	function() {
  		$(this).removeClass().addClass("navigation-over");
  	},
  	function() {
  		$(this).removeClass().addClass("navigation-normal");
  	}
  );
  
  $(".all-consumer tr").live("mouseover",
  	function() {
  		$(this).removeClass().addClass("row-over");
  });
  
  $(".all-consumer tr").live("mouseout",
  	function() {
  		$(this).removeClass().addClass("row-normal");
  	}
  );
  
  $(".consumer-detail tr").hover(
  	function() {
  		$(this).removeClass().addClass("row-over");
  	},
  	function() {
  		$(this).removeClass().addClass("row-normal");
  	}
  );
  
  $(".edit-icon").click(
  	function() {
  		edit(this);
  	}
  );
  
  $(".help-icon").hover(
  	function() {
  		$(this).css("size", "200%");
  	},
  	function() {
  		$(this).css("size", "100%");
  	}
  );
  
  $(".help-icon").click(function() {
		$('.help-notification').animate({
		    opacity: 0.25,
		    left: '+=50',
		    height: 'toggle'
		  }, 5000, function() {
		    // Animation complete.
		  });
  	}
  );
  
  $(".consumer-inputs").find(".submit").live("click", function() {
		submit(this);
  });
  
});