package fucksocks.server.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created by fengyouchao on 8/28/15.
 *
 */
public class FileBasedUserManager implements UserManager {

    private File storeFile;
    private StoreType storeType = StoreType.PROPERTIES;
    private List<User> managedUsers;
    private boolean autoReload = false;
    private long reloadAfter = 20000;
    private AutoReloadService autoReloadService;

    public FileBasedUserManager(File storeFile, StoreType storeType) throws IOException {
        this.storeFile = storeFile;
        this.storeType = storeType;
        loadFromFile();
    }

    public FileBasedUserManager(File storeFile) throws IOException {
        this(storeFile, StoreType.PROPERTIES);
    }

    public FileBasedUserManager(String storeFile, boolean autoReload, long reloadAfter) throws IOException {
        if(storeFile.startsWith("classpath:")) {
            storeFile = storeFile.split(":")[1];
            storeFile = this.getClass().getResource("/"+storeFile).getPath();
        }
        this.storeFile = new File(storeFile);
        this.autoReload = autoReload;
        this.reloadAfter = reloadAfter;
        loadFromFile();
        if(this.autoReload){
            autoReloadService = new AutoReloadService(this.reloadAfter);
            autoReloadService.start();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new FileBasedUserManager("classpath:users.properties", true, 2000);
        Thread.sleep(800000000);
    }

    private void synchronizedWithFile() {

    }

    private void loadFromFile() throws IOException {
        if(managedUsers == null){
            managedUsers = new ArrayList<>();
        }
        Properties properties = new Properties();
        properties.load(new FileInputStream(storeFile));
        Enumeration enum1 = properties.propertyNames();
        while (enum1.hasMoreElements()) {
            String username = (String) enum1.nextElement();
            String password = properties.getProperty(username);
            System.out.println(username + "=" + password);
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            managedUsers.add(user);
        }
    }

    @Override public void create(User user) {

    }

    @Override public Void addUser(String username, String password) {
        return null;
    }

    @Override public User findUser(String username, String password) {
        for (User user : managedUsers) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    @Override public User deleteUser(String username) {
        return null;
    }

    @Override public List<User> findAll() {
        return null;
    }

    public enum StoreType {
        PROPERTIES
    }

    private class AutoReloadService implements Runnable {

        private Thread thread;
        private long reloadAfter;

        public AutoReloadService(long reloadAfter){
           this.reloadAfter = reloadAfter;
        }

        public void start(){
            thread = new Thread(this, "AutoReloadService");
            thread.setDaemon(true);
            thread.start();;
        }

        @Override public void run() {
            while(true){
                try {
                    Thread.sleep(reloadAfter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    loadFromFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
