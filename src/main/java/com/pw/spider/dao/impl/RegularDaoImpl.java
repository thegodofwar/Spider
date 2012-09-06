package com.pw.spider.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.pw.spider.Util.SpringIoCUtil;
import com.pw.spider.dao.RegularDao;
import com.pw.spider.dao.SiteDao;
import com.pw.spider.model.Regular;

public class RegularDaoImpl extends SqlMapClientDaoSupport implements RegularDao {
    
	public static final Logger LOG=Logger.getLogger(RegularDaoImpl.class.getName());
	
	private SiteDao siteDao=(SiteDao)SpringIoCUtil.getBean("siteDao");
	
	@Override
	public int insert(Regular regular) {
		Object object=null;
    	SqlMapClient sqlMap=null;
    	try {
    		sqlMap=super.getSqlMapClient();
    		sqlMap.startTransaction();
    		object=sqlMap.insert("Regular.insertRegular",regular);
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
	public Regular get(int siteId, int type) {
		Object object=null;
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId", siteId);
		map.put("type", type);
		try {
			object=super.getSqlMapClient().queryForObject("Regular.getBySiteIdAndType",map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return (Regular)object;
	}
	
	@Override
	public Regular get(String siteName, int type) {
		int siteId=siteDao.getIdByName(siteName);
		return get(siteId,type);
	}
	
	public static void main(String args[]) {
		RegularDao regularDao=(RegularDao)SpringIoCUtil.getBean("regularDao");
		
		/*Regular regular=new Regular();
		regular.setSiteId(1);
		regular.setCharset("UTF-8");
		regular.setRegex("this is a JSON");
		regular.setType(3);
		LOG.info(regularDao.insert(regular));*/
		
		//LOG.info(regularDao.get(1,3).getRegex());
		
		LOG.info(regularDao.get("121xs",3).getRegex());
	}

}
