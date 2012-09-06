package com.pw.spider.model;

import java.util.Date;

public class Entry {
   
	private int siteId;
	
	private String url;
	
	private int totalPage;
	
	private int updatePageCrawlIndex;//记录爬虫异常退出时，更新页爬到第几页，恢复后从这页开始爬
	
	private Date lastTime;
	
	private int isStop;//1表示正常，0表示stop
    
	public int getUpdatePageCrawlIndex() {
		return updatePageCrawlIndex;
	}

	public void setUpdatePageCrawlIndex(int updatePageCrawlIndex) {
		this.updatePageCrawlIndex = updatePageCrawlIndex;
	}
	
	public int getIsStop() {
		return isStop;
	}

	public void setIsStop(int isStop) {
		this.isStop = isStop;
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

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}
	
	
}
