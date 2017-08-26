package vriddhi.Vriddhi.records;

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
import database.schema.SAREE;
import database.schema.Key;

public class Saree {
	

	

	
	public static String rootElementName = "sarees";
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllSarees()
	{
		try
		{
			Query query = new Query();
			Select sq = new Select();
			sq.addSelectColumn(query.new Column(SAREE.TABLE,"*"));
			Interface dbInt = new Interface();
			JSONObject rs = (JSONObject)dbInt.getData(sq);
			if(rs!=null)
			{
				Field field = new Field(SAREE.TABLE);
				HashMap<String,String> columnVsFieldLabelMap = field.getColumnVsFieldLabelMap();
				JSONArray sarees = rs.getJSONArray("saree");
				JSONArray resSarees = new JSONArray();
				for(int i=0; i<sarees.length(); i++)
				{
					JSONObject sareeRes = new JSONObject();
					JSONObject saree = sarees.getJSONObject(i);
					Iterator<?> sareeKeys = saree.keys();
					while(sareeKeys.hasNext())
					{
						String sareeKey = (String)sareeKeys.next();
						String key = null;
						if(sareeKey.equals(Key.getPrimaryKey(SAREE.TABLE)))
						{
							key = "ID";
						}
						else
						{
							key = columnVsFieldLabelMap.get(sareeKey);
						}
						Object value = saree.get(sareeKey);
						sareeRes.put(key, value);
					}
					resSarees.put(sareeRes);
				}
				JSONObject response = new JSONObject();
				response.put(rootElementName, resSarees);
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
	public String postSarees(String sarees)
	{
		Response response = new Response();
		int dataLength = 0;
		try
		{
			JSONObject sareesObj = new JSONObject(sarees);
			JSONArray postSarees = sareesObj.getJSONArray(rootElementName);
			Field field = new Field(SAREE.TABLE);
			Query query = new Query();
			HashMap<String,String> fieldLabelVsColumnName = field.getFieldLabelVsColumnMap();
			Insert insertObj = new Insert();
			dataLength = postSarees.length();
			for(int i=0; i<dataLength; i++)
			{
				JSONObject postSaree = postSarees.getJSONObject(i);
				Iterator<?> fieldLabelKeys = postSaree.keys();
				HashMap<Query.Column,Object> insertMapEntry = new HashMap<Query.Column,Object>();
				while(fieldLabelKeys.hasNext())
				{
					String fieldLabel = (String)fieldLabelKeys.next();
					String columnName = fieldLabelVsColumnName.get(fieldLabel);
					Object value = postSaree.get(fieldLabel);
					Query.Column column = query.new Column(SAREE.TABLE,columnName);
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
	@Path("{sareeId}")
	public String updateSaree(@PathParam("sareeId") Integer sareeId, String sareeUpdates)
	{
		Response response = new Response();
		try
		{
			JSONObject sareesObj = new JSONObject(sareeUpdates);
			JSONArray sareesArray = sareesObj.getJSONArray(rootElementName);
			JSONObject saree = sareesArray.getJSONObject(0);
			Update updateObj = new Update();
			updateObj.setUpdateTableName(SAREE.TABLE);
			Iterator<?> sareeKeys = saree.keys();
			Field field = new Field(SAREE.TABLE);
			HashMap<String,String> fieldLabelVsColumnName = field.getFieldLabelVsColumnMap();
			Query query = new Query();
			while(sareeKeys.hasNext())
			{
				String fieldLabel = (String)sareeKeys.next();
				String columnName = fieldLabelVsColumnName.get(fieldLabel);
				Object value = saree.get(fieldLabel);
				updateObj.setValueForColumn(query.new Column(SAREE.TABLE,columnName), value);
			}
			Query.Criteria uCr = query.new Criteria(query.new Column(SAREE.TABLE,SAREE.SAREE_ID), sareeId, Query.comparison_operators.EQUAL_TO);
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
	@Path("{sareeId}")
	public String deleteSaree(@PathParam("sareeId") Integer sareeId)
	{
		Response response = new Response();
		Delete deleteObj = new Delete();
		deleteObj.setDeleteTableName(SAREE.TABLE);
		Query query = new Query();
		Query.Criteria dCr = query.new Criteria(query.new Column(SAREE.TABLE,SAREE.SAREE_ID), sareeId, Query.comparison_operators.EQUAL_TO);
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
	public String deleteSarees(@Context UriInfo uriInfo)
	{
		Response response = new Response();
		MultivaluedMap<String,String> queryParams = uriInfo.getQueryParameters();
		String sareeIdsString = queryParams.getFirst("sareeIds");
		String[] sareeIdStrings = sareeIdsString.split(",");
		int dataLen = sareeIdStrings.length;
		int deletedLen = 0;
		Query query = new Query();
		for(int i=0; i<dataLen; i++)
		{
			Integer sareeId = Integer.parseInt(sareeIdStrings[i]);
			Delete deleteObj = new Delete();
			deleteObj.setDeleteTableName(SAREE.TABLE);
			Query.Criteria dCr = query.new Criteria(query.new Column(SAREE.TABLE,SAREE.SAREE_ID), sareeId, Query.comparison_operators.EQUAL_TO);
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
