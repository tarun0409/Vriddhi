package vriddhi.Vriddhi.records;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import data.Field;
import data.Interface;
import data.Response;
import database.query.Delete;
import database.query.Insert;
import database.query.Query;
import database.query.Select;
import database.query.Update;
import database.schema.ACCOUNT;
import database.schema.Key;

@Path("accounts")
public class Account {
	
	public static String rootElementName = "accounts";
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllAccounts(@Context UriInfo uriInfo)
	{
		try
		{
			ArrayList<Integer> accountIds = new ArrayList<Integer>();
			if(uriInfo!=null)
			{
				MultivaluedMap<String,String> queryParams = uriInfo.getQueryParameters();
				String idsString = queryParams.getFirst("ids");
				if(idsString!=null && !idsString.isEmpty())
				{
					String[] ids = idsString.split(",");
					for(int i=0; i<ids.length; i++)
					{
						accountIds.add(Integer.parseInt(ids[i]));
					}
				}
			}
			Query query = new Query();
			Select sq = new Select();
			sq.addSelectColumn(query.new Column(ACCOUNT.TABLE,"*"));
			if(accountIds.size()>0)
			{
				Query.Criteria idCr = query.new Criteria(query.new Column(ACCOUNT.TABLE,ACCOUNT.ACCOUNT_ID),accountIds,Query.comparison_operators.IN);
				sq.setCriteria(idCr);
			}
			Interface dbInt = new Interface();
			JSONObject rs = (JSONObject)dbInt.getData(sq);
			if(rs!=null)
			{
				Field field = new Field(ACCOUNT.TABLE);
				HashMap<String,String> columnVsFieldLabelMap = field.getColumnVsFieldLabelMap();
				JSONArray accounts = rs.getJSONArray("account");
				JSONArray resAccounts = new JSONArray();
				for(int i=0; i<accounts.length(); i++)
				{
					JSONObject accountRes = new JSONObject();
					JSONObject account = accounts.getJSONObject(i);
					Iterator<?> accountKeys = account.keys();
					while(accountKeys.hasNext())
					{
						String accountKey = (String)accountKeys.next();
						String key = null;
						if(accountKey.equals(Key.getPrimaryKey(ACCOUNT.TABLE)))
						{
							key = "ID";
						}
						else
						{
							key = columnVsFieldLabelMap.get(accountKey);
						}
						Object value = account.get(accountKey);
						accountRes.put(key, value);
					}
					resAccounts.put(accountRes);
				}
				JSONObject response = new JSONObject();
				response.put(rootElementName, resAccounts);
				return response.toString();
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String postAccounts(String accounts)
	{
		Response response = new Response();
		int dataLength = 0;
		try
		{
			JSONObject accountsObj = new JSONObject(accounts);
			JSONArray postAccounts = accountsObj.getJSONArray(rootElementName);
			Field field = new Field(ACCOUNT.TABLE);
			Query query = new Query();
			HashMap<String,String> fieldLabelVsColumnName = field.getFieldLabelVsColumnMap();
			Insert insertObj = new Insert();
			dataLength = postAccounts.length();
			for(int i=0; i<dataLength; i++)
			{
				JSONObject postAccount = postAccounts.getJSONObject(i);
				Iterator<?> fieldLabelKeys = postAccount.keys();
				HashMap<Query.Column,Object> insertMapEntry = new HashMap<Query.Column,Object>();
				while(fieldLabelKeys.hasNext())
				{
					String fieldLabel = (String)fieldLabelKeys.next();
					String columnName = fieldLabelVsColumnName.get(fieldLabel);
					Object value = postAccount.get(fieldLabel);
					Query.Column column = query.new Column(ACCOUNT.TABLE,columnName);
					insertMapEntry.put(column, value);
				}
				insertObj.addInsertEntry(insertMapEntry);
			}
			Interface dbInt = new Interface();
			int rs = dbInt.insertData(insertObj);
			if(rs>0)
			{
				response.setStatus(Response.SUCCESS);
				response.setMessage(rs+" out of "+dataLength+" records inserted successfully");
			}
			else
			{
				response.setStatus(Response.FAILURE);
				response.setMessage("Something went wrong while inserting records");
			}
			
			
		}
		catch(JSONException je)
		{
			response.setStatus(Response.FAILURE);
			response.setMessage("Please give a valid JSONObject as input to post request : "+je.getMessage());
		}
		return response.getResponse();
	}

		
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{accountId}")
	public String updateAccount(@PathParam("accountId") Integer accountId, String accountUpdates)
	{
		Response response = new Response();
		try
		{
			JSONObject accountsObj = new JSONObject(accountUpdates);
			JSONArray accountsArray = accountsObj.getJSONArray(rootElementName);
			JSONObject account = accountsArray.getJSONObject(0);
			Update updateObj = new Update();
			updateObj.setUpdateTableName(ACCOUNT.TABLE);
			Iterator<?> accountKeys = account.keys();
			Field field = new Field(ACCOUNT.TABLE);
			HashMap<String,String> fieldLabelVsColumnName = field.getFieldLabelVsColumnMap();
			Query query = new Query();
			while(accountKeys.hasNext())
			{
				String fieldLabel = (String)accountKeys.next();
				String columnName = fieldLabelVsColumnName.get(fieldLabel);
				Object value = account.get(fieldLabel);
				updateObj.setValueForColumn(query.new Column(ACCOUNT.TABLE,columnName), value);
			}
			Query.Criteria uCr = query.new Criteria(query.new Column(ACCOUNT.TABLE,ACCOUNT.ACCOUNT_ID), accountId, Query.comparison_operators.EQUAL_TO);
			updateObj.setCriteria(uCr);
			Interface dbInt = new Interface();
			int rs = dbInt.updateData(updateObj);
			if(rs>0)
			{
				response.setStatus(Response.SUCCESS);
				response.setMessage(rs+" record updated successfully");
			}
			else
			{
				response.setStatus(Response.FAILURE);
				response.setMessage("Something went wrong while updating records");
			}
		}
		catch(Exception e)
		{
			response.setStatus(Response.FAILURE);
			response.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return response.getResponse();
	}

	
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{accountId}")
	public String deleteAccount(@PathParam("accountId") Integer accountId)
	{
		Response response = new Response();
		Delete deleteObj = new Delete();
		deleteObj.setDeleteTableName(ACCOUNT.TABLE);
		Query query = new Query();
		Query.Criteria dCr = query.new Criteria(query.new Column(ACCOUNT.TABLE,ACCOUNT.ACCOUNT_ID), accountId, Query.comparison_operators.EQUAL_TO);
		deleteObj.setDeleteCriteria(dCr);
		Interface dbInt = new Interface();
		int rs = dbInt.deleteData(deleteObj);
		if(rs>0)
		{
			response.setStatus(Response.SUCCESS);
			response.setMessage("1 record deleted successfully");
		}
		else
		{
			response.setStatus(Response.FAILURE);
			response.setMessage("Something went wrong in deleting records");
		}
		return response.getResponse();
	}
	
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteAccounts(@Context UriInfo uriInfo)
	{
		Response response = new Response();
		MultivaluedMap<String,String> queryParams = uriInfo.getQueryParameters();
		String accountIdsString = queryParams.getFirst("accountIds");
		String[] accountIdStrings = accountIdsString.split(",");
		int dataLen = accountIdStrings.length;
		int deletedLen = 0;
		Query query = new Query();
		for(int i=0; i<dataLen; i++)
		{
			Integer accountId = Integer.parseInt(accountIdStrings[i]);
			Delete deleteObj = new Delete();
			deleteObj.setDeleteTableName(ACCOUNT.TABLE);
			Query.Criteria dCr = query.new Criteria(query.new Column(ACCOUNT.TABLE,ACCOUNT.ACCOUNT_ID), accountId, Query.comparison_operators.EQUAL_TO);
			deleteObj.setDeleteCriteria(dCr);
			Interface dbInt = new Interface();
			int rs = dbInt.deleteData(deleteObj);
			if(rs>0)
			{
				deletedLen++;
			}
		}
		if(deletedLen>0)
		{
			response.setStatus(Response.SUCCESS);
			response.setMessage(deletedLen+" records out of "+dataLen+" records deleted successfully");
		}
		else
		{
			response.setStatus(Response.FAILURE);
			response.setMessage("Something went wrong in deleting records");
		}
		return response.getResponse();
	}
	
}
