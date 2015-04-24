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

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fucksocks.utils.SocksUtil;

/**
 * The class <code>Socks5DatagramPacketHandler</code> represents a datagram packet handler.
 * 
 * @author Youchao Feng
 * @date Mar 24, 2015 9:09:39 PM
 * @version 1.0
 *
 */
public class Socks5DatagramPacketHandler implements DatagramPacketEncapsulation,
    DatagramPacketDecapsulation {

  protected static final Logger logger = LoggerFactory.getLogger(Socks5DatagramPacketHandler.class);

  public Socks5DatagramPacketHandler() {

  }

  @Override
  public DatagramPacket encapsulate(DatagramPacket packet, SocketAddress destination)
      throws SocksException {
    if (destination instanceof InetSocketAddress) {
      InetSocketAddress destinationAddress = (InetSocketAddress) destination;
      final byte[] data = packet.getData();
      final InetAddress remoteServerAddress = packet.getAddress();
      final byte[] addressBytes = remoteServerAddress.getAddress();
      final int ADDRESS_LENGTH = remoteServerAddress.getAddress().length;
      final int remoteServerPort = packet.getPort();
      byte[] buffer = new byte[6 + data.length + ADDRESS_LENGTH];

      buffer[0] = buffer[1] = 0; // reserved byte
      buffer[2] = 0; // fragment byte
      buffer[3] = (byte) (ADDRESS_LENGTH == 4 ? AddressType.IPV4 : AddressType.IPV6);
      System.arraycopy(addressBytes, 0, buffer, 4, ADDRESS_LENGTH);
      buffer[4 + ADDRESS_LENGTH] = SocksUtil.getFisrtByteFromPort(remoteServerPort);
      buffer[5 + ADDRESS_LENGTH] = SocksUtil.getSecondByteFromPort(remoteServerPort);
      System.arraycopy(data, 0, buffer, 6 + ADDRESS_LENGTH, data.length);
      return new DatagramPacket(buffer, buffer.length, destinationAddress.getAddress(),
          destinationAddress.getPort());
    } else {
      throw new IllegalArgumentException("Only support java.net.InetSocketAddress");
    }
  }

  @Override
  public void decapsulate(DatagramPacket packet) throws SocksException {
    final byte[] data = packet.getData();

    if (!(data[0] == 0 && data[1] == data[0])) {
      // check reserved byte.
      throw new SocksException("SOCKS version error");
    }
    if (data[2] != 0) {
      throw new SocksException("SOCKS fregment is not supported");
    }
    InetAddress remoteServerAddress = null;
    int remoteServerPort = -1;
    byte[] originalData = null;

    switch (data[3]) {
      case AddressType.IPV4:
        try {
          remoteServerAddress = InetAddress.getByAddress(Arrays.copyOfRange(data, 4, 8));
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }
        remoteServerPort = SocksUtil.bytesToPort(data[8], data[9]);
        originalData = Arrays.copyOfRange(data, 10, packet.getLength());
        break;
      case AddressType.IPV6:
        try {
          remoteServerAddress = InetAddress.getByAddress(Arrays.copyOfRange(data, 4, 20));
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }
        remoteServerPort = SocksUtil.bytesToPort(data[20], data[21]);
        originalData = Arrays.copyOfRange(data, 22, packet.getLength());
        break;
      case AddressType.DOMAINNAME:
        // TODO implements later
        break;

      default:
        break;
    }

    packet.setAddress(remoteServerAddress);
    packet.setPort(remoteServerPort);
    packet.setData(originalData);
  }

}
