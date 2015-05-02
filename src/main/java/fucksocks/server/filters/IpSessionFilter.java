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

  private List<IpRange> ipRanges = new ArrayList<IpRange>();

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

  public enum Mode {
    BLACK_LIST, WHITE_LIST
  }

  public List<IpRange> getIpRanges() {
    return ipRanges;
  }

  public void setIpRanges(List<IpRange> ipRanges) {
    this.ipRanges = ipRanges;
  }

  public Mode getMode() {
    return mode;
  }

  public IpSessionFilter setMode(Mode mode) {
    this.mode = mode;
    return this;
  }
  
  public IpSessionFilter addIpRange(String ipRange) {
    ipRanges.add(IpRange.parseFromString(ipRange));
    return this;
  }
  
  public IpSessionFilter addIp(String ip) {
    ipRanges.add(IpRange.parseFromString(ip+"-"+ip));
    return this;
  }
  
}
