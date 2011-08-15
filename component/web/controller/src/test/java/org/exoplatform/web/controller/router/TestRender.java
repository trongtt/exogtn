/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.exoplatform.web.controller.router;

import org.exoplatform.web.controller.QualifiedName;
import static org.exoplatform.web.controller.metadata.DescriptorBuilder.*;

import java.util.Collections;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TestRender extends AbstractTestController
{

   public void testRoot() throws Exception
   {
      Router router = router().add(route("/")).build();

      //
      assertEquals("/", router.render(Collections.<QualifiedName, String>emptyMap()));
   }

   public void testA() throws Exception
   {
      Router router = router().add(route("/a")).build();

      //
      assertEquals("/a", router.render(Collections.<QualifiedName, String>emptyMap()));
   }

   public void testAB() throws Exception
   {
      Router router = router().add(route("/a/b")).build();

      //
      assertEquals("/a/b", router.render(Collections.<QualifiedName, String>emptyMap()));
   }

   public void testPathParam() throws Exception
   {
      Router router = router().add(route("/{p}")).build();

      //
      assertEquals("/a", router.render(Collections.singletonMap(Names.P, "a")));
      assertEquals("", router.render(Collections.<QualifiedName, String>emptyMap()));
   }

   public void testSimplePatternPathParam() throws Exception
   {
      Router router = router().add(route("/{p}").with(pathParam("p").matchedBy("a"))).build();

      //
      assertEquals("/a", router.render(Collections.singletonMap(Names.P, "a")));
      assertEquals("", router.render(Collections.singletonMap(Names.P, "ab")));
   }

   public void testPrecedence() throws Exception
   {
      Router router = router().
         add(route("/a")).
         add(route("/{p}/b").
            with(pathParam("p").matchedBy("a"))).
         build();

      //
      assertEquals("/a", router.render(Collections.<QualifiedName, String>emptyMap()));

      //
      assertEquals("/a/b", router.render(Collections.singletonMap(Names.P, "a")));
   }

   public void testLang() throws Exception
   {
      Router router = router().
         add(route("/{a}b").
            with(pathParam("a").matchedBy("(([A-Za-z]{2})/)?").preservePath())).
         build();

      //
      assertEquals("/fr/b", router.render(Collections.singletonMap(Names.A, "fr/")));
      assertEquals("/b", router.render(Collections.singletonMap(Names.A, "")));
   }
}
