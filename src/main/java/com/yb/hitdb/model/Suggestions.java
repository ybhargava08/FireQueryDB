package com.yb.hitdb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.yb.hitdb.model.Suggestions;

@Repository
public interface SuggestionsRepo extends CrudRepository<Suggestions, String>{
	      
	      @Query("select new com.yb.hitdb.model.Suggestions(env,hostname,username,password,serviceId) "
	      		+ "from Suggestions where LOWER(env) like LOWER(CONCAT(:key,'%'))")
          public List<Suggestions> findAllByKey(@Param("key") String key);
}
