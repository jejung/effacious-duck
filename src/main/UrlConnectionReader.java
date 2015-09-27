package main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * A complete Java class that demonstrates how to read content (text) from a URL
 * using the Java URL and URLConnection classes.
 * 
 * @author alvin alexander, devdaily.com
 */
public class UrlConnectionReader {

	/**
	 * 
	 */
	public UrlConnectionReader() {
		super();
	}
	
	public InputStream getInputStream(String theUrl) 
			throws IOException 
	{
		URL url = new URL(theUrl);

		// create a urlconnection object
		URLConnection urlConnection = url.openConnection();
		
		return urlConnection.getInputStream();
	}

	public String getUrlContents(String theUrl) throws IOException {
		StringBuilder content = new StringBuilder();

		// many of these calls can throw exceptions, so i've just
		// wrapped them all in one try/catch statement.
		// create a url object
		URL url = new URL(theUrl);

		// create a urlconnection object
		URLConnection urlConnection = url.openConnection();
		try (
		// wrap the urlconnection in a bufferedreader
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(urlConnection.getInputStream()));) {
			String line;
			// read from the urlconnection via the bufferedreader
			while ((line = bufferedReader.readLine()) != null) {
				content.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content.toString();
	}
}
