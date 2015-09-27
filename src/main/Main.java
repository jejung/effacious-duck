package main;
import java.io.IOException;

import javax.swing.JFrame;

import view.View;

/**
 * @author Jean Jung
 */
public class Main {
	
	/**
	 * 
	 */
	public Main() {
	}

	/**
	 * @param args
	 * @throws IOException  
	 */
	public static void main(String[] args) 
		throws IOException
	{
		View view = new View();
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.pack(); 
		view.setLocationRelativeTo(null);
		view.setVisible(true);
	}
}
