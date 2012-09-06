package com.pw.spider.service;

import org.apache.http.client.HttpClient;

import com.pw.spider.model.Chapter;

public interface ContentPageService {
  
	public void contentPageCrawl(Chapter chapter,HttpClient httpclient);
	
}
