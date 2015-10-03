package fucksocks.server;

import java.util.List;

/**
 * @author Youchao Feng
 * @version 1.0
 * @date Sep 30, 2015 12:36 PM
 */
public interface SessionManager {

  List<Session> getAllManagedSession();

  void addSessionListener(SessionListener sessionListener);

}
