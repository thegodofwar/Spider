package com.pw.spider.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.pw.spider.Util.SpringIoCUtil;
import com.pw.spider.dao.SiteDao;
import com.pw.spider.model.Site;

public class SiteDaoImpl extends SqlMapClientDaoSupport implements SiteDao {
    
	public static final Logger LOG=Logger.getLogger(SiteDaoImpl.class.getName());
	
	@Override
	public int insert(Site site) {
		Object object=null;
    	SqlMapClient sqlMap=null;
    	try {
    		sqlMap=super.getSqlMapClient();
    		sqlMap.startTransaction();
    		object=sqlMap.insert("Site.insertSite",site);
    		sqlMap.commitTransaction();
		} catch (SQLException e) {
			LOG.error("",e);
		} finally {
			try {
				sqlMap.endTransaction();
			} catch (SQLException e) {
				LOG.error("",e);
			}
		}
		if(object == null)
			return -1;
		else
			return (Integer)object;
	}
	
	@Override
	public int getIdByName(String name) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("name",name);
		Object object=null;
	    try {
		    object=super.getSqlMapClient().queryForObject("Site.getIdByName",map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return (Integer)object;
	}
	
	@Override
	public Site getSiteByName(String name) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("name",name);
		Object object=null;
	    try {
		    object=super.getSqlMapClient().queryForObject("Site.getSiteByName",map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return (Site)object;
	}
	
	@Override
	public Site getSiteById(int id) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("id",id);
		Object object=null;
	    try {
		    object=super.getSqlMapClient().queryForObject("Site.getSiteById",map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return (Site)object;
	}
	
	@Override
	public List<Site> getAll() {
		List<Site> list=null;
        try {
        	list=super.getSqlMapClient().queryForList("Site.getAll");
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return list;
	}
	
	public static void main(String args[]) {
		SiteDao siteDao=(SiteDao)SpringIoCUtil.getBean("siteDao");
		/*Site site=new Site();
		site.setName("121xs");
		site.setUrl("www.121Url.com");
		site.setCharset("UTF-8");
		site.setType(0);
		site.setWeight(50);
		site.setUseProxy(0);
		LOG.info(siteDao.insert(site));*/
		
		//LOG.info(siteDao.getIdByName("121xs"));
		
		//LOG.info(siteDao.getSiteByName("121xs").getUrl());
		
		LOG.info(siteDao.getSiteById(1).getName());
		
	}

}
