package user;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class User {

	public static void main(String[] args) {
		if(Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if(desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					URI uri = new URI("http://oauth2server:8343/OAuth2Server/login");
					desktop.browse(uri);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		    else {
			System.out.println("Desktop action BROWSE is not supported");
		} 
		} else {
			System.out.println("Desktop is not supported in this platform");
		}
	}
}