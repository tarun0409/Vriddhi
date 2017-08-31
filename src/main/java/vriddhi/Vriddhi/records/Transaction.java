package vriddhi.Vriddhi.records;

import java.util.HashMap;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import database.schema.TRANSACTION;
import database.schema.Key;

@Path("transactions")
public class Transaction {
	

	

	
	public static String rootElementName = "transactions";
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllTransactions(@Context UriInfo uriInfo)
	{
		try
		{
			Integer limit = 0;
			if(uriInfo!=null)
			{
				MultivaluedMap<String,String> queryParams = uriInfo.getQueryParameters();
				String limitString = queryParams.getFirst("limit");
				if(limitString!=null && !limitString.isEmpty())
				{
					limit = Integer.parseInt(limitString);
				}
			}
			
			Query query = new Query();
			Select sq = new Select();
			sq.addSelectColumn(query.new Column(TRANSACTION.TABLE,"*"));
			sq.addOrderByColumn(query.new Column(TRANSACTION.TABLE,TRANSACTION.CREATED_TIME));
			sq.setSortingOrder(Select.order.DESC);
			if(limit>0)
			{
				sq.setLimit(limit);
			}
			Interface dbInt = new Interface();
			JSONObject rs = (JSONObject)dbInt.getData(sq);
			if(rs!=null)
			{
				Field field = new Field(TRANSACTION.TABLE);
				HashMap<String,String> columnVsFieldLabelMap = field.getColumnVsFieldLabelMap();
				JSONArray transactions = rs.getJSONArray("transaction");
				JSONArray resTransactions = new JSONArray();
				for(int i=0; i<transactions.length(); i++)
				{
					JSONObject transactionRes = new JSONObject();
					JSONObject transaction = transactions.getJSONObject(i);
					Iterator<?> transactionKeys = transaction.keys();
					while(transactionKeys.hasNext())
					{
						String transactionKey = (String)transactionKeys.next();
						String key = null;
						if(transactionKey.equals(Key.getPrimaryKey(TRANSACTION.TABLE)))
						{
							key = "ID";
						}
						else
						{
							key = columnVsFieldLabelMap.get(transactionKey);
						}
						Object value = transaction.get(transactionKey);
						if(key!=null && value!=null)
						{
							transactionRes.put(key, value);
						}
						if(key!=null && value==null)
						{
							transactionRes.put(key, JSONObject.NULL);
						}
					}
					resTransactions.put(transactionRes);
				}
				JSONObject response = new JSONObject();
				response.put(rootElementName, resTransactions);
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
	public String postTransactions(String transactions)
	{
		Response response = new Response();
		int dataLength = 0;
		try
		{
			JSONObject transactionsObj = new JSONObject(transactions);
			JSONArray postTransactions = transactionsObj.getJSONArray(rootElementName);
			Field field = new Field(TRANSACTION.TABLE);
			Query query = new Query();
			HashMap<String,String> fieldLabelVsColumnName = field.getFieldLabelVsColumnMap();
			Insert insertObj = new Insert();
			dataLength = postTransactions.length();
			for(int i=0; i<dataLength; i++)
			{
				JSONObject postTransaction = postTransactions.getJSONObject(i);
				Iterator<?> fieldLabelKeys = postTransaction.keys();
				HashMap<Query.Column,Object> insertMapEntry = new HashMap<Query.Column,Object>();
				while(fieldLabelKeys.hasNext())
				{
					String fieldLabel = (String)fieldLabelKeys.next();
					String columnName = fieldLabelVsColumnName.get(fieldLabel);
					Object value = postTransaction.get(fieldLabel);
					Query.Column column = query.new Column(TRANSACTION.TABLE,columnName);
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
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{transactionId}")
	public String deleteTransaction(@PathParam("transactionId") Integer transactionId)
	{
		Response response = new Response();
		Delete deleteObj = new Delete();
		deleteObj.setDeleteTableName(TRANSACTION.TABLE);
		Query query = new Query();
		Query.Criteria dCr = query.new Criteria(query.new Column(TRANSACTION.TABLE,TRANSACTION.TRANSACTION_ID), transactionId, Query.comparison_operators.EQUAL_TO);
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
	public String deleteTransactions(@Context UriInfo uriInfo)
	{
		Response response = new Response();
		MultivaluedMap<String,String> queryParams = uriInfo.getQueryParameters();
		String transactionIdsString = queryParams.getFirst("transactionIds");
		String[] transactionIdStrings = transactionIdsString.split(",");
		int dataLen = transactionIdStrings.length;
		int deletedLen = 0;
		Query query = new Query();
		for(int i=0; i<dataLen; i++)
		{
			Integer transactionId = Integer.parseInt(transactionIdStrings[i]);
			Delete deleteObj = new Delete();
			deleteObj.setDeleteTableName(TRANSACTION.TABLE);
			Query.Criteria dCr = query.new Criteria(query.new Column(TRANSACTION.TABLE,TRANSACTION.TRANSACTION_ID), transactionId, Query.comparison_operators.EQUAL_TO);
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
