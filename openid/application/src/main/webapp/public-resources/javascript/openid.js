$("#OpenIdPortlet").ready(function() {
   var text = $('input[type="text"][readonly != "readonly"]');
   $('input[type="text"]').addClass("idleField");
   text.focus(function() {
      $(this).removeClass("idleField").addClass("focusField");
      if (this.value == this.defaultValue) {
         this.value = '';
         if (this.id === "password" || this.id === "confirmpassword") {
            this.type = "password";
         }
      }
      if (this.value != this.defaultValue) {
         this.select();
      }
   });
   text.blur(function() {
      $(this).removeClass("focusField").addClass("idleField");
      if ($.trim(this.value) == '') {
         this.value = (this.defaultValue ? this.defaultValue : '');
         if (this.id === "password" || this.id === "confirmpassword") {
            this.type = "text";
         }
      }
   });

   var password = $('input[type="password"]');
   password.addClass("idleField");
   password.focus(function() {
      $(this).removeClass("idleField").addClass("focusField");
      if (this.value == this.defaultValue) {
         this.value = '';
         this.type = "password";
      }
      if (this.value != this.defaultValue) {
         this.select();
      }
   });
   password.blur(function() {
      $(this).removeClass("focusField").addClass("idleField");
      if ($.trim(this.value) == '') {
         this.value = (this.defaultValue ? this.defaultValue : '');
      }
   });
});