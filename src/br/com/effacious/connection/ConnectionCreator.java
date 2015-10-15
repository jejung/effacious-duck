package br.com.effacious.connection;

import java.net.URL;

class ConnectionCreator {

	static public Connection create(URL url) {
		return new Connection(url);
	}
}