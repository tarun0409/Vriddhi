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
import database.schema.ITEM;
import database.schema.Key;


@Path("items")
public class Item {
	

	

	
	public static String rootElementName = "items";
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllItems(@Context UriInfo uriInfo)
	{
		try
		{
			ArrayList<Integer> itemIds = new ArrayList<Integer>();
			if(uriInfo!=null)
			{
				MultivaluedMap<String,String> queryParams = uriInfo.getQueryParameters();
				String idsString = queryParams.getFirst("ids");
				if(idsString!=null && !idsString.isEmpty())
				{
					String[] ids = idsString.split(",");
					for(int i=0; i<ids.length; i++)
					{
						itemIds.add(Integer.parseInt(ids[i]));
					}
				}
			}
			Query query = new Query();
			Select sq = new Select();
			sq.addSelectColumn(query.new Column(ITEM.TABLE,"*"));
			if(itemIds.size()>0)
			{
				Query.Criteria idCr = query.new Criteria(query.new Column(ITEM.TABLE,ITEM.ITEM_ID),itemIds,Query.comparison_operators.IN);
				sq.setCriteria(idCr);
			}
			Interface dbInt = new Interface();
			JSONObject rs = (JSONObject)dbInt.getData(sq);
			if(rs!=null)
			{
				Field field = new Field(ITEM.TABLE);
				HashMap<String,String> columnVsFieldLabelMap = field.getColumnVsFieldLabelMap();
				JSONArray items = rs.getJSONArray("item");
				JSONArray resItems = new JSONArray();
				for(int i=0; i<items.length(); i++)
				{
					JSONObject itemRes = new JSONObject();
					JSONObject item = items.getJSONObject(i);
					Iterator<?> itemKeys = item.keys();
					while(itemKeys.hasNext())
					{
						String itemKey = (String)itemKeys.next();
						String key = null;
						if(itemKey.equals(Key.getPrimaryKey(ITEM.TABLE)))
						{
							key = "ID";
						}
						else
						{
							key = columnVsFieldLabelMap.get(itemKey);
						}
						Object value = item.get(itemKey);
						itemRes.put(key, value);
					}
					resItems.put(itemRes);
				}
				JSONObject response = new JSONObject();
				response.put(rootElementName, resItems);
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
	public String postItems(String items)
	{
		Response response = new Response();
		int dataLength = 0;
		try
		{
			JSONObject itemsObj = new JSONObject(items);
			JSONArray postItems = itemsObj.getJSONArray(rootElementName);
			Field field = new Field(ITEM.TABLE);
			Query query = new Query();
			HashMap<String,String> fieldLabelVsColumnName = field.getFieldLabelVsColumnMap();
			Insert insertObj = new Insert();
			dataLength = postItems.length();
			for(int i=0; i<dataLength; i++)
			{
				JSONObject postItem = postItems.getJSONObject(i);
				Iterator<?> fieldLabelKeys = postItem.keys();
				HashMap<Query.Column,Object> insertMapEntry = new HashMap<Query.Column,Object>();
				while(fieldLabelKeys.hasNext())
				{
					String fieldLabel = (String)fieldLabelKeys.next();
					String columnName = fieldLabelVsColumnName.get(fieldLabel);
					Object value = postItem.get(fieldLabel);
					Query.Column column = query.new Column(ITEM.TABLE,columnName);
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
	@Path("{itemId}")
	public String updateItem(@PathParam("itemId") Integer itemId, String itemUpdates)
	{
		Response response = new Response();
		try
		{
			JSONObject itemsObj = new JSONObject(itemUpdates);
			JSONArray itemsArray = itemsObj.getJSONArray(rootElementName);
			JSONObject item = itemsArray.getJSONObject(0);
			Update updateObj = new Update();
			updateObj.setUpdateTableName(ITEM.TABLE);
			Iterator<?> itemKeys = item.keys();
			Field field = new Field(ITEM.TABLE);
			HashMap<String,String> fieldLabelVsColumnName = field.getFieldLabelVsColumnMap();
			Query query = new Query();
			while(itemKeys.hasNext())
			{
				String fieldLabel = (String)itemKeys.next();
				String columnName = fieldLabelVsColumnName.get(fieldLabel);
				Object value = item.get(fieldLabel);
				updateObj.setValueForColumn(query.new Column(ITEM.TABLE,columnName), value);
			}
			Query.Criteria uCr = query.new Criteria(query.new Column(ITEM.TABLE,ITEM.ITEM_ID), itemId, Query.comparison_operators.EQUAL_TO);
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
	@Path("{itemId}")
	public String deleteItem(@PathParam("itemId") Integer itemId)
	{
		Response response = new Response();
		Delete deleteObj = new Delete();
		deleteObj.setDeleteTableName(ITEM.TABLE);
		Query query = new Query();
		Query.Criteria dCr = query.new Criteria(query.new Column(ITEM.TABLE,ITEM.ITEM_ID), itemId, Query.comparison_operators.EQUAL_TO);
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
	public String deleteItems(@Context UriInfo uriInfo)
	{
		Response response = new Response();
		MultivaluedMap<String,String> queryParams = uriInfo.getQueryParameters();
		String itemIdsString = queryParams.getFirst("itemIds");
		String[] itemIdStrings = itemIdsString.split(",");
		int dataLen = itemIdStrings.length;
		int deletedLen = 0;
		Query query = new Query();
		for(int i=0; i<dataLen; i++)
		{
			Integer itemId = Integer.parseInt(itemIdStrings[i]);
			Delete deleteObj = new Delete();
			deleteObj.setDeleteTableName(ITEM.TABLE);
			Query.Criteria dCr = query.new Criteria(query.new Column(ITEM.TABLE,ITEM.ITEM_ID), itemId, Query.comparison_operators.EQUAL_TO);
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
