package com.yb.hitdb.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.yb.hitdb.model.QueryBean;
import com.yb.hitdb.model.Suggestions;
import com.yb.hitdb.repo.HitQueryRepo;
import com.yb.hitdb.repo.SuggestionsRepo;

import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;
import reactor.core.publisher.UnicastProcessor;

@Service
public class HitQueryService {

  @Autowired	
  HitQueryRepo repo;	

  @Autowired
  SuggestionsRepo suggRepo;
  
  @Autowired
  CopyOnWriteArrayList<Suggestions> cache;
  
  private JdbcTemplate template ;
  
    public void runQuery(QueryBean bean,UnicastProcessor<QueryBean> customProcessor) {
	  ThreadPoolExecutor exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
	   try {
		   String[] arr = getAllQueryStrings(bean);
		   JdbcTemplate template = createJDBCTemplate(bean);
		   for(int i=0;i<arr.length;i++) {
			    exec.submit(new QueryWorker((QueryBean) bean.clone(),repo,template,arr[i],customProcessor));
		   } 
	
		   saveDataForSuggestions(new Suggestions(bean.getEnv(),bean.getHostname(),bean.getUsername(),bean.getPassword(),bean.getServiceId()));
	   }catch(SQLException | CloneNotSupportedException ex) {
		   bean.setResultType("Error");
		   bean.setResultDesc("Error "+ex.getMessage());
		   bean.setResultTime(System.currentTimeMillis());
		   customProcessor.onNext(bean);
	   } finally {
		   exec.shutdown();
	   }
	   
  }
  
  @Async
  public void saveDataForSuggestions(Suggestions sugg) {
	  sugg = suggRepo.save(sugg);
	  updateInCacheListIfNeeded(sugg);
  }
  
  public void updateInCacheListIfNeeded(Suggestions sugg) {
	  
	  if(cache.contains(sugg)) {
		  cache.forEach(suggBean-> {
			  if(suggBean.equals(sugg)){
				 if(!suggBean.getHostname().equalsIgnoreCase(sugg.getHostname())) {
					 suggBean.setHostname(sugg.getHostname());
				 }
				 if(!suggBean.getUsername().equalsIgnoreCase(sugg.getUsername())) {
					 suggBean.setUsername(sugg.getUsername());
				 }
				 if(!suggBean.getPassword().equalsIgnoreCase(sugg.getPassword())) {
					 suggBean.setPassword(sugg.getPassword());
				 }
				 if(!suggBean.getServiceId().equalsIgnoreCase(sugg.getServiceId())) {
					 suggBean.setServiceId(sugg.getServiceId());
				 }
			  }    
		  });
	  }
	  
	  if(cache.contains(sugg)) {
		  for(Suggestions suggBean: cache) {
			  if(suggBean.equals(sugg) && !suggBean.getHostname().equalsIgnoreCase(sugg.getHostname()) || 
					  !suggBean.getUsername().equalsIgnoreCase(sugg.getUsername()) || !suggBean.getPassword().equalsIgnoreCase(sugg.getPassword()) || 
					  !suggBean.getServiceId().equalsIgnoreCase(sugg.getServiceId())) {
				  suggBean = sugg;
				  break;
			  }
		  }
	  }else {
		  cache.add(sugg);
	  }
  }
  
  public List<Suggestions> getSuggestions(String key) {
	  List<Suggestions> list =cache.stream().filter(bean->bean.getEnv().toLowerCase().startsWith(key.toLowerCase())).collect(Collectors.toList());
	  if(null==list || list.isEmpty()) {
		 // System.out.println("not found in cache getting from db");
		  list = suggRepo.findAllByKey(key);
		  cache.addAll(list);
	  }
	  return list;
  }
  
  /*public Flux<String> getSuggestionsFlux(String key) {
	  return Flux.fromIterable(getSuggestions(key)).map(bean->gson.toJson(
			  new Suggestions(bean.getEnv(),bean.getHostname(),bean.getUsername(),bean.getPassword(),bean.getServiceId())));
  }*/
  
  private JdbcTemplate createJDBCTemplate(QueryBean bean) throws SQLException {
	  String url = "jdbc:oracle:thin:@"+bean.getHostname()+":1521:"+bean.getServiceId(); 
	  OracleDataSource dataSource = new OracleDataSource();
	  dataSource.setUser(bean.getUsername());
	  dataSource.setPassword(bean.getPassword());
	  dataSource.setURL(url);
	  dataSource.setImplicitCachingEnabled(true);
	  Properties prop =new Properties();
	  prop.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT, "5000");
	  dataSource.setConnectionProperties(prop);
	  template = new JdbcTemplate(dataSource);
	  template.setQueryTimeout(5);
	  return template;
  }
  
  private String[] getAllQueryStrings(QueryBean bean) {
	return bean.getQuery().split(";");
  }
}

class QueryWorker implements Runnable{
	
	private QueryBean bean ;
	private HitQueryRepo repo;
	private JdbcTemplate template;
	private String queryString;
	private UnicastProcessor<QueryBean> processor;
	
	public QueryWorker(QueryBean bean,HitQueryRepo repository,JdbcTemplate temp,String query,UnicastProcessor<QueryBean> customProcessor) {
		this.bean = bean;
		this.repo = repository;
		this.template = temp;
		this.queryString = query;
		this.processor = customProcessor;
	}

	@Override
	public void run() {
		bean.setResultType(queryString);
		 bean.setResultDesc( repo.runQuery(queryString, template));
		 bean.setResultTime(System.currentTimeMillis());
		 processor.onNext(bean);
	}	
}
