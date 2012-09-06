package com.pw.spider.Util;

public class Constants {
	
	//the path of config file
	public static final String CONFIG="config.xml";
	
	//crawl service type
	public static final int BOOK_UPDATE_PAGE_CRAWL_TYPE=1;
	
	public static final int BOOK_INDEX_PAGE_CRAWL_TYPE=2;
	
	public static final int BOOK_DIR_PAGE_CRAWL_TYPE=3;
	
	public static final int BOOK_CHAPTER_PAGE_CRAWL_TYPE=4;
	
	//for exceptions
	public static final int PAGE_REGEX_ERR = 1;//书更页面正则错误
	
	public static final int COVER_REGEX_ERR = 2;//书封页面正则错误
	
	public static final int DIR_REGEX_ERR = 3;//目录页面正则错误
	
	public static final int CONTENT_REGEX_ERR = 4;//内容页面正则错误
	
	public static final int UPDATE_NETWORK_ERR = 5;//下载网络错误timeout
	
	public static final int OTHER_ERR = 6;//其他错误kv store failed
	
	public static final int COVER_NET_BUSY=7;//爬书封面页时500
	
	public static final int DIR_NET_BUSY=8;//爬书目录页时500
	
	public static final int Not_FOUNDINT=9;//没有找到404
	
	public static final int COVER_NETWORK_ERR=10;//下载网络错误timeout
	
	public static final int DIR_NETWORK_ERR=11;//下载网络错误timeout
	
	public static final int COVER_STATUS_UNKNOW=12;//下载失败原因未知
	
	//stop
	public static final int STOP_TIMEOUT=-1;
	
	public static final int STOP_UPDATE_REGEX_ERROR=-2;
	
	public static final int STOP_COVER_REGEX_ERROR=-3;
	
	public static final int STOP_DIR_TOME_REGEX_ERROR=-4;
	
	public static final int STOP_DIR_CHAPTER_REGEX_ERROR=-5;
	
	
	//chapter content type 
	public static final int CONTENT_WORD_TYPE=1;
	
	public static final int CONTENT_PICTURE_TYPE=2;
	
	//when crawling too frequent,then server will return status 500
	public static final String SERVER_BUSY="SERVER BUSY";
	
	public static final String NOT_FOUNDSTR="NOT FOUND";
	
}
