package fucksocks.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
  private List<SessionListener> sessionListeners = new ArrayList<>();

  @Override
  public Map<Long, Session> getAllManagedSessions() {
    return managedSessions;
  }

  @Override
  public Session newSession(Socket socket) {
    Session session = new SocksSession(++nextSessionId, socket, managedSessions);
    managedSessions.put(session.getId(), session);
    return session;
  }

  @Override
  public void addSessionListener(SessionListener sessionListener) {
    sessionListeners.add(sessionListener);
  }

  @Override
  public void removeSessionListener(SessionListener sessionListener) {
    sessionListeners.remove(sessionListener);
  }

  @Override
  public List<SessionListener> getSessionListeners() {
    return sessionListeners;
  }

  @Override
  public void setSessionListeners(List<SessionListener> sessionListeners) {
    this.sessionListeners = checkNotNull(sessionListeners);
  }

  @Override
  public Session getSession(long id) {
    return managedSessions.get(id);
  }

}
