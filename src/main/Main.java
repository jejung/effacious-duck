package main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import view.View;

class Base {
	
	public static void main(String[] args) {
		System.out.println("Hello");
	}
}

/**
 * @author Jean Jung
 */
public class Main extends Base {
	List<Integer> list = new ArrayList<>();
	
//	/**
//	 * 
//	 */
//	public Main() {
//	}
//
//	/**
//	 * @param args
//	 * @throws IOException  
//	 */
//	public static void main(String[] args) 
//		throws IOException
//	{
//		View view = new View();
//		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		view.pack(); 
//		view.setLocationRelativeTo(null);
//		view.setVisible(true);
//	}
}
