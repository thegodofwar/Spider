package com.pw.spider.model;

import java.util.Date;

/**
 * @author liufukun
 * 
 */
public class Chapter {
    private String siteName;
	
	private long id;
	
	private long kv;
	
	private long chapterId;
	
	private String name;
	
	private String url;
	
	private int siteId;
	
	private long bookId;
	
	private int type;
	
	private int chapterMatchStart;
	
	private int tomeOrder;
	
	private Date updateTime;
	
	private String kvs;
	
	public String getKvs() {
		return kvs;
	}

	public void setKvs(String kvs) {
		this.kvs = kvs;
	}

	public long getKv() {
		return kv;
	}

	public void setKv(long kv) {
		this.kv = kv;
	}
	
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
    
	public int getChapterMatchStart() {
		return chapterMatchStart;
	}

	public void setChapterMatchStart(int chapterMatchStart) {
		this.chapterMatchStart = chapterMatchStart;
	}

	public int getTomeOrder() {
		return tomeOrder;
	}

	public void setTomeOrder(int tomeOrder) {
		this.tomeOrder = tomeOrder;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getChapterId() {
		return chapterId;
	}

	public void setChapterId(long chapterId) {
		this.chapterId = chapterId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public long getBookId() {
		return bookId;
	}

	public void setBookId(long bookId) {
		this.bookId = bookId;
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
