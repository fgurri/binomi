package com.creugrogasoft.biphone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope("session")
public class UserInfo {

	private String name;
	private boolean logged = false;
	private String fullName;
	
	
	public UserInfo() {
		super();
	}
	public UserInfo(String fullname, String name, boolean logged) {
		super();
		this.fullName = fullname;
		this.name = name;
		this.logged = logged;
	}
	
	@Bean
	@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public UserInfo userInfoBean() {
		return new UserInfo();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isLogged() {
		return logged;
	}
	public void setLogged(boolean logged) {
		this.logged = logged;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
}
