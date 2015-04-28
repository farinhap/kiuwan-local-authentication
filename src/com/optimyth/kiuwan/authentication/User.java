package com.optimyth.kiuwan.authentication;

import java.util.Date;

public class User {

	String username;
	Long date;
	
	public User() {
	}
	
	public User(String username) {
		this.username = username;
		setDate(new Date().getTime());
	}
	
	public User(String username, Long date) {
		this.username = username;
		this.date = date;
	}
	
	public void setDate(Long date) {
		this.date = date;
	}
	
	public Long getDate() {
		return date;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
}

