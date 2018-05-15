package com.yb.hitdb.model;

public class QueryBean implements Cloneable{

	private String username;
	private String password;
	private String hostname;
	private String serviceId;
	private String query;
	private String resultType;
	private String resultDesc;
	private String sessionId;
	private long resultTime;
	private String env;
		
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getResultDesc() {
		return resultDesc;
	}
	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}

	public long getResultTime() {
		return resultTime;
	}
	public void setResultTime(long resultTime) {
		this.resultTime = resultTime;
	}
	
	public String toString() {
		return this.resultType+" "+this.resultDesc;
	}
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}
}
