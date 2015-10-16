package br.com.efficacious.connection;

import java.net.URL;

class ConnectionCreator {

	public static Connection create(URL url) {
		return new Connection(url);
	}
}