package com.yb.hitdb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.context.annotation.Primary;

@Entity
@Table(name="Suggestions")
public class Suggestions {

	private String env;
	private String hostname;
	private String username;
	private String password;
	private String serviceId;
	
	public Suggestions(String env,String hostname,String username,String password,String serviceId) {
		this.env = env;
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		this.serviceId = serviceId;
	}
	
	public Suggestions() {}

	@Primary
	@Column
	@Id
	public String getEnv() {
		return env;
	}
	public void setEnv(String env) {
		this.env = env;
	}
	
	@Column
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	@Column
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Column
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public int hashCode() {
		return this.env.hashCode();
	}
	
	public boolean equals(Object o) {
		return ((Suggestions)o).getEnv().equalsIgnoreCase(this.env);
	}
	
	public String toString() {
		return this.env;
	}
	
	@Column
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
}
