package vriddhi.Vriddhi.user;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import database.Connect;
import database.DBUser;
import java.sql.Connection;

@Path("authenticate")
public class Authentication {
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String authenticateUser(String authInfo)
	{
		JSONObject response = new JSONObject();
		try
		{
			JSONObject authObj = new JSONObject(authInfo);
			DBUser dbUser = new DBUser();
			dbUser.setUserName(authObj.getString("user_name"));
			dbUser.setPassword(authObj.getString("password"));
			Connect connect = Connect.getInstance();
			Connection connection = connect.getConnection(dbUser);
			if(connection!=null)
			{
				response.put("status", "SUCCESS");
				response.put("message", "Authentication successful");
			}
			else
			{
				response.put("status", "FAILURE");
				response.put("message", "Authentication failure");
			}
		}
		catch(JSONException je)
		{
			System.out.println("Something went wrong while parsing json object "+je.getMessage());
			je.printStackTrace();
		}
		return response.toString();
		
	}

}
