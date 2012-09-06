package com.pw.spider.service.impl;

import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pw.spider.Util.BooleanObject;
import com.pw.spider.Util.ChangeBusyStatus;
import com.pw.spider.Util.Constants;
import com.pw.spider.Util.HtmlUtil;
import com.pw.spider.Util.HttpCrawler;
import com.pw.spider.Util.JsonUtil;
import com.pw.spider.Util.XMLUtil;
import com.pw.spider.dao.RegularDao;
import com.pw.spider.dao.SiteDao;
import com.pw.spider.exception.SpiderException;
import com.pw.spider.model.Book;
import com.pw.spider.model.Regular;
import com.pw.spider.model.Site;
import com.pw.spider.regex.CoverPageRegex;
import com.pw.spider.service.CoverPageService;

public class CoverPageServiceImpl implements CoverPageService {
    
	public static final Logger LOG=Logger.getLogger(CoverPageServiceImpl.class.getName());
	
	public static final int coverCrawlMaxErrorCount=Integer.parseInt(XMLUtil.loadValueByKey("coverCrawlMaxErrorCount"));
	
	public static final long init_serverBusy_sleepTime=Long.parseLong(XMLUtil.loadValueByKey("init_serverBusy_sleepTime"));
	
	private SiteDao siteDao;
	
	private RegularDao regularDao;

	@Override
	public void coverPageCrawl(BooleanObject isCrawlCover_DirError,Book book, HttpClient httpclient) {
		Site site=siteDao.getSiteById(book.getSiteId());
		Regular coverRegular=regularDao.get(site.getName(),Constants.BOOK_INDEX_PAGE_CRAWL_TYPE);
		
		CoverPageRegex coverPageRegex=JsonUtil.Json2CoverPageRegex(coverRegular.getRegex());
		
       	String url=book.getUrl();
       	String coverPageContent=HttpCrawler.crawl(site.getName()+": book cover page", httpclient, url, coverRegular.getCharset());
       
       	/*
       	//deal with 500 and so on server error of some web site
       	int busyCount=0;
       	while(coverPageContent!=null&&coverPageContent.equals(Constants.SERVER_BUSY)) {
       		busyCount++;
       		isCrawlCover_DirError.coverBusy.getAndSet(true);
       		LOG.error("[Website "+site.getName()+"] start sleeping "+busyCount+"*"+init_serverBusy_sleepTime+" while crawling the book cover page "+url+" because of server busy error.");
       		try {
				Thread.sleep(busyCount*init_serverBusy_sleepTime);
			} catch (InterruptedException e) {
				LOG.error("",e);
			}
			coverPageContent=HttpCrawler.crawl(site.getName()+": book cover page", httpclient, url, coverRegular.getCharset());
       	}
       	isCrawlCover_DirError.coverBusy.getAndSet(false);
       	*/
       	
       	if(coverPageContent==null) {
       		if(isCrawlCover_DirError!=null) {
       		  if(isCrawlCover_DirError.continueCoverTimeout.incrementAndGet()>=10) {
       			 //isCrawlCover_DirError.error.getAndSet(true); 
       			 //LOG.error("[Website "+site.getName()+"] stop crawling because of book cover page downloading timeout failed continous 10 times.");
       		     //return;
       			isCrawlCover_DirError.coverTimeout.getAndSet(true);
       			LOG.error("[Website "+site.getName()+"] pause crawling because of book cover page downloading timeout failed continous 10 times.");
       		  }
       		}
        	LOG.error("[Website "+site.getName()+"] Crawl the book cover page "+url+" failed.(timeout)");
            SpiderException downloadException=new SpiderException(site.getId(),book.getId(),url,Constants.COVER_NETWORK_ERR);
            downloadException.saveException();
        	return;
        }
       	if(isCrawlCover_DirError!=null) {
       	   isCrawlCover_DirError.continueCoverTimeout.set(0);
       	}
       	
       	if(coverPageContent!=null&&coverPageContent.equals("302")) {
       	  LOG.info("[Website "+site.getName()+"] Crawl the book cover page "+url+" failed and not retry agin because of redirect(302).");
       	  return;
       	}
       	if(coverPageContent!=null&&coverPageContent.equals(Constants.SERVER_BUSY)) {
       	  if(isCrawlCover_DirError!=null) {
       	     if(isCrawlCover_DirError.coverBusy.getAndSet(true)==false) {
              //ScheduledExecutorService scheduledPool=Executors.newSingleThreadScheduledExecutor();
              //scheduledPool.schedule(new ChangeBusyStatus(isCrawlCover_DirError.coverBusy),init_serverBusy_sleepTime,TimeUnit.MILLISECONDS);
              //scheduledPool.shutdown();
              //LOG.error("[Website "+site.getName()+"] Crawl the book cover page "+url+" failed because of server busy and "+init_serverBusy_sleepTime+"s later crawl cover again.(500)");
       	     }
       	   }
       	   LOG.error("[Website "+site.getName()+"] Crawl the book cover page "+url+" failed because of server busy.(500)");
           SpiderException downloadException=new SpiderException(site.getId(),book.getId(),url,Constants.COVER_NET_BUSY);
           downloadException.saveException();
       	   return;	
       	}
       	if(coverPageContent!=null&&coverPageContent.equals(Constants.NOT_FOUNDSTR)) {
       	   LOG.warn("[Website "+site.getName()+"] Crawl the book cover page "+url+" failed because of 404.");
       	   SpiderException downloadException=new SpiderException(site.getId(),book.getId(),url,Constants.Not_FOUNDINT);
           downloadException.saveException();
           return;	
       	}
       	
       	Pattern p=Pattern.compile(coverPageRegex.getCoverInfoRegex());
       	Matcher m=p.matcher(coverPageContent);
       	if(m.find()) {
       		//crawl coverImgUrl
       		String coverImgUrl=m.group(coverPageRegex.getCoverImgUrlIndex());
       		coverImgUrl = coverImgUrl == null ? "" : coverImgUrl.trim();
       		if (coverImgUrl.indexOf("/") == 0) {
       			coverImgUrl = site.getUrl() + coverImgUrl;
			}
       		if(coverImgUrl.length()>100) {
       			LOG.warn("[Website "+site.getName()+"] the book with name <<"+book.getName()+">> and coverUrl="+url+" is to long coverImgUrl!"+coverImgUrl+"(>100)");
       		} else if(!coverImgUrl.equals("")&&coverImgUrl.toLowerCase().startsWith("http")) {
       			book.setCoverUrl(coverImgUrl);
       		}
       		//crawl brief
       		String brief=m.group(coverPageRegex.getBriefIndex());
       		brief = brief == null ? "" : brief.trim();
       		brief = brief.replaceAll("[<]\\s*[/]?(?:br|BR|bR|Br)\\s*[/]?[>]", "\n");
       		brief = brief.replaceAll("[&]nbsp[;]", "");
       		brief = HtmlUtil.clearHtmlAndEntity(brief).trim();
       		if(brief.length()>500) {
       			LOG.warn("[Website "+site.getName()+"] the book with name <<"+book.getName()+">> and coverUrl="+url+" is to long brief!(>500)");
       		    book.setBrief(brief.substring(0, 500));
       		} else if(!brief.equals("")) {
       			book.setBrief(brief);
       		}
       	} else {
       	  LOG.error("[Website "+site.getName()+"] the book <<"+book.getName()+">> and coverUrl="+url+" cover page regular is not correct because of website changing.coverErrorCount="+isCrawlCover_DirError.coverErrorCount.get());
       	  if(isCrawlCover_DirError!=null) {
       	   if(isCrawlCover_DirError.coverErrorCount.incrementAndGet()>=coverCrawlMaxErrorCount) {
       		 isCrawlCover_DirError.error.getAndSet(true);  
       		 LOG.error("[Website "+site.getName()+"] stop crawling because of book cover page regular changing.");
       		 SpiderException coverRegexError=new SpiderException(site.getId(),-2,"[Website "+site.getName()+"]cover regex error.",Constants.STOP_COVER_REGEX_ERROR);
       		 coverRegexError.saveException();
       	   }
       	  }
       	  SpiderException coverException=new SpiderException(site.getId(),book.getId(),url,Constants.COVER_REGEX_ERR);
          coverException.saveException();
       	}
	}
	
	@Autowired
	public void setSiteDao(SiteDao siteDao) {
		this.siteDao = siteDao;
	}
    
	@Autowired
	public void setRegularDao(RegularDao regularDao) {
		this.regularDao = regularDao;
	}
	
	public static void main(String args[]) {
		LOG.info(new String(new KVContentSaveService().getText(Long.parseLong(args[0]))));
	}
	
}
