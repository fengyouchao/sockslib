package fucksocks.server;

import fucksocks.common.SocksException;
import fucksocks.server.msg.CommandMessage;

/**
 * @author Youchao Feng
 * @version 1.0
 * @date Sep 30, 2015 12:38 PM
 */
public interface SessionListener {

  void onCreate(Session session);

  void onCommand(Session session, CommandMessage commandMessage) throws SocksException;

  void onClose(Session session, Exception exception);

}
