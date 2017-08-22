package database.query;

public class Delete {

	private String deleteTableName;
	private Query.Criteria deleteCriteria;

	public String getDeleteTableName() {
		return deleteTableName;
	}

	public void setDeleteTableName(String deleteTableName) {
		this.deleteTableName = deleteTableName;
	}

	public void setDeleteCriteria(Query.Criteria deleteCriteria) {
		this.deleteCriteria = deleteCriteria;
	}
	
	public String getDeleteQueryAsString()
	{
		String queryString = "DELETE FROM "+this.deleteTableName;
		if(this.deleteCriteria!=null)
		{
			queryString+=" WHERE "+this.deleteCriteria.getCriteriaAsString();
		}
		queryString+=";";
		return queryString;
	}
}
