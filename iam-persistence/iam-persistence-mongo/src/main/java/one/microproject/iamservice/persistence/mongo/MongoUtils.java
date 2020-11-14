package one.microproject.iamservice.persistence.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import one.microproject.iamservice.core.model.keys.ModelKey;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.mongojack.JacksonMongoCollection;

public class MongoUtils {

    public final static String SEPARATOR = "/";

    public static MongoDatabase createMongoDatabase(MongoConfiguration configuration) {
        MongoClient mongoClient = MongoClients.create(configuration.getConnectionString());
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        return mongoClient.getDatabase(configuration.getDatabase()).withCodecRegistry(pojoCodecRegistry);
    }

    public static <T> JacksonMongoCollection<T> createJacksonMongoCollection(MongoConfiguration configuration, Class<T> type, String connectionName) {
        MongoClient mongoClient = MongoClients.create(configuration.getConnectionString());
        return JacksonMongoCollection.builder()
                .build(mongoClient, configuration.getDatabase(), connectionName, type, UuidRepresentation.JAVA_LEGACY);
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
