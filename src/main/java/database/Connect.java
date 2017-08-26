package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
	
	private Connection connection;
	private static Connect connect;
	
	public static Connect getInstance()
	{
		if(connect==null)
		{
			connect = new Connect();
		}
		return connect;
	}
	public Connection getConnection(DBUser user)
	{
		if(connection==null)
		{
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/prabandh",user.getUserName(),user.getPassword());
				System.out.println("Database connection established successfully");
				connection = con;
			}
			catch(SQLException s)
			{
				System.out.println("Unable to establish SQL connection");
				connection = null;
			} catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
		return connection;
	}

}
