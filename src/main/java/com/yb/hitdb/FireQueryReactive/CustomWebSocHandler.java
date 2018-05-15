package com.yb.hitdb.FireQueryReactive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.google.gson.Gson;
import com.yb.hitdb.model.QueryBean;
import com.yb.hitdb.service.HitQueryService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

public class CustomWebSocHandler implements WebSocketHandler{
	
	Gson gson = new Gson();
	
	@Autowired
	HitQueryService service;
	
	UnicastProcessor<QueryBean> processor;
	Flux<QueryBean> messages;
	
	public CustomWebSocHandler(UnicastProcessor<QueryBean> processor,Flux<QueryBean> messages) {
		this.processor = processor;
		this.messages = messages;
	}
	
	@Override
	public Mono<Void> handle(WebSocketSession session) {
		CustomSubscriber subs = new CustomSubscriber(service,processor,session.getId());
		session.receive().map(WebSocketMessage::getPayloadAsText).map(this::convertStringToJson)
		.subscribe(subs::onNext,subs::onError,subs::onComplete);
		return session.send(messages.filter(bean->bean.getSessionId().equalsIgnoreCase(session.getId())).
				map(this::convertBeanToJson).map(session::textMessage));
	}
	
	private QueryBean convertStringToJson(String json) {
		return gson.fromJson(json, QueryBean.class);
	}

	private String convertBeanToJson(QueryBean bean) {
		return gson.toJson(bean);
	}
}

class CustomSubscriber{
	
	HitQueryService serv;
	UnicastProcessor<QueryBean> customProcessor;
	String sessionId;
	
	public CustomSubscriber(HitQueryService service,UnicastProcessor<QueryBean> processor,String id) {
		this.serv = service;
		this.customProcessor = processor;
		this.sessionId = id;
	}
	
	public void onNext(QueryBean bean) {
		bean.setSessionId(sessionId);
		serv.runQuery(bean,customProcessor);
	}
	
	public void onError(Throwable th) {
		System.out.println("Error: "+th.getMessage());
	}
	
	public void onComplete() {
		//System.out.println("Completed");
	}

}

