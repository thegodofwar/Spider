package com.pw.spider.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.pw.spider.Util.SpringIoCUtil;
import com.pw.spider.dao.ErrorDao;
import com.pw.spider.model.Entry;
import com.pw.spider.model.Error;

public class ErrorDaoImpl extends SqlMapClientDaoSupport implements ErrorDao {
    
	public static final Logger LOG=Logger.getLogger(ErrorDaoImpl.class.getName());
	
	@Override
	public long insert(Error error) {
		Object object=null;
    	SqlMapClient sqlMap=null;
    	try {
    		sqlMap=super.getSqlMapClient();
    		sqlMap.startTransaction();
    		object=sqlMap.insert("Error.insertError",error);
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
			return -1L;
		else
			return (Long)object;
	}
	
	@Override
	public boolean delete(long id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id",id);
		try {
			super.getSqlMapClient().delete("Error.delete", map);
		} catch (SQLException e) {
			LOG.error("", e);
			return false;
		}
		return true;
	}
	
	@Override
	public List<Error> getRetrys(long start, int limit) {
		List<Error> list=null;
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("start",start);
		map.put("limit",limit);
		try {
			list=super.getSqlMapClient().queryForList("Error.getRetrys", map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return list;
	}

	@Override
	public long getRetrysCount() {
		Object object=null;
	    try {
		    object=super.getSqlMapClient().queryForObject("Error.getRetrysCount");
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return (Long)object;
	}
	
	@Override
	public Error getError(long bookId,String url) {
		Object object=null;
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("bookId",bookId);
		map.put("url",url);
		try {
			object=super.getSqlMapClient().queryForObject("Error.getError",map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return (Error)object;
	}
	
	public static void main(String args[]) {
		ErrorDao errorDao=(ErrorDao)SpringIoCUtil.getBean("errorDao");
		Error error=new Error();
		error.setSiteId(1);
		error.setUrl("www.error.com");
		error.setType(1);
		error.setUpdateTime(new Date());
		LOG.info(errorDao.insert(error));
	}

}
