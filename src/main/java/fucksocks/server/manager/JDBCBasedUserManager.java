package fucksocks.server.manager;

import java.util.List;

/**
 * Created by fengyouchao on 8/28/15.
 */
public class JDBCBasedUserManager implements UserManager {

    @Override public void create(User user) {

    }

    @Override public Void addUser(String username, String password) {
        return null;
    }

    @Override public User findUser(String username, String password) {
        return null;
    }

    @Override public User deleteUser(String username) {
        return null;
    }

    @Override public List<User> findAll() {
        return null;
    }
}
