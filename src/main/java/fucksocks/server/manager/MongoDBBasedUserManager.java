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

package fucksocks.server.manager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import fucksocks.utils.MongoDBCallback;
import fucksocks.utils.MongoDBUtil;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>MongoDBBasedUserManager</code> can manage user in MongoDB.
 *
 * Created by fengyouchao on 8/23/15.
 *
 * @author fengyouchao
 */
public class MongoDBBasedUserManager implements UserManager {

    private static final Logger logger = LoggerFactory.getLogger(MongoDBBasedUserManager.class);
    private static final String COLLECTION_NAME = "users";
    private static final String USER_USERNAME_KEY = "username";
    private static final String USER_PASSWORD_KEY = "password";

    private LoadingCache<String, User> cache;

    //MongoDB configuration
    private String host = "localhost";
    private int port;
    private String dbName;
    private String username;
    private String password;
    private String collectionName = COLLECTION_NAME;
    private String databaseName;
    private MongoDBUtil mongoDBUtil;


    /**
     * Constructs a <code>MongoDBBasedUserManager</code> and add some users;
     *
     * @param users Users which will be create.
     */
    public MongoDBBasedUserManager(User... users){
        for (User user : users) {
            create(user);
        }
    }

    public MongoDBBasedUserManager() {
        cache = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<String, User>() {
            @Override public User load(String key) throws Exception {
                User user = null;
                user  = fetchUserFromMongoDB(key);
                if(user == null){
                    return new User();
                }else {
                    return user;
                }
            }

        });
        host = "localhost";
        collectionName = "users";
        port = 27017;
        databaseName = "fucksocks";
    }

    public User fetchUserFromMongoDB(final String username) {
        if (mongoDBUtil == null) {
            mongoDBUtil = initMongoDBUtil();
        }
        return mongoDBUtil.doJob(new MongoDBCallback<User>() {
            @Override public User process(MongoCollection<Document> collection) {
                FindIterable<Document> result =
                    collection.find(new Document(USER_USERNAME_KEY, username));
                for (Document document : result) {
                    return formUser(document);
                }
                return null;
            }
        });
    }

    private MongoDBUtil initMongoDBUtil() {
        System.out.println(collectionName);
        return new MongoDBUtil(host, port, databaseName, collectionName);
    }

    @Override public void create(User user) {

    }

    @Override public Void addUser(final String username, final String password) {
        if (mongoDBUtil == null) {
            mongoDBUtil = initMongoDBUtil();
        }
        return mongoDBUtil.doJob(new MongoDBCallback<Void>() {
            @Override public Void process(MongoCollection<Document> collection) {
                collection.insertOne(new Document().append(USER_USERNAME_KEY, username)
                    .append(USER_PASSWORD_KEY, password));
                return null;
            }
        });
    }

    @Override public User findUser(String username, String password) {
        User user = cache.getUnchecked(username);
        if(user.getUsername() == null){
            return null;
        }
        if (user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override public User deleteUser(String username) {
        return null;
    }

    @Override public List<User> findAll() {
        if (mongoDBUtil == null) {
            mongoDBUtil = initMongoDBUtil();
        }
        return mongoDBUtil.doJob(new MongoDBCallback<List<User>>() {
            @Override public List<User> process(MongoCollection<Document> collection) {
                FindIterable<Document> result = collection.find();
                List<User> users = new ArrayList<User>();
                for (Document document : result) {
                    users.add(formUser(document));
                }
                return users;
            }
        });
    }

    private User formUser(Document document) {
        User user = new User();
        user.setUsername(document.getString(USER_USERNAME_KEY));
        user.setPassword(document.getString(USER_PASSWORD_KEY));
        return user;
    }


    public LoadingCache<String, User> getCache() {
        return cache;
    }

    public void setCache(LoadingCache<String, User> cache) {
        this.cache = cache;
    }
}
