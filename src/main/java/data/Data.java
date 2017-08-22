package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.query.Delete;
import database.query.Insert;
import database.query.Query;
import database.query.Update;
import database.schema.Key;

public class Data {
	
	Field fieldObj;
	private String tableName;
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	private Field getFieldObject()
	{
		if(fieldObj==null)
		{
			fieldObj = new Field(this.tableName);
		}
		return fieldObj;
	}
	
	public int insertRecord(HashMap<String,Object> record)
	{
		ArrayList<HashMap<String,Object>> records = new ArrayList<HashMap<String,Object>>();
		records.add(record);
		return (insertRecords(records));
	}
	public int insertRecords(ArrayList<HashMap<String,Object>> records)
	{
		Interface data = new Interface();
		Field fieldObj = this.getFieldObject();
		HashMap<String,String> fieldLabelVsColumnMap = fieldObj.getFieldLabelVsColumnMap();
		Query query = new Query();
		Insert iq  = new Insert();
		iq.setInsertTableName(this.tableName);
		for(HashMap<String,Object> record : records)
		{
			HashMap<Query.Column,Object> insertRow = new HashMap<Query.Column,Object>();
			for(Map.Entry<String, Object> recordEntry : record.entrySet())
			{
				String fieldLabel = recordEntry.getKey();
				Object value = recordEntry.getValue();
				if(fieldLabelVsColumnMap.containsKey(fieldLabel))
				{
					String columnName = fieldLabelVsColumnMap.get(fieldLabel);
					Query.Column column = query.new Column(this.tableName,columnName);
					insertRow.put(column, value);
				}
			}
			iq.addInsertEntry(insertRow);
		}
		return data.insertData(iq);
	}
	
	public int updateRecord(int recordId, HashMap<String,Object> record)
	{
		Interface data = new Interface();
		Field fieldObj = this.getFieldObject();
		HashMap<String,String> fieldLabelVsColumnMap = fieldObj.getFieldLabelVsColumnMap();
		Query query = new Query();
		Update uq = new Update();
		uq.setUpdateTableName(this.tableName);
		for(Map.Entry<String, Object> recordEntry : record.entrySet())
		{
			String fieldLabel = recordEntry.getKey();
			Object value = recordEntry.getValue();
			if(fieldLabelVsColumnMap.containsKey(fieldLabel))
			{
				String columnName = fieldLabelVsColumnMap.get(fieldLabel);
				Query.Column column = query.new Column(this.tableName,columnName);
				uq.setValueForColumn(column, value);
			}
		}
		Query.Criteria upCr = query.new Criteria(query.new Column(this.tableName,Key.getPrimaryKey(this.tableName)),recordId,Query.comparison_operators.EQUAL_TO);
		uq.setCriteria(upCr);	
		return data.updateData(uq);
	}
	
	public int deleteRecord(int recordId)
	{
		ArrayList<Integer> recordIds = new ArrayList<Integer>();
		recordIds.add(recordId);
		return deleteRecords(recordIds);
	}
	public int deleteRecords(ArrayList<Integer> recordIds)
	{
		Interface data = new Interface();
		Query query = new Query();
		Delete dq = new Delete();
		dq.setDeleteTableName(this.tableName);
		Query.Criteria dlCr = query.new Criteria(query.new Column(this.tableName,Key.getPrimaryKey(this.tableName)),recordIds,Query.comparison_operators.IN);
		dq.setDeleteCriteria(dlCr);
		return data.deleteData(dq);
	}
	
	

}
