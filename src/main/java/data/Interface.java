package data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import database.Connect;
import database.query.Delete;
import database.query.Insert;
import database.query.Select;
import database.query.Update;

public class Interface {

	Connection connection;
	JSONObject columnTypes;
	private String dataType;
	public Interface()
	{
		columnTypes = new JSONObject();
		if(connection==null)
		{
			Connect connect = Connect.getInstance();
			connection = connect.getConnection(null);
		}
		this.dataType = "json";
	}
	public String getDataType()
	{
		return this.dataType;
	}
	public void setDataType(String dataType)
	{
		this.dataType = dataType;
	}
	public ResultSet getDataFromDB(Select sq)
	{
		ResultSet rs = null;
		try
		{
			if(connection!=null)
			{
				Statement st = connection.createStatement();
				rs = st.executeQuery(sq.getSelectQueryAsString());
			}
		}
		catch(SQLException sqe)
		{
			System.out.println("Something went wrong in executing the query \n"+sq.getSelectQueryAsString());
		}
		
		return rs;
	}
	public int insertDataIntoDB(Insert iq)
	{
		try
		{
			if(connection!=null)
			{
				Statement st = connection.createStatement();
				int rs = st.executeUpdate(iq.getInsertQueryAsString());
				return rs;
			}
		}
		catch(SQLException sqe)
		{
			System.out.println("Something went wrong in executing the query \n"+sqe.getMessage()+"\n"+iq.getInsertQueryAsString());
		}
		return 0;
		
	}
	
	public int updateDataInDB(Update uq)
	{
		try
		{
			if(connection!=null)
			{
				Statement st = connection.createStatement();
				int rs = st.executeUpdate(uq.getUpdateQueryAsString());
				return rs;
			}
		}
		catch(SQLException sqe)
		{
			System.out.println("Something went wrong in executing the query \n"+sqe.getMessage()+"\n"+uq.getUpdateQueryAsString());
		}
		return 0;
	}
	
	public int deleteDataFromDB(Delete dq)
	{
		try
		{
			if(connection!=null)
			{
				Statement st = connection.createStatement();
				int rs = st.executeUpdate(dq.getDeleteQueryAsString());
				return rs;
			}
		}
		catch(SQLException sqe)
		{
			System.out.println("Something went wrong in executing the query \n"+sqe.getMessage()+"\n"+dq.getDeleteQueryAsString());
		}
		return 0;
	}
	
	public JSONObject getDataAsJSONObject(ResultSet rs)
	{
		JSONObject data = new JSONObject();
		try
		{
			ResultSetMetaData resultMetaData = rs.getMetaData();
			int columnCount = resultMetaData.getColumnCount();
			String[] columnLabels = new String[columnCount];
			for(int i=1; i<=columnCount; i++)
			{
				columnLabels[i-1] = resultMetaData.getColumnLabel(i);
				String columnTableName = resultMetaData.getTableName(i);
				String columnTypeName = resultMetaData.getColumnTypeName(i);
				if(!this.columnTypes.has(columnTableName))
				{
					this.columnTypes.put(columnTableName, new JSONObject());
				}
				(this.columnTypes.getJSONObject(columnTableName)).put(columnLabels[i-1], columnTypeName);
			}
			while(rs.next())
			{
				JSONObject dataObj = new JSONObject();
				String tableName = null;
				for(int i=1; i<=columnCount; i++)
				{
					if(i==1)
					{
						tableName = resultMetaData.getTableName(i);
					}
					dataObj.put(columnLabels[i-1], rs.getObject(columnLabels[i-1]));
				}
				if(!data.has(tableName))
				{
					data.put(tableName, new JSONArray());
				}
				data.getJSONArray(tableName).put(dataObj);
			}
		}
		catch(SQLException sq)
		{
			System.out.println(sq.getStackTrace());
		}
		catch(JSONException je)
		{
			System.out.println(je.getStackTrace());
		}
		return data;
	}
	
	public int insertData(Insert iq)
	{
		int rs = insertDataIntoDB(iq);
		return rs;
	}
	public int updateData(Update uq)
	{
		int rs = updateDataInDB(uq);
		return rs;
	}
	public int deleteData(Delete dq)
	{
		int rs = deleteDataFromDB(dq);
		return rs;
	}
	public Object getData(Select sq)
	{
		ResultSet rs = getDataFromDB(sq);
		if(this.dataType.equals("json"))
		{
			return getDataAsJSONObject(rs);
		}
		return new JSONObject();
	}
}
