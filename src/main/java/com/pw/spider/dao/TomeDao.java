package com.pw.spider.dao;

import java.util.List;

import com.pw.spider.model.Tome;

public interface TomeDao {
   
	public long insert(Tome tome);
	
	public boolean insertBatch(List<Tome> tomes,int batchNum);
	
	public boolean exist(long bookId,int tomeOrder);
	
}
