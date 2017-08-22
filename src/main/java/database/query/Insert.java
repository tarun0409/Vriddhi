package database.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Insert {
	
	private String insertTableName;
	private ArrayList<HashMap<String,Object>> rows;
	private ArrayList<String> colNames;
	public Insert()
	{
		colNames = new ArrayList<String>();
		rows = new ArrayList<HashMap<String,Object>>();
	}
	public String getInsertTableName()
	{
		return this.insertTableName;
	}
	
	public void setInsertTableName(String tableName)
	{
		this.insertTableName = tableName;
	}
	
	public String getInsertQueryAsString()
	{
		StringBuilder queryString = new StringBuilder("INSERT INTO");
		queryString.append(" "+this.insertTableName+" (");
		int len = colNames.size();
		int cnt = 1;
		for(String colName : colNames)
		{
			queryString.append(colName);
			if(cnt!=len)
			{
				queryString.append(",");
			}
			cnt++;
		}
		queryString.append(") VALUES ");
		len = rows.size();
		cnt=1;
		for(HashMap<String,Object> row : rows)
		{
			queryString.append("(");
			int sublen = colNames.size();
			int subcnt = 1;
			for(String colName : colNames)
			{
				if(row.containsKey(colName))
				{
					queryString.append(row.get(colName));
				}
				if(subcnt!=sublen)
				{
					queryString.append(",");
				}
				subcnt++;
			}
			queryString.append(")");
			if(len!=cnt)
			{
				queryString.append(",");
			}
			cnt++;
		}
		queryString.append(";");
		return queryString.toString();
		
	}
	
	public void addInsertEntry(HashMap<Query.Column, Object> row)
	{
		HashMap<String, Object> insertRow = new HashMap<String, Object>();
		for(Map.Entry<Query.Column, Object> insertEntry : row.entrySet())
		{
			Query.Column column = insertEntry.getKey();
			Object value = insertEntry.getValue();
			if(value instanceof String)
			{
				String temp = (String)value;
				temp="\'"+temp+"\'";
				value = temp;
			}
			String tableName = column.getTableName();
			if(this.insertTableName == null)
			{
				this.insertTableName = tableName;
			}
			else if(tableName.equals(this.insertTableName))
			{
				String columnName = column.getColumnName();
				if(!colNames.contains(columnName))
				{
					colNames.add(columnName);
				}
				insertRow.put(columnName, value);
			}
		}
		this.rows.add(insertRow);
	}

}
