package com.yb.hitdb.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class HitQueryRepo {

	public String runQuery(String query,JdbcTemplate template) {
		String result = null;
		try {
			int rows = template.update(query);
			result = createResult(query,rows);
		}catch(Exception ex) {
			result = "Error "+ex.getMessage();
		}
		return result;
	}
	
	private String createResult(String query,int rows) {
		 String result = String.valueOf(rows);
		if(query.toLowerCase().replaceAll("\\s+", "").startsWith("update")) {
			result+=" Updated";
		}else if(query.toLowerCase().replaceAll("\\s+", "").startsWith("insert")) {
			result+=" Inserted";
		}else if(query.toLowerCase().replaceAll("\\s+", "").startsWith("delete")) {
			result+=" Deleted";
		}else {
			result+=" Affected";
		}
		return result;
	}
	
	
}
