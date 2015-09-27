package fucksocks.common.net;

import java.net.DatagramPacket;

/**
 * @author Youchao Feng
 * @version 1.0
 * @date Sep 23, 2015 11:20 AM
 */
public class NetworkMonitor implements SocketMonitor, DatagramSocketMonitor {

  private long receiveTCP = 0;
  private long receiveUDP = 0;
  private long sendTCP = 0;
  private long sendUDP = 0;

  @Override
  public void onRead(int b) {
    receiveTCP++;
  }

  @Override
  public void onWrite(int b) {
    sendTCP++;
  }

  @Override
  public void onSend(DatagramPacket datagramPacket) {
    sendUDP += datagramPacket.getLength();
  }

  @Override
  public void onReceive(DatagramPacket datagramPacket) {
    receiveUDP += datagramPacket.getLength();
  }

  public long getTotalReceive() {
    return receiveTCP + receiveUDP;
  }

  public long getTotalSend() {
    return sendTCP + sendUDP;
  }

  public long getTotal() {
    return getTotalReceive() + getTotalSend();
  }

  public long getReceiveTCP() {
    return receiveTCP;
  }

  public long getReceiveUDP() {
    return receiveUDP;
  }

  public long getSendTCP() {
    return sendTCP;
  }

  public long getSendUDP() {
    return sendUDP;
  }

}
