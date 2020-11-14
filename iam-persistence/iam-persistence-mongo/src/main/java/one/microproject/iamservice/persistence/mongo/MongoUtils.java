package one.microproject.iamservice.persistence.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import one.microproject.iamservice.core.model.keys.ModelKey;
import org.bson.UuidRepresentation;
import org.mongojack.JacksonMongoCollection;

public class MongoUtils {

    public final static String SEPARATOR = "/";

    public static <T> JacksonMongoCollection<T> createJacksonMongoCollection(MongoConfiguration configuration, Class<T> type, String collectionName) {
        MongoClient mongoClient = MongoClients.create(configuration.getConnectionString());
        return JacksonMongoCollection.builder()
                .build(mongoClient, configuration.getDatabase(), collectionName, type, UuidRepresentation.JAVA_LEGACY);
    }

    public static <T> String convertToId(ModelKey<T> key) {
        String stringKey = "";
        for (int i=0; i<key.getIds().length; i++) {
            stringKey = stringKey + key.getIds()[i].getId();
            if (i < (key.getIds().length - 1)) {
                stringKey = stringKey + SEPARATOR;
            }
        }
        return stringKey;
    }

}
