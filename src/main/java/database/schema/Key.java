package database.schema;

import java.util.HashMap;

public class Key {
	
	private static HashMap<String,String> primaryKeys;
	static
	{
		primaryKeys = new HashMap<String,String>();
		primaryKeys.put("account", "ACCOUNT_ID");
		primaryKeys.put("contact", "CONTACT_ID");	
		primaryKeys.put("field", "FIELD_ID");
		primaryKeys.put("item", "ITEM_ID");
		primaryKeys.put("saree", "SAREE_ID");
		primaryKeys.put("transaction", "TRANSACTION_ID");
		primaryKeys.put("transaction_type", "TRANSACTION_TYPE_NAME");
	}

	public static String getPrimaryKey(String tableName)
	{
		return primaryKeys.get(tableName);
	}
}
