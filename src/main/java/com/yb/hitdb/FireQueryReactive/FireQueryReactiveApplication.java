package com.yb.hitdb.FireQueryReactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import com.yb.hitdb.model.QueryBean;
import com.yb.hitdb.model.Suggestions;

import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@SpringBootApplication
@EnableJpaRepositories(basePackages= {"com.yb.hitdb.repo"})
@EntityScan(basePackages= {"com.yb.hitdb.model"})
@ComponentScan(basePackages= {"com.yb.hitdb"})
public class FireQueryReactiveApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(FireQueryReactiveApplication.class, args);
	}
	
	@Bean
	public UnicastProcessor<QueryBean> broadcastProcessor(){
		return UnicastProcessor.create();
	}
	
	@Bean
	public Flux<QueryBean> getAllMessages(UnicastProcessor<QueryBean> broadcastProcessor){
		return broadcastProcessor.replay(0).autoConnect();
	}
	
	@Bean
	RouterFunction<ServerResponse> routes() {
		return RouterFunctions.route(RequestPredicates.GET("/fireQuery"), 
             request-> ServerResponse.ok().body(BodyInserters.fromResource(new ClassPathResource("static/query.html"))));
	}
	
	@Bean
	public CustomWebSocHandler webSocketHandler(UnicastProcessor<QueryBean> broadcastProcessor,Flux<QueryBean> getAllMessages) {
		return new CustomWebSocHandler(broadcastProcessor,getAllMessages);
	}
	
	@Bean
	public HandlerMapping webSocketMapping(CustomWebSocHandler webSocketHandler) {
		  Map<String,Object> map= new HashMap<String,Object>(1);
		 map.put("/queryconn", webSocketHandler);
		 SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
		 handlerMapping.setUrlMap(map);
		 handlerMapping.setOrder(1);
		 return handlerMapping;
	}
	
	@Bean
	public WebSocketHandlerAdapter handlerAdapter() {
		return new WebSocketHandlerAdapter();
	}
	
	@Bean
	public CopyOnWriteArrayList<Suggestions> cache() {
		return new CopyOnWriteArrayList<Suggestions>();
	}
	
	@Bean
	public SecurityWebFilterChain webFilterChain(ServerHttpSecurity http) {
		 return http.authorizeExchange().pathMatchers("/favicon.ico","/fireQuery").permitAll().
				 pathMatchers("/*.js","/*.css","/*.html").denyAll().anyExchange().permitAll().and().build();
	}
}
