package sockslib.server;

import sockslib.server.listener.SessionListener;
import sockslib.server.listener.StopProcessException;
import sockslib.server.msg.CommandMessage;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The class <code>BasicSessionManager</code> implements {@link SessionManager}
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Oct 10,2015 7:15 PM
 */
public class BasicSessionManager implements SessionManager {

  private static int nextSessionId = 0;
  private Map<Long, Session> managedSessions = new HashMap<>();
  private Map<String, SessionListener> sessionListeners = new HashMap<>();

  @Override
  public Session newSession(Socket socket) {
    Session session = new SocksSession(++nextSessionId, socket, managedSessions);
    managedSessions.put(session.getId(), session);
    return session;
  }


  @Override
  public Session getSession(long id) {
    return managedSessions.get(id);
  }

  @Override
  public void sessionOnCreate(Session session) throws StopProcessException {
    for (SessionListener listener : sessionListeners.values()) {
      listener.onCreate(session);
    }
  }

  @Override
  public void sessionOnCommand(Session session, CommandMessage message) throws
      StopProcessException {
    for (SessionListener listener : sessionListeners.values()) {
      listener.onCommand(session, message);
    }
  }

  @Override
  public void sessionOnException(Session session, Exception exception) {
    for (SessionListener listener : sessionListeners.values()) {
      listener.onException(session, exception);
    }
  }

  @Override
  public void sessionOnClose(Session session) {
    for (SessionListener listener : sessionListeners.values()) {
      listener.onClose(session);
    }
  }

  @Override
  public void removeSessionListener(String name) {
    sessionListeners.remove(name);
  }

  @Override
  public void addSessionListener(String name, SessionListener listener) {
    sessionListeners.put(name, listener);
  }

  @Override
  public Map<Long, Session> getManagedSessions() {
    return managedSessions;
  }

  public Map<String, SessionListener> getSessionListeners() {
    return sessionListeners;
  }

  public void setSessionListeners(Map<String, SessionListener> sessionListeners) {
    this.sessionListeners = checkNotNull(sessionListeners, "sessionListeners");
  }
}
