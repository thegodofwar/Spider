package com.pw.spider.dao.impl;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.pw.spider.Util.SpringIoCUtil;
import com.pw.spider.dao.ChapterDao;
import com.pw.spider.model.Chapter;

public class ChapterDaoImpl extends SqlMapClientDaoSupport implements ChapterDao {
    
	public static final Logger LOG=Logger.getLogger(ChapterDaoImpl.class.getName());
	
	@Override
	public long insert(Chapter chapter) {
		Object object=null;
    	SqlMapClient sqlMap=null;
    	try {
    		sqlMap=super.getSqlMapClient();
    		sqlMap.startTransaction();
    		object=sqlMap.insert("Chapter.insertChapter",chapter);
    		sqlMap.commitTransaction();
		} catch (SQLException e) {
			LOG.error("bookID="+chapter.getBookId()+" TomeOrder="+chapter.getTomeOrder(),e);
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
	public boolean insertBatch(final List<Chapter> chapters, final int batchNum) {
		super.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
			   public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {	
				    executor.startBatch();
				    int batch = 0;
				    for (int i=0;i<chapters.size();i++) {
				     Chapter chapter=chapters.get(i);
				     executor.insert("Chapter.insertChapter",chapter);
				     batch++;
				     if (batch == batchNum) {
				       executor.executeBatch();
				       batch = 0;
				     }
				    }
				    executor.executeBatch();
				    return null;
			   }
			}
			);
			return true;
	}
	
	@Override
	public Chapter getChapter(int siteId,long bookId,String name,String url) {
		List<Chapter> list=null;
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId",siteId);
		map.put("bookId",bookId);
		map.put("name",name);
		map.put("url",url);
		try {
			list=super.getSqlMapClient().queryForList("Chapter.getChapter",map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		if(list==null||list.size()==0) {
			return null;
		}
		return list.get(0);
	}
	
	@Override
	public boolean updateKV(int siteId, long id, long kv, int type) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId",siteId);
		map.put("id",id);
		map.put("kv",kv);
		map.put("type",type);
		try {
			getSqlMapClient().update("Chapter.updateKV", map);
		} catch (SQLException e) {
			LOG.error("",e);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean updateKVS(int siteId, long id, String kvs, int type) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId",siteId);
		map.put("id",id);
		map.put("kvs",kvs);
		map.put("type",type);
		try {
			getSqlMapClient().update("Chapter.updateKVS", map);
		} catch (SQLException e) {
			LOG.error("",e);
			return false;
		}
		return true;
	}
	
	@Override
	public List<Chapter> getKVChapters(int siteId, long from, int limit) {
		List<Chapter> list=null;
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId", siteId);
		map.put("from", from);
		map.put("limit",limit);
		try {
			list=super.getSqlMapClient().queryForList("Chapter.getKVChapters", map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return list;
	}
	
	@Override
	public List<Chapter> getKVSChapters(int siteId, long from, int limit) {
		List<Chapter> list=null;
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId", siteId);
		map.put("from", from);
		map.put("limit",limit);
		try {
			list=super.getSqlMapClient().queryForList("Chapter.getKVSChapters", map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return list;
	}

	@Override
	public long getKVCount(int siteId) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId",siteId);
		Object object=null;
	    try {
		    object=super.getSqlMapClient().queryForObject("Chapter.getKVCount",map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return (Long)object;
	}
	
	@Override
	public long getKVSCount(int siteId) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId",siteId);
		Object object=null;
	    try {
		    object=super.getSqlMapClient().queryForObject("Chapter.getKVSCount",map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return (Long)object;
	}
	
	@Override
	public boolean deleteChapters(int siteId, long bookId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("siteId",siteId);
		map.put("bookId",bookId);
		try {
			super.getSqlMapClient().delete("Chapter.deleteChapters", map);
		} catch (SQLException e) {
			LOG.error("", e);
			return false;
		}
		return true;
	}
	
	@Override
	public List<Chapter> getChaptersByBookId(int siteId, long bookId) {
		List<Chapter> list=null;
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId", siteId);
		map.put("bookId", bookId);
		try {
			list=super.getSqlMapClient().queryForList("Chapter.getChaptersByBookId", map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return list;
	}
	
	@Override
	public long getHotKVSCount(int siteId, int hot) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId",siteId);
		map.put("hot",hot);
		Object object=null;
	    try {
		    object=super.getSqlMapClient().queryForObject("Chapter.getHotKVSCount",map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return (Long)object;
	}
	
	@Override
	public List<Chapter> getHotKVSChapters(int siteId, int hot, long from,int limit) {
		List<Chapter> list=null;
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId", siteId);
        map.put("hot",hot);
		map.put("from", from);
		map.put("limit",limit);
		try {
			list=super.getSqlMapClient().queryForList("Chapter.getHotKVSChapters", map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return list;
	}
	
	public static void main(String args[]) {
	   ChapterDao chapterDao=(ChapterDao)SpringIoCUtil.getBean("chapterDao");	
	   /*Chapter chapter=new Chapter();
	   chapter.setSiteName("121xs");
	   chapter.setName("这是一个章节");
	   chapter.setUrl("www.url.com");
	   chapter.setSiteId(1);
	   chapter.setBookId(1);
	   chapter.setType(1);
	   chapter.setUpdateTime(new Date());
	   LOG.info(chapterDao.insertBatch(Arrays.asList(new Chapter[]{chapter}),10));*/
	   LOG.info(chapterDao.getChaptersByBookId(13, 1).get(0).getName());
	}

}
