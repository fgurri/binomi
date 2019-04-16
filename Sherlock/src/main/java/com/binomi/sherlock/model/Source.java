package com.binomi.sherlock.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table (name="t_source")
public class Source {

	private String host;
	private String username;
	private String password;
	private String port;
	private String database;
	
	
	public Source() {
		super();
	}
	
	public Source(String host, String username, String password, String port, String database) {
		super();
		this.host = host;
		this.username = username;
		this.password = password;
		this.port = port;
		this.database = database;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
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
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	
	
}
