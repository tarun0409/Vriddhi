package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
	
	private static Connection prabandh;
	public static Connection getConnection(DBUser user)
	{
		if(prabandh==null)
		{
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/prabandh",user.getUserName(),user.getPassword());
				System.out.println("Database connection established successfully");
				prabandh = con;
			}
			catch(SQLException s)
			{
				System.out.println("Unable to establish SQL connection");
				prabandh = null;
			} catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
		return prabandh;
	}

}
