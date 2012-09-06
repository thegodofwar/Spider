package com.pw.spider.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.pw.spider.Util.SpringIoCUtil;
import com.pw.spider.dao.EntryDao;
import com.pw.spider.model.Entry;

public class EntryDaoImpl extends SqlMapClientDaoSupport implements EntryDao {
    
	public static final Logger LOG=Logger.getLogger(EntryDaoImpl.class.getName());
	
	@Override
	public boolean insert(Entry entry) {
    	SqlMapClient sqlMap=null;
    	try {
    		sqlMap=super.getSqlMapClient();
    		sqlMap.startTransaction();
    		sqlMap.insert("Entry.insertEntry",entry);
    		sqlMap.commitTransaction();
		} catch (SQLException e) {
			LOG.error("",e);
			return false;
		} finally {
			try {
				sqlMap.endTransaction();
			} catch (SQLException e) {
				LOG.error("",e);
				return false;
			}
		}
		return true;
	}
    
	@Override
	public Entry getBySiteId(int siteId) {
		Object object=null;
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId", siteId);
		try {
			object=super.getSqlMapClient().queryForObject("Entry.getBySiteId",map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return (Entry)object;
	}
	
	@Override
	public boolean updateLastTime(int siteId, Date latestUpdateTime) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("latestUpdateTime",latestUpdateTime);
		map.put("siteId",siteId);
		try {
			getSqlMapClient().update("Entry.updateLastTime", map);
		} catch (SQLException e) {
			LOG.error("",e);
			return false;
		}
		return true;
	}
	
	@Override
	public int getUpdatePageCrawlIndex(int siteId) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId",siteId);
		Object object=null;
	    try {
		    object=super.getSqlMapClient().queryForObject("Entry.getUpdatePageCrawlIndex",map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return (Integer)object;
	}

	@Override
	public boolean updateUpdatePageCrawlIndex(int siteId, int value) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId",siteId);
		map.put("value",value);
		try {
			getSqlMapClient().update("Entry.updateUpdatePageCrawlIndex", map);
		} catch (SQLException e) {
			LOG.error("",e);
			return false;
		}
		return true;
	}
	
	public static void main(String args[]) {
		EntryDao entryDao=(EntryDao)SpringIoCUtil.getBean("entryDao");
		/*Entry entry=new Entry();
		entry.setSiteId(1);
		entry.setUrl("www.121Url.[[NUM]].html");
		entry.setTotalPage(100);
		entry.setLastTime(new Date());
		LOG.info(entryDao.insert(entry));*/
	
		//LOG.info(entryDao.getBySiteId(1).getUrl());
		LOG.info(entryDao.updateUpdatePageCrawlIndex(1, 10));
	}

}
