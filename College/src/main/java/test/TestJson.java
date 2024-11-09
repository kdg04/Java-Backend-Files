package test;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class TestJson {

	public static void main(String[] args) {
		String s = "{\"userID\":23,\"messages\":[{\"date\":\"19/2/2024\",\"from\":\"Kavita\",\"msg\":\"You are requested to stay back late till 7 pm\",\"seen\":\"true\"},{\"date\":\"15/3/2024\",\"from\":\"Admin\",\"msg\":\"Coming Monday will be a holiday\",\"seen\":\"false\"},{\"date\":\"25/3/2024\",\"from\":\"Admin\",\"msg\":\"Please get the terminal 5 repaired at the earliest\",\"seen\":\"true\"},{\"date\":\"06/04/2024\",\"from\":\"Admin\",\"msg\":\"Please contribute a part of your salary to college improvement fund.\",\"seen\":\"false\"},{\"date\":\"06/04/2024\",\"from\":\"Admin\",\"msg\":\"hi sir meeting within 15 mn.\",\"seen\":\"true\"},{\"date\":\"08/04/2024\",\"from\":\"Admin\",\"msg\":\"gather in 5 minutes\",\"seen\":\"false\"}]};\r\n";
		JSONObject json = new JSONObject(s);
        int Id = json.getInt("userID");
        JSONArray list = json.getJSONArray("messages");	
        System.out.println("id : " + Id);
        System.out.println("ARR : " + list);
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");
        MongoDatabase database = mongoClient.getDatabase("Users");
	    MongoCollection collection = database.getCollection("staffList");
	    Document doc = new Document("userID", Id).append("msgs", list.toList());
	    collection.insertOne(doc);
	    
	}

}
