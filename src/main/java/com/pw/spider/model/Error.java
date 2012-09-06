package com.pw.spider.model;

import java.util.Date;

public class Error {
    
	private long id;
	
	private long bookId;//if book update page error,then bookId=-1L
	
	private int siteId;
	
	private String url;
	
	private int type;
	
	private Date updateTime;
	
	public Error(){
		
	}
	
	public Error(int siteId,long bookId,String url,int type){
		this.siteId = siteId;
		this.bookId=bookId;
		this.url = url;
		this.type = type;
	}
    
	public long getBookId() {
		return bookId;
	}

	public void setBookId(long bookId) {
		this.bookId = bookId;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
}
