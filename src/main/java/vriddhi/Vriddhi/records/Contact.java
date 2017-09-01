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
import database.schema.CONTACT;
import database.schema.Key;

@Path("contacts")
public class Contact {
	

	
	public static String rootElementName = "contacts";
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllContacts(@Context UriInfo uriInfo)
	{
		try
		{
			ArrayList<Integer> contactIds = new ArrayList<Integer>();
			if(uriInfo!=null)
			{
				MultivaluedMap<String,String> queryParams = uriInfo.getQueryParameters();
				String idsString = queryParams.getFirst("ids");
				if(idsString!=null && !idsString.isEmpty())
				{
					String[] ids = idsString.split(",");
					for(int i=0; i<ids.length; i++)
					{
						contactIds.add(Integer.parseInt(ids[i]));
					}
				}
			}
			Query query = new Query();
			Select sq = new Select();
			sq.addSelectColumn(query.new Column(CONTACT.TABLE,"*"));
			if(contactIds.size()>0)
			{
				Query.Criteria idCr = query.new Criteria(query.new Column(CONTACT.TABLE,CONTACT.CONTACT_ID),contactIds,Query.comparison_operators.IN);
				sq.setCriteria(idCr);
			}
			Interface dbInt = new Interface();
			JSONObject rs = (JSONObject)dbInt.getData(sq);
			if(rs!=null)
			{
				Field field = new Field(CONTACT.TABLE);
				HashMap<String,String> columnVsFieldLabelMap = field.getColumnVsFieldLabelMap();
				JSONArray contacts = rs.getJSONArray("contact");
				JSONArray resContacts = new JSONArray();
				for(int i=0; i<contacts.length(); i++)
				{
					JSONObject contactRes = new JSONObject();
					JSONObject contact = contacts.getJSONObject(i);
					Iterator<?> contactKeys = contact.keys();
					while(contactKeys.hasNext())
					{
						String contactKey = (String)contactKeys.next();
						String key = null;
						if(contactKey.equals(Key.getPrimaryKey(CONTACT.TABLE)))
						{
							key = "ID";
						}
						else
						{
							key = columnVsFieldLabelMap.get(contactKey);
						}
						Object value = contact.get(contactKey);
						contactRes.put(key, value);
					}
					resContacts.put(contactRes);
				}
				JSONObject response = new JSONObject();
				response.put(rootElementName, resContacts);
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
	public String postContacts(String contacts)
	{
		Response response = new Response();
		int dataLength = 0;
		try
		{
			JSONObject contactsObj = new JSONObject(contacts);
			JSONArray postContacts = contactsObj.getJSONArray(rootElementName);
			Field field = new Field(CONTACT.TABLE);
			Query query = new Query();
			HashMap<String,String> fieldLabelVsColumnName = field.getFieldLabelVsColumnMap();
			Insert insertObj = new Insert();
			dataLength = postContacts.length();
			for(int i=0; i<dataLength; i++)
			{
				JSONObject postContact = postContacts.getJSONObject(i);
				Iterator<?> fieldLabelKeys = postContact.keys();
				HashMap<Query.Column,Object> insertMapEntry = new HashMap<Query.Column,Object>();
				while(fieldLabelKeys.hasNext())
				{
					String fieldLabel = (String)fieldLabelKeys.next();
					String columnName = fieldLabelVsColumnName.get(fieldLabel);
					Object value = postContact.get(fieldLabel);
					Query.Column column = query.new Column(CONTACT.TABLE,columnName);
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
	@Path("{contactId}")
	public String updateContact(@PathParam("contactId") Integer contactId, String contactUpdates)
	{
		Response response = new Response();
		try
		{
			JSONObject contactsObj = new JSONObject(contactUpdates);
			JSONArray contactsArray = contactsObj.getJSONArray(rootElementName);
			JSONObject contact = contactsArray.getJSONObject(0);
			Update updateObj = new Update();
			updateObj.setUpdateTableName(CONTACT.TABLE);
			Iterator<?> contactKeys = contact.keys();
			Field field = new Field(CONTACT.TABLE);
			HashMap<String,String> fieldLabelVsColumnName = field.getFieldLabelVsColumnMap();
			Query query = new Query();
			while(contactKeys.hasNext())
			{
				String fieldLabel = (String)contactKeys.next();
				String columnName = fieldLabelVsColumnName.get(fieldLabel);
				Object value = contact.get(fieldLabel);
				updateObj.setValueForColumn(query.new Column(CONTACT.TABLE,columnName), value);
			}
			Query.Criteria uCr = query.new Criteria(query.new Column(CONTACT.TABLE,CONTACT.CONTACT_ID), contactId, Query.comparison_operators.EQUAL_TO);
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
	@Path("{contactId}")
	public String deleteContact(@PathParam("contactId") Integer contactId)
	{
		Response response = new Response();
		Delete deleteObj = new Delete();
		deleteObj.setDeleteTableName(CONTACT.TABLE);
		Query query = new Query();
		Query.Criteria dCr = query.new Criteria(query.new Column(CONTACT.TABLE,CONTACT.CONTACT_ID), contactId, Query.comparison_operators.EQUAL_TO);
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
	public String deleteContacts(@Context UriInfo uriInfo)
	{
		Response response = new Response();
		MultivaluedMap<String,String> queryParams = uriInfo.getQueryParameters();
		String contactIdsString = queryParams.getFirst("contactIds");
		String[] contactIdStrings = contactIdsString.split(",");
		int dataLen = contactIdStrings.length;
		int deletedLen = 0;
		Query query = new Query();
		for(int i=0; i<dataLen; i++)
		{
			Integer contactId = Integer.parseInt(contactIdStrings[i]);
			Delete deleteObj = new Delete();
			deleteObj.setDeleteTableName(CONTACT.TABLE);
			Query.Criteria dCr = query.new Criteria(query.new Column(CONTACT.TABLE,CONTACT.CONTACT_ID), contactId, Query.comparison_operators.EQUAL_TO);
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
