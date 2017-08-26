package data;

import org.json.JSONException;
import org.json.JSONObject;

public class Response {
	
	JSONObject response;
	
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	
	
	public Response()
	{
		response = new JSONObject();
	}
	
	public void setStatus(String status)
	{
		try
		{
			response.put("status", status);
		}
		catch(JSONException je)
		{
			je.printStackTrace();
		}
	}
	
	public void setMessage(String message)
	{
		try
		{
			response.put("message", message);
		}
		catch(JSONException je)
		{
			je.printStackTrace();
		}
	}
	
	public String getResponse()
	{
		return response.toString();	
	}

}
