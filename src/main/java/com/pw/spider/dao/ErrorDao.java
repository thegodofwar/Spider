package com.pw.spider.dao;

import java.util.List;

import com.pw.spider.model.Error;

public interface ErrorDao {
   
	public long insert(Error error);
	
	public boolean delete(long id);
	
	public long getRetrysCount();
	
	public List<Error> getRetrys(long start,int limit);
	
	public Error getError(long bookId,String url);
	
}
