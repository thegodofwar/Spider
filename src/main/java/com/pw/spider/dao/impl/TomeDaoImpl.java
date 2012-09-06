package com.pw.spider.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.pw.spider.Util.SpringIoCUtil;
import com.pw.spider.dao.TomeDao;
import com.pw.spider.model.Chapter;
import com.pw.spider.model.Tome;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class TomeDaoImpl extends SqlMapClientDaoSupport implements TomeDao {
    
	public static final Logger LOG=Logger.getLogger(TomeDaoImpl.class.getName());
	
	@Override
	public long insert(Tome tome) {
		Object object=null;
    	SqlMapClient sqlMap=null;
    	try {
    		sqlMap=super.getSqlMapClient();
    		sqlMap.startTransaction();
    		object=sqlMap.insert("Tome.insertTome",tome);
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
	public boolean insertBatch(final List<Tome> tomes,final int batchNum) {
		super.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
			   public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {	
				    executor.startBatch();
				    int batch = 0;
				    for (int i=0;i<tomes.size();i++) {
				     Tome tome=tomes.get(i);
				     executor.insert("Tome.insertTome",tome);
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
	public boolean exist(long bookId, int tomeOrder) {
		List<Tome> list=null;
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("bookId",bookId);
		map.put("tomeOrder",tomeOrder);
		try {
			list=super.getSqlMapClient().queryForList("Tome.getByTomeOrder",map);
		} catch (SQLException e) {
			LOG.error("",e);
			return false;
		} 
		if(list==null||list.size()==0) {
			return false;
		} else {
			return true;
		}
	}
	
	public static void main(String args[]) {
		TomeDao tomeDao=(TomeDao)SpringIoCUtil.getBean("tomeDao");
		Tome tome=new Tome();
		tome.setBookId(1);
		tome.setName("全卷阅读");
		tome.setTomeOrder(1);
		tome.setTomeMatchStart(100);
		LOG.info(tomeDao.insert(tome));
	}

}
