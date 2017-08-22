package database.query;

import java.util.HashMap;
import java.util.Map;

public class Update {
	
	private String updateTableName;
	private HashMap<String,Object> valueMap;
	private String criteria;
	
	public Update()
	{
		this.valueMap = new HashMap<String,Object>();
	}
	
	public String getUpdateTableName()
	{
		return this.updateTableName;
	}
	
	public void setUpdateTableName(String updateTableName)
	{
		this.updateTableName = updateTableName;
	}
	
	public void setCriteria(Query.Criteria crtString)
	{
		this.criteria = crtString.getCriteriaAsString();
	}
	
	public void setValueForColumn(Query.Column column, Object value)
	{
		String columnName = column.getColumnName();
		String tableName = column.getTableName();
		if(tableName.equals(this.updateTableName))
		{
			if(value instanceof String)
			{
				String temp = (String)value;
				temp="\'"+temp+"\'";
				value = temp;
			}
			this.valueMap.put(columnName, value);
		}
	}
	
	public String getUpdateQueryAsString()
	{
		StringBuilder queryString = new StringBuilder("UPDATE");
		queryString.append(" "+this.updateTableName+" SET");
		int cnt = 1;
		int len = valueMap.size();
		for(Map.Entry<String, Object> valueEntry : valueMap.entrySet())
		{
			String columnName = valueEntry.getKey();
			Object value = valueEntry.getValue();
			queryString.append(" "+columnName+" = "+value);
			if(cnt!=len)
			{
				queryString.append(",");
			}
			cnt++;
			
		}
		if(this.criteria!=null)
		{
			queryString.append(" WHERE "+this.criteria+";");
		}
		return queryString.toString();
	}

}
