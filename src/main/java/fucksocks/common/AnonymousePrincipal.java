/*
 * Copyright 2015-2025 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package fucksocks.common;

import java.io.Serializable;
import java.security.Principal;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

/**
 * The class <code>AnonymousePrincipal</code> represents an anonymous principal.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date May 14, 2015 2:36:34 PM
 */
public class AnonymousePrincipal implements Principal, Serializable {

  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = 1L;

  @Override
  public String getName() {
    return "Anonymouse";
  }

  @Override
  public int hashCode() {
    return Hashing.md5().newHasher().putString(getName(), Charsets.UTF_8).hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AnonymousCredentials) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "Principal[Anonymouse]";
  }



}
