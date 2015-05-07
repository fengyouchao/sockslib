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

package fucksocks.server.filters;

import java.util.Iterator;

/**
 * 
 * The class <code>IpIterator</code> represents an IP address iterator.
 * 
 * @author Youchao Feng
 * @date May 2, 2015 9:23:00 AM
 * @version 1.0
 *
 */
public class IpIterator implements Iterator<Ip> {

  /**
   * IP address range.
   */
  private IpRange range;

  /**
   * Current IP address.
   */
  private Ip currentIP;

  /**
   * A flag. It's always <code>true</code> in the beginning but it will become <code>false</code> if
   * {{@link #next()} is invoked.
   */
  private boolean start = true;

  /**
   * Constructs an instance of {@link IpIterator} with a {@link IpRange}.
   * 
   * @param range IP address range.
   */
  public IpIterator(IpRange range) {
    this.range = range;
    currentIP = range.getStartIp();
  }

  /**
   * Constructs an instance of {@link IpIterator} with tow IP address.
   * 
   * @param startIP Starting IP address.
   * @param endIP End IP address.
   */
  public IpIterator(Ip startIP, Ip endIP) {
    range = new IpRange(startIP, endIP);
    currentIP = startIP;
  }

  @Override
  public boolean hasNext() {
    if (start) {
      return true;
    } else {
      return range.contains(currentIP.nextIP());
    }
  }

  @Override
  public Ip next() {

    if (start) {
      start = false;
      return currentIP;
    } else {
      currentIP = currentIP.nextIP();
      return currentIP;
    }
  }

  @Override
  public void remove() {}

}
