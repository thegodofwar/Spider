package com.pw.spider.exception;

import org.apache.log4j.Logger;

import com.pw.spider.Util.Constants;
import com.pw.spider.Util.SpringIoCUtil;
import com.pw.spider.dao.ErrorDao;
import com.pw.spider.model.Error;

public class SpiderException extends Exception{
	private static final Logger LOG = Logger.getLogger(SpiderException.class);
	private static final long serialVersionUID = -6430649497068916372L;
	private long bookId;
	private String url;
	private int type;
	private int site;
	
	public SpiderException(int siteId, String url){
		this.url = url;
		this.site = siteId;
		this.type = Constants.OTHER_ERR;
	}
	
	public SpiderException(int siteId,long bookId,String url, int type){
		this.site = siteId;
		this.bookId=bookId;
		this.url = url;
		this.type = type;
	}
	
	public void saveException(){
		try{
			ErrorDao ed = (ErrorDao) SpringIoCUtil.getBean("errorDao");
			Error error = new Error(site,bookId,url,type);
			Error dbE=ed.getError(bookId,url);
			if(dbE==null) {
			   ed.insert(error);
			} else {
			   ed.delete(dbE.getId());
			   ed.insert(error);
			}
		}catch(Throwable t){
			LOG.error("saveException error:" + t);
		}
	}

}
