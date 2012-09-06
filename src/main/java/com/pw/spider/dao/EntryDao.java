package com.pw.spider.dao;

import java.util.Date;

import com.pw.spider.model.Entry;

public interface EntryDao {
   
	public boolean insert(Entry entry);
	
	public Entry getBySiteId(int siteId);
	
	public boolean updateLastTime(int siteId,Date latestUpdateTime);
	
	public int getUpdatePageCrawlIndex(int siteId);
	
	public boolean updateUpdatePageCrawlIndex(int siteId,int value);
}
