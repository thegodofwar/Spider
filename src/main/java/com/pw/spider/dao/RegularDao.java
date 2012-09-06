package com.pw.spider.dao;

import com.pw.spider.model.Regular;

public interface RegularDao {
   
	public int insert(Regular regular);
	
	public Regular get(int siteId,int type);
	
	public Regular get(String siteName,int type);
	
}
