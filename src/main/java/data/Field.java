package data;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import database.query.Query;
import database.query.Select;
import database.schema.FIELD;


public class Field {
	
	private JSONArray fields;
	private HashMap<String,String> fieldLabelVsColumnMap;
	private HashMap<String,String> columnVsFieldLabelMap;
	private HashMap<String,Object> columnVsDefaultValueMap;
	public static int TRUE = 1;
	public static int FALSE = 0;
	public static Select getSelectQueryForField()
	{
		Select sq = new Select();
		Query query = new Query();
		sq.addSelectColumn(query.new Column(FIELD.TABLE,FIELD.FIELD_ID));
		sq.addSelectColumn(query.new Column(FIELD.TABLE,FIELD.TABLE_NAME));
		sq.addSelectColumn(query.new Column(FIELD.TABLE,FIELD.COLUMN_NAME));
		sq.addSelectColumn(query.new Column(FIELD.TABLE,FIELD.FIELD_LABEL));
		sq.addSelectColumn(query.new Column(FIELD.TABLE,FIELD.DATA_TYPE));
		sq.addSelectColumn(query.new Column(FIELD.TABLE,FIELD.DEFAULT_VALUE));
		return sq;
	}
	
	public Field()
	{
		Interface dataInterface = new Interface();
		Select sq = getSelectQueryForField();
		JSONObject fieldInfo = (JSONObject)dataInterface.getData(sq);
		try
		{
			if(fieldInfo!=null && fieldInfo.has(FIELD.TABLE))
			{
				this.fields = fieldInfo.getJSONArray(FIELD.TABLE);
			}
		}
		catch(JSONException je)
		{
			System.out.println("Something went wrong in fetching fields data \n"+je.getMessage()+"\n");
			je.printStackTrace();
		}
	}
	public Field(String tableName)
	{
		Interface dataInterface = new Interface();
		Select sq = getSelectQueryForField();
		Query query = new Query();
		Query.Criteria fieldCr = query.new Criteria(query.new Column(FIELD.TABLE,FIELD.TABLE_NAME),tableName,Query.comparison_operators.EQUAL_TO);
		sq.setCriteria(fieldCr);
		JSONObject fieldInfo = (JSONObject)dataInterface.getData(sq);
		try
		{
			if(fieldInfo!=null && fieldInfo.has(FIELD.TABLE))
			{
				this.fields = fieldInfo.getJSONArray(FIELD.TABLE);
			}
		}
		catch(JSONException je)
		{
			System.out.println("Something went wrong in fetching fields data \n"+je.getMessage()+"\n");
			je.printStackTrace();
		}
	}
	public HashMap<String,Object> getColumnVsDefaultValueMap()
	{
		if(this.columnVsDefaultValueMap!=null)
		{
			return this.columnVsDefaultValueMap;
		}
		this.columnVsDefaultValueMap = new HashMap<String,Object>();
		try
		{
			for(int i=0; i<this.fields.length(); i++)
			{
				JSONObject field = fields.getJSONObject(i);
				String columnName = field.getString(FIELD.COLUMN_NAME);
				String dataType = field.getString(FIELD.DATA_TYPE);
				if(field.has(FIELD.DEFAULT_VALUE))
				{
					String defaultValue = field.getString(FIELD.DEFAULT_VALUE);
					if(defaultValue!=null)
					{
						this.columnVsDefaultValueMap.put(columnName, DataType.getValue(dataType, defaultValue));
					}
				}
			}	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return this.columnVsDefaultValueMap;
		
	}
	private void populateFieldLabelColumnMap()
	{
		if(this.fieldLabelVsColumnMap==null)
		{
			fieldLabelVsColumnMap = new HashMap<String,String>();
		}
		if(this.columnVsFieldLabelMap==null)
		{
			columnVsFieldLabelMap = new HashMap<String,String>();
		}
		try
		{
			for(int i=0; i<this.fields.length(); i++)
			{
				JSONObject field = fields.getJSONObject(i);
				String fieldLabel = field.getString(FIELD.FIELD_LABEL);
				String columnName = field.getString(FIELD.COLUMN_NAME);
				this.fieldLabelVsColumnMap.put(fieldLabel, columnName);
				this.columnVsFieldLabelMap.put(columnName, fieldLabel);
			}
		}
		catch(JSONException je)
		{
			System.out.println(je.getMessage()+"\n");
			je.printStackTrace();
		}
	}
	public HashMap<String,String> getColumnVsFieldLabelMap()
	{
		if(this.columnVsFieldLabelMap==null)
		{
			populateFieldLabelColumnMap();
		}
		return this.columnVsFieldLabelMap;
	}
	public HashMap<String,String> getFieldLabelVsColumnMap()
	{
		if(this.fieldLabelVsColumnMap==null)
		{
			populateFieldLabelColumnMap();
		}
		return this.fieldLabelVsColumnMap;
	}

}
