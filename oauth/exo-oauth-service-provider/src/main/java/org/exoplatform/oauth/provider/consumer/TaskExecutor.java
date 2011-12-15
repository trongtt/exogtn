/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.oauth.provider.consumer;

import org.chromattic.api.ChromatticSession;
import org.exoplatform.commons.chromattic.ChromatticLifeCycle;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 12/15/11
 */
public class TaskExecutor
{

   private final ChromatticLifeCycle lifecycle;

   public TaskExecutor(ChromatticLifeCycle lifecycle)
   {
      this.lifecycle = lifecycle;
   }

   public <T> T execute(Task<T> task)
   {
      ChromatticSession session = lifecycle.getContext().getSession();
      T result = task.run(session);
      session.save();
      return result;
   }
}
