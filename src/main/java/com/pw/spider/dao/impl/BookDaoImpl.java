package com.pw.spider.dao.impl;

import java.sql.SQLException;
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
import com.pw.spider.dao.BookDao;
import com.pw.spider.model.Book;

public class BookDaoImpl extends SqlMapClientDaoSupport implements BookDao {
	
	public static final Logger LOG=Logger.getLogger(BookDaoImpl.class.getName());
	
	@Override
	public long insert(Book book) {
		Object object=null;
    	SqlMapClient sqlMap=null;
    	try {
    		sqlMap=super.getSqlMapClient();
    		sqlMap.startTransaction();
    		object=sqlMap.insert("Book.insertBook",book);
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
	public List<Book> getBookLimit(long start, long limit) {
		List<Book> list=null;
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("start",start);
		map.put("limit",limit);
		try {
			list=super.getSqlMapClient().queryForList("Book.getBookLimit", map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return list;
	}
	
	@Override
	public boolean insertBacth(final List<Book> books, final int batchNum) {
		super.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
		   public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {	
			    executor.startBatch();
			    int batch = 0;
			    for (int i=0;i<books.size();i++) {
			     Book book=books.get(i);
			     executor.insert("Book.insertBook",book);
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
	public Book getBySiteId_Name_Author(int siteId, String bookName,String authorName) {
		Object object=null;
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId",siteId);
		map.put("bookName",bookName);
		map.put("authorName",authorName);
		try {
			object=super.getSqlMapClient().queryForObject("Book.getBySiteIdNameAuthor",map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return (Book)object;
	}
	
	@Override
	public Book getById(long id) {
		Object object=null;
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("id",id);
		try {
			object=super.getSqlMapClient().queryForObject("Book.getById",map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return (Book)object;
	}
	
	@Override
	public boolean updateCover(long id, String brief, String coverUrl) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("id",id);
		map.put("brief",brief);
		map.put("coverUrl",coverUrl);
		try {
			getSqlMapClient().update("Book.updateCover", map);
		} catch (SQLException e) {
			LOG.error("",e);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean updateLatestChapter(long id, String newChapterName,String newChapterUrl,Date webUpdateTime) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("id",id);
		map.put("newChapterName",newChapterName);
		map.put("newChapterUrl",newChapterUrl);
		map.put("webUpdateTime",webUpdateTime);
		try {
			getSqlMapClient().update("Book.updateLatestChapter", map);
		} catch (SQLException e) {
			LOG.error("",e);
			return false;
		}
		return true;
	}
	
	@Override
	public Date getMaxWebUpdateTime(int siteId) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId",siteId);
		Object object=null;
	    try {
		    object=super.getSqlMapClient().queryForObject("Book.getMaxWebUpdateTime",map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return (Date)object;
	}
	
	@Override
	public List<Book> getAllHotBooks(int siteId) {
		List<Book> list=null;
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("siteId",siteId);
		try {
			list=super.getSqlMapClient().queryForList("Book.getAllHotBooks",map);
		} catch (SQLException e) {
			LOG.error("",e);
		} 
		return list;
	}
	
	public static void main(String args[]) {
	  Book book=new Book();
	  /*book.setSiteId(1);
	  book.setName("永生");
	  book.setAuthor("梦入神机");
	  book.setUrl("www.indexUrl.com");
	  book.setDirUrl("www.driUrl.com");
	  book.setCoverUrl("www.coverImgUrl.com");
	  book.setBrief("一本很好的书");
	  book.setCategory("奇幻");
	  book.setNewChapterName("最新章节");
	  book.setNewChapterUrl("www.newUrl.com");*/
	  BookDao bookDao=(BookDao)SpringIoCUtil.getBean("bookDao");
	  LOG.info(bookDao.getAllHotBooks(13).get(0).getAuthor()); 
	}

}
