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

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import fucksocks.server.Session;

/**
 * 
 * The class <code>WhiteListIpSessionFilter</code> represents
 * 
 * @author Youchao Feng
 * @date May 2, 2015 12:42:18 AM
 * @version 1.0
 *
 */
public class IpSessionFilter implements SessionFilter {

  /**
   * Ranges of IP address.
   */
  private List<IpRange> ipRanges = new ArrayList<IpRange>();

  /**
   * Mode.
   */
  private Mode mode = Mode.WHITE_LIST;

  @Override
  public boolean doFilter(Session session) {
    Ip ip = new Ip(((InetSocketAddress) session.getRemoteAddress()).getAddress().getAddress());
    switch (mode) {

      case BLACK_LIST:
        for (IpRange range : ipRanges) {
          if (range.contains(ip)) {
            return false;
          }
        }
        return true;

      case WHITE_LIST:
        for (IpRange range : ipRanges) {
          if (range.contains(ip)) {
            return true;
          }
        }
        return false;

      default:
        break;

    }
    return false;
  }

  /**
   * Returns all IP address ranges.
   * 
   * @return All IP address ranges.
   */
  public List<IpRange> getIpRanges() {
    return ipRanges;
  }

  /**
   * Sets IP address ranges.
   * 
   * @param ipRanges
   */
  public void setIpRanges(List<IpRange> ipRanges) {
    this.ipRanges = ipRanges;
  }

  /**
   * Returns mode.
   * 
   * @return mode.
   */
  public Mode getMode() {
    return mode;
  }

  /**
   * Changes mode.
   * 
   * @param mode Mode.
   * @return The instance of {@link IpSessionFilter}.
   */
  public IpSessionFilter setMode(Mode mode) {
    this.mode = mode;
    return this;
  }

  /**
   * Add an IP address range to the List of IP address ranges.
   * 
   * @param ipRange IP address range in String such as "192.168.1.1-192.168.255.255".
   * @return The instance of {@link IpSessionFilter}.
   */
  public IpSessionFilter addIpRange(String ipRange) {
    ipRanges.add(IpRange.parseFromString(ipRange));
    return this;
  }

  /**
   * Add an IP address to the list of IP address ranges.
   * 
   * @param ip IP address in String such as "192.168.1.1".
   * @return The instance of {@link IpSessionFilter}.
   */
  public IpSessionFilter addIp(String ip) {
    ipRanges.add(IpRange.parseFromString(ip + "-" + ip));
    return this;
  }

  /**
   * The enumeration <code>Mode</code> represents a Mode of {@link IpSessionFilter}.
   * 
   * @author Youchao Feng
   * @date May 4, 2015 3:44:37 AM
   * @version 1.0
   *
   */
  public enum Mode {
    /**
     * Black list mode.
     */
    BLACK_LIST,
    /**
     * White list mode.
     */
    WHITE_LIST
  }

}
