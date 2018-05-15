package com.yb.hitdb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.yb.hitdb.model.Suggestions;
import com.yb.hitdb.service.HitQueryService;

@RestController
public class QueryController {

	@Autowired
	HitQueryService service;
	
	 Gson gson = new Gson();
	
	/*@GetMapping(value="/getSuggestions/{key}",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> getSuggestions(@PathVariable("key") String key) {
		return Flux.fromIterable(service.getSuggestions(key)).map(bean->
		gson.toJson(new Suggestions(bean.getEnv(),bean.getHostname(),bean.getUsername(),bean.getPassword(),bean.getServiceId())))
				.delayElements(Duration.ofSeconds(2));
	}*/

	@GetMapping(value="/getSuggestions/{key}")
	public List<Suggestions> getSuggestions(@PathVariable("key") String key) {
		return service.getSuggestions(key);
	}
	
	@PostMapping("/updateCacheorDB")
	public String updateCacheorDB(@RequestBody Suggestions sugg) {
		
		service.saveDataForSuggestions(sugg);
		return "updated";
	}
	
}
