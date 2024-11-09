package userEntity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import javax.net.ssl.HttpsURLConnection;

public class User {

	public static void main(String[] args) {
		URI uri = URI.create("https://oauth2server:8343/OAuth2Server/Permit");
		
		 HttpsURLConnection connection;
		try {
			connection = (HttpsURLConnection) uri.toURL().openConnection();
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			if(responseCode == 200)
				connection.disconnect();
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

	    
	        
	}

}
