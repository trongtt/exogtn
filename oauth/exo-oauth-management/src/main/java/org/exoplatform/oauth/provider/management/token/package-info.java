@Application
@Assets(
   scripts = {
      @Script(src = "javascripts/jquery-1.7.1.min.js"),
      @Script(src = "javascripts/jquery-ui-1.7.2.custom.min.js"),
      @Script(src = "javascripts/token.js")
   },
   stylesheets = {
      @Stylesheet(src = "stylesheets/token/style.css")
   }
)
package org.exoplatform.oauth.provider.management.token;

import org.juzu.Application;
import org.juzu.plugin.asset.Assets;
import org.juzu.plugin.asset.Script;
import org.juzu.plugin.asset.Stylesheet;