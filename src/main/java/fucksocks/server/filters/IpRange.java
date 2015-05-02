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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * 
 * The class <code>IpRange</code> represents an IPrange.
 * 
 * @author Youchao Feng
 * @date May 2, 2015 12:45:25 AM
 * @version 1.0
 *
 */
public class IpRange implements Iterable<Ip>, Serializable {

  private static final long serialVersionUID = 1L;


  private final Ip startIp;

  /**
   * 最大IP。
   */
  private final Ip endIp;

  /**
   * Constructs a <code>IpRange</code> instance by given tow IP.
   * 
   * @param startIp IP starts.
   * @param endIp IP ends.
   */
  public IpRange(Ip startIp, Ip endIp) {

    int result = endIp.compareTo(startIp);
    if (result > 0 || result == 0)
      Preconditions.checkArgument(result > 0 || result == 0,
          "maxIP must equal or bigger than minIP");

    this.startIp = startIp;
    this.endIp = endIp;
  }

  /**
   * Creates a <code>IpRange</code> instance by a string.
   * 
   * @param range a string such as "1.1.1.1-1.1.2.255".
   * @return IP range.
   */
  public static IpRange parseFromString(String range) {
    String[] ips = range.split("-");
    Preconditions.checkArgument(ips.length == 2,
        "IP range string must be fomarted as [minIP-maxIP],error argument:" + range);
    return new IpRange(Ip.parseFromString(ips[0]), Ip.parseFromString(ips[1]));
  }


  /**
   * Creates a {@link IpRange} instance by IP with mask.
   * 
   * @param ipWithMask IP/mask, such as 192.168.70.1/24
   * @return {@link IpRange} instance
   */
  public static IpRange parseFromIPWithMask(String ipWithMask) {
    long minIpAsLong = 0;
    long maxIpAsLong = 0;
    String[] strs = ipWithMask.split("/");

    if (strs.length == 2) {
      Ip ip = Ip.parseFromString(strs[0]);
      int mask = Integer.parseInt(strs[1]);
      long maskAsLong = 0xffffffff << (32 - mask);
      minIpAsLong = ip.toLong();
      maxIpAsLong = minIpAsLong | (~maskAsLong);
    } else {
      throw new IllegalArgumentException("The input String format error. for example"
          + " 192.168.1.1/24");
    }
    return new IpRange(new Ip(minIpAsLong), new Ip(maxIpAsLong));
  }


  /**
   * Gets A class IP range.
   * 
   * @return A class IP range.
   */
  public static IpRange AClassLocalIPRange() {
    // 10.0.0.0 - 10.255.255.255
    return new IpRange(new Ip(0x0A000000), new Ip(0x0AFFFFFF));
  }

  /**
   * Gets B class IP range.
   * 
   * @return B class IP range.
   */
  public static IpRange BClassLocalIPRange() {
    return new IpRange(new Ip(172, 16, 0, 0), new Ip(172, 31, 255, 255));
  }

  /**
   * Gets C class IP range.
   * 
   * @return C class IP range.
   */
  public static IpRange CClassLocalIPRange() {
    return new IpRange(new Ip(192, 168, 0, 0), new Ip(192, 168, 255, 255));
  }


  /**
   * Returns <code>true</code> if the given IP is in the IP range.
   * 
   * @param ip IP.
   * @return If the IP is in the rang return <code>true</code>.
   */
  public boolean contains(Ip ip) {
    if (ip.compareTo(startIp) >= 0 && ip.compareTo(endIp) <= 0) {
      return true;
    }
    return false;
  }

  /**
   * Returns size of IP range.
   * 
   * @return Size of IP range.
   */
  public long size() {
    return (endIp.getValue() - startIp.getValue() + 1L);
  }

  public Ip getStartIp() {
    return startIp;
  }

  public Ip getEndIp() {
    return endIp;
  }

  public List<IpRange> split(Ip ip) {
    List<IpRange> ranges = new ArrayList<IpRange>();
    if (this.contains(ip)) {
      ranges.add(new IpRange(this.startIp, ip));
      ranges.add(new IpRange(ip, this.endIp));
    }
    return ranges;
  }

  @Override
  public Iterator<Ip> iterator() {
    return new IpIterator(startIp, endIp);
  }

  @Override
  public String toString() {
    return startIp + "-" + endIp;
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof IpRange) {
      IpRange range = (IpRange) obj;
      return range.getStartIp().equals(startIp) && range.getEndIp().equals(endIp);
    } else {
      return false;
    }
  }

}
