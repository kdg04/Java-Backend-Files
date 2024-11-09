package mongoutil;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoUtil {
	private static final ThreadLocal<MongoDatabase> db = new ThreadLocal<>();

	public static MongoDatabase getDatabase() {
		if (db.get() == null) {
			MongoClient mongoClient = MongoClients.create(
					  "mongodb://rootAdmin:rootAdmin@localhost:27017/?authSource=AdminDB&authMechanism=SCRAM-SHA-256");			
			db.set(mongoClient.getDatabase("AdminDB"));
		}
		return db.get();
	}

}
