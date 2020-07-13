package simple_java.wallet.ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

import simple_java.wallet.service.UserServiceImpl;

public class App 
{
	private static Connection connection;
	private static Scanner sc = new Scanner(System.in);
    
	public static Scanner getSc() {
		return sc;
	}
	
	private static void closeScanner() {
		sc.close();
	}
	
	public static void setConnection(Connection connection) {
		App.connection = connection;
	}

	public static Connection getConnection() {
		return connection;
	}
	
	private static void setConnection() {
		try {
			App.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/banking", "root", "");
		} catch (SQLException e) {
			System.out.println("Couldn't establish Connection");
			e.printStackTrace();
		}
	}
	
	private static void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println("Couldn't Close Connection");
			e.printStackTrace();
		}
	}
	
	public static void main( String[] args )
    {
    	try {
    		
            //Load JDBC Driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			//Create Connection
			setConnection();
			
			UserServiceImpl service = new UserServiceImpl();
			service.App();
			
			//Closing Scanner
			closeScanner();
			//Closing Connection
			closeConnection();
			
		} catch (ClassNotFoundException e) {
			System.out.println("Couldn't Load Driver Class");
			e.printStackTrace();
		}
    }
}
