package database.query;

import java.util.ArrayList;


public class Select {
	
	private ArrayList<Query.Column> selectColumns;
	private ArrayList<Query.Join> joins;
	private Query.Criteria criteria;
	private ArrayList<Query.Column> groupByColumns;
	private ArrayList<Query.Column> orderByColumns;
	private String sortOrder;
	private String baseTableName;
	private Integer limit;
	
	public static enum order
	{
		ASC,DESC;
	}
	
	public static enum aggregate_functions 
	{
		MIN,MAX,SUM,AVG,COUNT;
	}
	public Select()
	{
		selectColumns = new ArrayList<Query.Column>();
		joins = new ArrayList<Query.Join>();
		groupByColumns = new ArrayList<Query.Column>();
		orderByColumns = new ArrayList<Query.Column>();
		sortOrder = ""+Select.order.ASC;
	}
	public String getSelectQueryAsString()
	{
		String selectStatement = "SELECT";
		int arrLen = selectColumns.size();
		int cnt = 1;
		for(Query.Column selectColumn : selectColumns)
		{
			if(cnt==1)
			{
				this.baseTableName = selectColumn.getTableName();
			}
			selectStatement+=" "+(selectColumn.getColumnString());
			if(cnt!=arrLen)
			{
				selectStatement+=",";
			}
			cnt++;
		}
		selectStatement+=" FROM "+this.baseTableName;
		for(Query.Join join : this.joins)
		{
			selectStatement+=" "+join.getJoinAsString(this.baseTableName);
		}
		if(this.criteria!=null)
		{
			selectStatement+=" WHERE "+this.criteria.getCriteriaAsString();
		}
		if(this.groupByColumns!=null && !this.groupByColumns.isEmpty() && this.groupByColumns.size()>0)
		{
			String groupByString = "GROUP BY";
			arrLen = this.groupByColumns.size();
			cnt = 1;
			for(Query.Column groupByColumn : this.groupByColumns)
			{
				groupByString+=" "+groupByColumn.getColumnString();
				if(cnt!=arrLen)
				{
					groupByString+=",";
				}
				cnt++;
			}
			selectStatement+=" "+groupByString;
		}
		if(this.orderByColumns!=null && !this.orderByColumns.isEmpty() && this.orderByColumns.size()>0)
		{
			String orderByString = "ORDER BY";
			arrLen = orderByColumns.size();
			cnt = 1;
			for(Query.Column orderByColumn : orderByColumns)
			{
				orderByString+=" "+orderByColumn.getColumnString();
				if(cnt!=arrLen)
				{
					orderByString+=",";
				}
				cnt++;
			}
			orderByString+=" "+this.sortOrder;
			selectStatement+=" "+orderByString;
		}
		if(limit!=null && limit!=0)
		{
			selectStatement+=" limit "+limit.toString();
		}
		selectStatement+=";";
		return selectStatement;
	}
	public void addSelectColumn(Query.Column column)
	{
		selectColumns.add(column);
	}
	public void addSelectColumns(ArrayList<Query.Column> columns)
	{
		for(Query.Column column : columns)
		{
			if(!selectColumns.contains(column))
			{
				selectColumns.add(column);
			}
		}
	}
	public void setCriteria(Query.Criteria criteria)
	{
		this.criteria = criteria;
	}
	public void addJoin(Query.Join join)
	{
		if(this.joins.size()==0 && this.baseTableName==null && !(join.getJoinTable1()).equals(join.getJoinTable2()))
		{
			joins.add(join);
		}
		else if(this.joins.size()==1 && baseTableName==null)
		{
			Query.Join oldJoin = joins.get(0);
			String oldTable1 = oldJoin.getJoinTable1();
			String oldTable2 = oldJoin.getJoinTable2();
			String newTable1 = join.getJoinTable1();
			String newTable2 = join.getJoinTable2();
			if(newTable1.equals(oldTable1) || newTable1.equals(oldTable2))
			{
				this.baseTableName = newTable1;
			}
			else if(newTable2.equals(oldTable1) || newTable2.equals(oldTable2))
			{
				this.baseTableName = newTable2;
			}
			if(this.baseTableName!=null && !newTable1.equals(newTable2))
			{
				this.joins.add(join);
			}
		}
		else if(this.joins.size()>1 && this.baseTableName!=null)
		{
			String newTable1 = join.getJoinTable1();
			String newTable2 = join.getJoinTable2();
			if((newTable1.equals(this.baseTableName) || newTable2.equals(this.baseTableName)) && !newTable1.equals(newTable2))
			{
				this.joins.add(join);
			}
		}
	}
	public void addGroupByColumn(Query.Column groupByColumn)
	{
		this.groupByColumns.add(groupByColumn);
	}
	public void addGroupByColumns(ArrayList<Query.Column> groupByColumns)
	{
		for(Query.Column groupByColumn : groupByColumns)
		{
			this.groupByColumns.add(groupByColumn);
		}
	}
	public void addOrderByColumn(Query.Column orderByColumn)
	{
		this.orderByColumns.add(orderByColumn);
	}
	public void addOrderByColumns(ArrayList<Query.Column> orderByColumns)
	{
		for(Query.Column orderByColumn : orderByColumns)
		{
			this.orderByColumns.add(orderByColumn);
		}
	}
	public void setSortingOrder(Select.order sOrder)
	{
		this.sortOrder = ""+sOrder;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}

}
