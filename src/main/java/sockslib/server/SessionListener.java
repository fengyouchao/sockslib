package sockslib.server;

import sockslib.common.SocksException;
import sockslib.common.methods.SocksMethod;
import sockslib.server.msg.CommandMessage;

/**
 * @author Youchao Feng
 * @version 1.0
 * @date Sep 30, 2015 12:38 PM
 */
public interface SessionListener {

  void postCreate(Session session) throws SocksException;

  void beforeDoMethod(Session session, SocksMethod method) throws SocksException;

  void postDoMethod(Session session, SocksMethod method) throws SocksException;

  void beforeDoCommand(Session session, CommandMessage commandMessage) throws SocksException;

  void postDoCommand(Session session, CommandMessage commandMessage) throws SocksException;

  void beforeClose(Session session) throws SocksException;

}
