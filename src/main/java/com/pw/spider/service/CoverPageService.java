package com.pw.spider.service;


import org.apache.http.client.HttpClient;

import com.pw.spider.Util.BooleanObject;
import com.pw.spider.model.Book;

public interface CoverPageService {
   
	public void coverPageCrawl(BooleanObject isCrawlCover_DirError,Book book,HttpClient httpclient);
	
}
