package com.pw.spider.service;

import java.util.List;

import org.apache.http.client.HttpClient;

import com.pw.spider.Util.BooleanObject;
import com.pw.spider.model.Book;
import com.pw.spider.model.Chapter;
import com.pw.spider.model.Tome;

public interface DirPageService {
   
	public void dirPageCrawl(BooleanObject isCrawlCover_DirError,Book book,List<Tome> tomes,List<Chapter> chapters,HttpClient httpclient);
	
}
