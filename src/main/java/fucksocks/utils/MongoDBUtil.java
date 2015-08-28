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

package fucksocks.utils;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by fengyouchao on 8/23/15.
 */
public class MongoDBUtil {

    private String host;
    private int port = 27017;
    private String username;
    private String password;
    private String databaseName;
    private String collectionName;


    public MongoDBUtil(){}

    public MongoDBUtil(String host, int port, String databaseName, String collectionName) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.collectionName = collectionName;
    }


    public <T> T doJob(MongoDBCallback<T> callback) {
        MongoClient client = null;
        T t = null;
        try {
            client = new MongoClient(host, port);
            MongoDatabase database = client.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);
            t = callback.process(collection);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return t;
    }
}
