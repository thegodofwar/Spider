package com.pw.spider.service;

import java.util.List;

import org.apache.http.client.HttpClient;

import com.pw.spider.model.Book;
import com.pw.spider.model.Entry;
import com.pw.spider.model.Site;

public interface UpdatePageService {
    
	public List<Book> updatePageCrawl(Site site,Entry entry,int pageIndex,HttpClient httpClient);
	
}
