package br.com.efficacious.config;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.efficacious.io.LuceneDirectory;

public class LoadArgumentsResources extends Thread {

	private String[] args;

	public LoadArgumentsResources(String[] args) {
		this.args = args;
	}

	private void loadResources() {

		Proxy proxy = null;

		String luceneDirectory = System.getProperty("user.dir") + "/index";

		for (int i = 0; i < args.length; i++) {
			try {
				if ("-p".equals(args[i])) {
					i++;
					String host = args[i].split(":")[0];
					int port = Integer.parseInt(args[i].split(":")[1]);

					proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
				}

				if ("-d".equals(args[i])) {

					i++;
					luceneDirectory = args[i];

				}

			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, String.format("Invalid argument[%d]: %s", i, e.getMessage(), e));
				throw e;
			}
		}

		LuceneDirectory.setDirectory(luceneDirectory);
		LuceneDirectory.getLatch().countDown();
		
//		NetConfig.instance().setProxy(proxy);
//		NetConfig.instance().getLatch().countDown();		
	}

	@Override
	public void run() {
		loadResources();
	}
}
