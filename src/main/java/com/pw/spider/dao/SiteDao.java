package com.pw.spider.dao;

import java.util.List;

import com.pw.spider.model.Site;

public interface SiteDao {
  
	public int insert(Site site);
	
	public int getIdByName(String name);
	
	public Site getSiteByName(String name);
	
	public Site getSiteById(int id);
	
	public List<Site> getAll();
	
}
