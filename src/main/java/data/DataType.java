package data;

public class DataType {
	
	public static String STRING = "STRING";
	public static String FLOAT = "FLOAT";
	public static String INTEGER = "INTEGER";
	
	public static Object getValue(String dataType, String input)
	{
		if(dataType.equals(STRING))
		{
			return input;
		}
		else if(dataType.equals(FLOAT))
		{
			return Float.parseFloat(input);
		}
		else if(dataType.equals(INTEGER))
		{
			return Integer.parseInt(input);
		}
		return input;
	}

}
