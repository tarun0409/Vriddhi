package vriddhi.Vriddhi.values;


import java.text.DecimalFormat;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Interface;
import data.Response;
import database.query.Query;
import database.query.Select;
import database.schema.ITEM;

@Path("functions")
public class Function {
	
	private static DecimalFormat df2 = new DecimalFormat(".##");
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("profit")
	public String getCurrentProfit()
	{
		JSONObject profit = new JSONObject();
		Query query = new Query();
		Select sq = new Select();
		sq.addSelectColumn(query.new Column(ITEM.TABLE,"*"));
		Interface dbInt = new Interface();
		JSONObject rs = (JSONObject)dbInt.getData(sq);
		float totalBought = 0.00F;
		float totalProfit = 0.00F;
		if(rs!=null)
		{
			try
			{
				JSONArray items = rs.getJSONArray(ITEM.TABLE);
				for(int i=0; i<items.length(); i++)
				{
					JSONObject itemRow = items.getJSONObject(i);
					Integer buyQ = (Integer)itemRow.get(ITEM.QUANTITY_BOUGHT);
					if(buyQ!=0)
					{
						Integer sellQ = (Integer)itemRow.get(ITEM.QUANTITY_SOLD);
						Float buyVal = (Float)itemRow.get(ITEM.BOUGHT_GOODS_VALUE);
						Float sellVal = (Float)itemRow.get(ITEM.SOLD_GOODS_VALUE);
						Float currentBuyVal = (buyVal/buyQ)*sellQ;
						totalProfit+=(sellVal-currentBuyVal);
						totalBought+=currentBuyVal;
					}
					
				}
				float profitPerCent = (totalProfit/totalBought)*100;
				profit.put("profit", Double.parseDouble(df2.format(profitPerCent)));
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
				Response response = new Response();
				response.setStatus(Response.FAILURE);
				response.setMessage("Something went wrong in calculating profit");
				return response.getResponse();
			}
			
		}
		else
		{
			Response response = new Response();
			response.setStatus(Response.FAILURE);
			response.setMessage("Something went wrong in calculating profit");
			return response.getResponse();
		}
		return profit.toString();
	}

}
