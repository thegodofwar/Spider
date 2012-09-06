package com.pw.spider.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pw.spider.Util.BooleanObject;
import com.pw.spider.Util.Constants;
import com.pw.spider.Util.HtmlUtil;
import com.pw.spider.Util.HttpCrawler;
import com.pw.spider.Util.JsonUtil;
import com.pw.spider.Util.SpringIoCUtil;
import com.pw.spider.Util.XMLUtil;
import com.pw.spider.dao.BookDao;
import com.pw.spider.dao.ChapterDao;
import com.pw.spider.dao.RegularDao;
import com.pw.spider.dao.SiteDao;
import com.pw.spider.dao.TomeDao;
import com.pw.spider.exception.SpiderException;
import com.pw.spider.model.Book;
import com.pw.spider.model.Chapter;
import com.pw.spider.model.Regular;
import com.pw.spider.model.Site;
import com.pw.spider.model.Tome;
import com.pw.spider.regex.DirPageRegex;
import com.pw.spider.service.DirPageService;

public class DirPageServiceImpl implements DirPageService {
	
	public static final Logger LOG=Logger.getLogger(DirPageServiceImpl.class.getName());
	
	public static final int dirCrawlMaxErrorCount=Integer.parseInt(XMLUtil.loadValueByKey("dirCrawlMaxErrorCount"));
	
    private SiteDao siteDao;
	
	private RegularDao regularDao;
	
	@Override
	public void dirPageCrawl(BooleanObject isCrawlCover_DirError,Book book, List<Tome> tomes,List<Chapter> chapters, HttpClient httpclient) {
     	Site site=siteDao.getSiteById(book.getSiteId());
     	Regular dirRegular=regularDao.get(site.getName(),Constants.BOOK_DIR_PAGE_CRAWL_TYPE);
		DirPageRegex dirPageRegex=JsonUtil.Json2DirPageRegex(dirRegular.getRegex());
     	
     	String dirUrl=book.getDirUrl();
     	String dirPageContent=HttpCrawler.crawl(site.getName()+": book dir page",httpclient,dirUrl,dirRegular.getCharset());	
	    
     	/*
     	//deal with 500 and so on server error of some web site
       	int busyCount=0;
       	while(dirPageContent!=null&&dirPageContent.equals(Constants.SERVER_BUSY)) {
       		busyCount++;
       		isCrawlCover_DirError.dirBusy.getAndSet(true);
       		LOG.error("[Website "+site.getName()+"] start sleeping "+busyCount+"*"+CoverPageServiceImpl.init_serverBusy_sleepTime+" while crawling the book dir page "+dirUrl+" because of server busy error.");
       		try {
				Thread.sleep(busyCount*CoverPageServiceImpl.init_serverBusy_sleepTime);
			} catch (InterruptedException e) {
				LOG.error("",e);
			}
			dirPageContent=HttpCrawler.crawl(site.getName()+": book dir page",httpclient,dirUrl,dirRegular.getCharset());
       	}
       	isCrawlCover_DirError.dirBusy.getAndSet(false);
       	*/
     	
     	if(dirPageContent==null) {
     	  if(isCrawlCover_DirError!=null) {
         	  if(isCrawlCover_DirError.continueDirTimeout.incrementAndGet()>=10) {
         		 //isCrawlCover_DirError.error.getAndSet(true); 
         		 //LOG.error("[Website "+site.getName()+"] stop crawling because of book dir page downloading timeout failed continous 10 times.");
         		 //return;
         		 isCrawlCover_DirError.dirTimeout.getAndSet(true);
         		 LOG.error("[Website "+site.getName()+"] pause crawling because of book dir page downloading timeout failed continous 10 times.");
         	   }
          }
  	      LOG.warn("[Website "+site.getName()+"] Crawl the book dir page "+dirUrl+" failed.");
  	      SpiderException downloadException=new SpiderException(site.getId(),book.getId(),dirUrl,Constants.DIR_NETWORK_ERR);
  	      downloadException.saveException();
  	      return;
  	    }
     	if(isCrawlCover_DirError!=null) {
           isCrawlCover_DirError.continueDirTimeout.set(0);
        }
     	
     	if(dirPageContent!=null&&dirPageContent.equals(Constants.SERVER_BUSY)) {
     		LOG.error("[Website "+site.getName()+"] Crawl the book dir page "+dirUrl+" failed because of server busy.");
     		SpiderException downloadException=new SpiderException(site.getId(),book.getId(),dirUrl,Constants.DIR_NET_BUSY);
            downloadException.saveException();
     		return;	
     	}
     	if(dirPageContent!=null&&dirPageContent.equals(Constants.NOT_FOUNDSTR)) {
     		LOG.error("[Website "+site.getName()+"] Crawl the book dir page "+dirUrl+" failed because of 404.");
     		SpiderException downloadException=new SpiderException(site.getId(),book.getId(),dirUrl,Constants.Not_FOUNDINT);
            downloadException.saveException();
     		return;	
     	}
     	
	    if(tomes==null||chapters==null) {
	      LOG.error("[Website "+site.getName()+"] Crawl the book dir page "+dirUrl+" the list params tomes or chapters is null.");
	      return;
	    }
	    //crawl tomes
	    crawlTomes(isCrawlCover_DirError,site,dirPageRegex,dirPageContent,book,tomes,chapters,httpclient);
	}
	
    public void crawlTomes(BooleanObject isCrawlCover_DirError,Site site,DirPageRegex dirPageRegex,String dirPageContent,Book book, List<Tome> tomes, List<Chapter> chapters, HttpClient httpclient) {
    	Pattern tomeMainP=Pattern.compile(dirPageRegex.getMainTomeContent());
 	    Matcher tomeMainM=tomeMainP.matcher(dirPageContent);
 	    String mainTomeContent=null;
 	    if(tomeMainM.find()) {
 	    	mainTomeContent=tomeMainM.group(1);
 	    	if(mainTomeContent==null||mainTomeContent.equals("")) {
 	    		LOG.error("[Website "+site.getName()+"] Crawl the book dir page "+book.getDirUrl()+" the mainTomeContent is null.");
 	    		SpiderException dirException=new SpiderException(site.getId(),book.getId(),book.getDirUrl(),Constants.DIR_REGEX_ERR);
 	    		dirException.saveException();
 	    		return;
 	    	} else {
 	    		Pattern tomeNameP=Pattern.compile(dirPageRegex.getTomeName());
 	    		Matcher tomeNameM=tomeNameP.matcher(mainTomeContent);
 	    		Tome t=null;
 	    		int tomeOrder=1;
 	    		while(tomeNameM.find()) {
 	    			t=new Tome();
 	    			t.setBookId(book.getId());
 	    			t.setTomeOrder(tomeOrder);
 	    			String tomeName = HtmlUtil.clearHtmlAndEntity(tomeNameM.group(1));
 	    			tomeName = tomeName == null ? "" : tomeName.replaceAll("&nbsp;", " ").trim();
 	    			t.setName(tomeName);
 	    			t.setTomeMatchStart(tomeNameM.start());
 	    			tomes.add(t);
 	    			tomeOrder++;
 	    		}
 	    		if(tomes.size()==0) {
 	    			t=new Tome();
 	    			t.setBookId(book.getId());
 	    			t.setTomeOrder(tomeOrder);
 	    			t.setName("全卷阅读");
 	    			t.setTomeMatchStart(-1);
 	    			tomes.add(t);
 	    		}
 	    	}
 	    } else {
 	    	LOG.error("[Website "+site.getName()+"] Crawl the book dir page "+book.getDirUrl()+" the regular can not match main tome content.dirErrorCount="+isCrawlCover_DirError.dirErrorCount.get());
 	    	if(isCrawlCover_DirError!=null) {
 	    	 if(isCrawlCover_DirError.dirErrorCount.incrementAndGet()>=dirCrawlMaxErrorCount) {
 	    	   isCrawlCover_DirError.error.getAndSet(true);
 	    	   LOG.error("[Website "+site.getName()+"] stop crawling because of book dir-->tome page regular changing.");
 	    	   SpiderException dirRegexError=new SpiderException(site.getId(),-2,"[Website "+site.getName()+"]dir regex error.",Constants.STOP_DIR_TOME_REGEX_ERROR);
        	   dirRegexError.saveException();
 	    	 }
 	    	}
 	    	SpiderException dirException=new SpiderException(site.getId(),book.getId(),book.getDirUrl(),Constants.DIR_REGEX_ERR);
	    	dirException.saveException();
 	    	return;
 	    } 
 	    //crawl chapters
 	    crawlChapters(isCrawlCover_DirError,site,dirPageRegex,mainTomeContent,book,tomes,chapters,httpclient);
    }
	
    public void crawlChapters(BooleanObject isCrawlCover_DirError,Site site,DirPageRegex dirPageRegex,String mainTomeContent,Book book,List<Tome> tomes,List<Chapter> chapters,HttpClient httpclient) {
    	Tome firstTome=tomes.get(0);
    	Pattern chapterInfoP=Pattern.compile(dirPageRegex.getChapterInfo());
    	Matcher chapterInfoM=chapterInfoP.matcher(mainTomeContent);
    	Chapter chapter = null;
    	int errorCount=0;
    	while(chapterInfoM.find()) {
    	  chapter=new Chapter();
    	  chapter.setSiteName(site.getName());
    	  chapter.setSiteId(site.getId());
    	  chapter.setBookId(book.getId());
    	  chapter.setType(0);
    	  chapter.setChapterMatchStart(chapterInfoM.start());
    	  chapter.setUpdateTime(new Date());
    	  //crawl chapter name
    	  if(!crawlChapterName(site,book,chapter,chapterInfoM,dirPageRegex)) {
    		  errorCount++;
    		  continue;
    	  }
    	  //crawl chapter url
    	  if(!crawlChapterUrl(site,book,chapter,chapterInfoM,dirPageRegex)) {
    		  errorCount++;
    		  continue; 
    	  }
    	  //crawl chapterId
    	  if(!crawlChapterId(site,book,chapter,chapterInfoM,dirPageRegex)) {
    		  errorCount++;
    		  continue;
    	  }
    	  boolean isSetTome = false;
		  for (int m = 0; m < tomes.size(); m++) {
				Tome tempTome = (Tome) tomes.get(m);
				if (m + 1 < tomes.size()) {
					Tome tempNextTome = (Tome) tomes.get(m + 1);
					if (tempTome.getTomeMatchStart() <= chapter
							.getChapterMatchStart()
							&& chapter.getChapterMatchStart() < tempNextTome
									.getTomeMatchStart()) {
						chapter.setTomeOrder(tempTome.getTomeOrder());
						isSetTome = true;
						break;
					}
				} else if (m + 1 == tomes.size()) {
					if (tempTome.getTomeMatchStart() < chapter
							.getChapterMatchStart()) {
						chapter.setTomeOrder(tempTome.getTomeOrder());
						isSetTome = true;
					}
				}
		  }

		  if (!isSetTome) {
			chapter.setTomeOrder(firstTome.getTomeOrder());
			isSetTome = true;
		   }
		  
          if(errorCount>30) {
        	 LOG.error("[Website "+site.getName()+"] your book dir page regular is not correct because of website changing.");
        	 SpiderException dirException=new SpiderException(site.getId(),book.getId(),book.getDirUrl(),Constants.DIR_REGEX_ERR);
	    	 dirException.saveException();
        	 break;
          }
		  chapters.add(chapter);
    	}
    	if(chapters.size()==0) {
    		LOG.error("[Website "+site.getName()+"] your book with name <<"+book.getName()+">> and dirUrl="+book.getDirUrl()+" dir page regular is not correct because of website changing.dirErrorCount="+isCrawlCover_DirError.dirErrorCount.get());
    		if(isCrawlCover_DirError!=null) {
    		 if(isCrawlCover_DirError.dirErrorCount.incrementAndGet()>=dirCrawlMaxErrorCount) {
    		   isCrawlCover_DirError.error.getAndSet(true);	
    		   LOG.error("[Website "+site.getName()+"] stop crawling because of book dir-->chapter page regular changing.");
    		   SpiderException dirRegexError=new SpiderException(site.getId(),-2,"[Website "+site.getName()+"]dir regex error.",Constants.STOP_DIR_CHAPTER_REGEX_ERROR);
        	   dirRegexError.saveException();
    		 }
    		}
    		SpiderException dirException=new SpiderException(site.getId(),book.getId(),book.getDirUrl(),Constants.DIR_REGEX_ERR);
	    	dirException.saveException();
    	}
    }
    
    public boolean crawlChapterName(Site site,Book book,Chapter chapter,Matcher chapterInfoM,DirPageRegex dirPageRegex) {
       String chapterName=chapterInfoM.group(dirPageRegex.getChapterNameIndex());
   	   chapterName=HtmlUtil.clearHtmlAndEntity(chapterName);
   	   chapterName = chapterName == null ? "" : chapterName.replaceAll("&nbsp;", " ").trim();
   	   if(chapterName.equals("")) {
   		 LOG.warn("[Website "+site.getName()+"] the book with name <<"+book.getName()+">> and dirUrl="+book.getDirUrl()+" that has someone chapterName is null.");
   		 return false;
   	   } else {
   		 chapter.setName(chapterName);
    	 return true;  
   	   }
    }
    
    public boolean crawlChapterUrl(Site site,Book book,Chapter chapter,Matcher chapterInfoM,DirPageRegex dirPageRegex) {
       int index=dirPageRegex.getChapterUrlIndex();
       String chapterUrl="";
       if(index>0) {
    	 chapterUrl=chapterInfoM.group(index);
    	 if(chapterUrl!=null&&chapterUrl.indexOf("/") == 0) {
    		chapterUrl=site.getUrl()+chapterUrl;
		 }
       } else {
    	 chapterUrl=chapterInfoM.group(Math.abs(index));
    	 if(dirPageRegex.getChapterUrlReplaceType()==1) {
    		chapterUrl=book.getDirUrl()+chapterUrl;
    	 } else if(dirPageRegex.getChapterUrlReplaceType()==2) {
    		chapterUrl=dirPageRegex.getChapterUrlReplaceStr()+chapterUrl;
    	 } else if(dirPageRegex.getChapterUrlReplaceType()==3) {
    		chapterUrl=book.getDirUrl().replace(dirPageRegex.getChapterUrlReplaceStr(),chapterUrl);
    	 }
       }
       if(chapterUrl!=null&&!chapterUrl.trim().equals("")) {
          Pattern chapterUrlValidateP=Pattern.compile(dirPageRegex.getChapterUrlValidate());
  	      Matcher chapterUrlValidateM=chapterUrlValidateP.matcher(chapterUrl);
  	      if(chapterUrlValidateM.find()) {
  	    	String bookIdValidate=chapterUrlValidateM.group(1);
			if(bookIdValidate==null||!bookIdValidate.equals(book.getBookId()+"")) {
			 LOG.warn("[Website "+site.getName()+"] the book with name <<"+book.getName()+">> and dirUrl="+book.getDirUrl()+" that has wrong BookId in someone chapterUrl."); 
			 return false;
			} 
			chapter.setUrl(chapterUrl.trim());
	  	    return true;
  	      } else {
  	    	LOG.warn("[Website "+site.getName()+"] the book with name <<"+book.getName()+">> and dirUrl="+book.getDirUrl()+" that has no BookId in someone chapterUrl.");
			return false;
  	      }
       } else {
    	  LOG.warn("[Website "+site.getName()+"] the book with name <<"+book.getName()+">> and dirUrl="+book.getDirUrl()+" that has someone chapterUrl is null.");
    	  return false;
       }
    }
    
    public boolean crawlChapterId(Site site,Book book,Chapter chapter,Matcher chapterInfoM,DirPageRegex dirPageRegex) {
    	String chapterIdStr=chapterInfoM.group(dirPageRegex.getChapterIdIndex());
    	if(chapterIdStr==null||chapterIdStr.equals("")) {
    	   LOG.warn("[Website "+site.getName()+"] the book with name <<"+book.getName()+">> and dirUrl="+book.getDirUrl()+" that has someone chapterIdStr is null.");
    	   return false;	
    	} else {
    	   try {
    	     long chapterId=Long.parseLong(chapterIdStr);
    	     chapter.setChapterId(chapterId);
    	   } catch(Exception e) {
    		 LOG.warn("[Website "+site.getName()+"] the book with name <<"+book.getName()+">> and dirUrl="+book.getDirUrl()+" that has someone chapterIdStr is wrong."); 
    	     return false;
    	   }
    	   return true;
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
    	HttpClient httpclient=HttpCrawler.createMultiThreadClient(400, 80, 6000, 9000);
    	List<Tome> tomes=new ArrayList<Tome>();
    	List<Chapter> chapters=new ArrayList<Chapter>();
    	DirPageService dirPageService=(DirPageService)SpringIoCUtil.getBean("dirPageService");
    	BookDao bookDao=(BookDao)SpringIoCUtil.getBean("bookDao");
    	TomeDao tomeDao=(TomeDao)SpringIoCUtil.getBean("tomeDao");
    	ChapterDao chapterDao=(ChapterDao)SpringIoCUtil.getBean("chapterDao");
    	List<Book> books=bookDao.getBookLimit(0, 30);
    	for(Book book:books) {
    	  dirPageService.dirPageCrawl(new BooleanObject(),book, tomes, chapters, httpclient);
    	}
    	for(Tome tome:tomes) {
    		tomeDao.insert(tome);
    	}
        for(Chapter chapter:chapters) {
        	chapterDao.insert(chapter);
        }
        httpclient.getConnectionManager().shutdown();
    }
}
