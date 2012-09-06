package com.pw.spider.main.cover_dir;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;

import com.pw.spider.Util.BooleanObject;
import com.pw.spider.Util.Constants;
import com.pw.spider.dao.BookDao;
import com.pw.spider.dao.ChapterDao;
import com.pw.spider.dao.TomeDao;
import com.pw.spider.exception.SpiderException;
import com.pw.spider.model.Book;
import com.pw.spider.model.Chapter;
import com.pw.spider.model.Site;
import com.pw.spider.model.Tome;
import com.pw.spider.service.CoverPageService;
import com.pw.spider.service.DirPageService;

public class CrawlCoverDirRun implements Runnable {
	
	public static final Logger LOG=Logger.getLogger(CrawlCoverDirRun.class.getName());
	
	private CopyOnWriteArraySet<Long> set;
	private BooleanObject isCrawlCover_DirError;
	private Book book;
	private Site site;
	private HttpClient httpclient;
	private CoverPageService coverPageService;
	private DirPageService dirPageService;
	private BookDao bookDao;
	private TomeDao tomeDao;
	private ChapterDao chapterDao;
	
	public CrawlCoverDirRun(CopyOnWriteArraySet<Long> set,BooleanObject isCrawlCover_DirError,Book book, Site site, HttpClient httpclient,
			CoverPageService coverPageService, DirPageService dirPageService,
			BookDao bookDao, TomeDao tomeDao, ChapterDao chapterDao) {
		super();
		this.set=set;
		this.isCrawlCover_DirError=isCrawlCover_DirError;
		this.book = book;
		this.site = site;
		this.httpclient = httpclient;
		this.coverPageService = coverPageService;
		this.dirPageService = dirPageService;
		this.bookDao = bookDao;
		this.tomeDao = tomeDao;
		this.chapterDao = chapterDao;
	}

	@Override
	public void run() {
		List<Tome> tomes = new ArrayList<Tome>();
		List<Chapter> chapters = new ArrayList<Chapter>();
		if(book.getFirstTag()==1) {
		   if(isCrawlCover_DirError.coverBusy.get()==false) {
		     coverPageService.coverPageCrawl(isCrawlCover_DirError,book, httpclient);
		     if(book.getBrief()!=null||book.getCoverUrl()!=null) {
			   bookDao.updateCover(book.getId(),book.getBrief(),book.getCoverUrl());
		     }
		   } else {
			 SpiderException downloadException=new SpiderException(site.getId(),book.getId(),book.getUrl(),Constants.COVER_STATUS_UNKNOW);
	         downloadException.saveException(); 
		   }
		   dirPageService.dirPageCrawl(isCrawlCover_DirError,book, tomes, chapters, httpclient);
		   if(tomes.size()!=0) {
			 tomeDao.insertBatch(tomes,WebsiteRun.insertBatchNum);   
		   }
		   if(chapters.size()!=0) {
			 chapterDao.insertBatch(chapters,WebsiteRun.insertBatchNum);
		   }
		   //deal with the latest chapter url is dirUrl.
		   //e.g:http://www.qingdi.com/Classification/1.html
		   if(chapters.size()!=0) {
		     Chapter theLastChapter=chapters.get(chapters.size()-1);
		     if(book.getNewChapterUrl()==null) {
			    bookDao.updateLatestChapter(book.getId(),theLastChapter.getName(),theLastChapter.getUrl(),book.getWebUpdateTime()); 
		     }
		   }
		   set.remove(book.getId());
		} else {
		   if(book.getHot()==1) {
			  LOG.info("[Website "+site.getName()+"] book[id="+book.getId()+"] is hot and has updated.");
			  set.remove(book.getId());
			  return;
		   }
		   Book dbbBook=bookDao.getBySiteId_Name_Author(site.getId(),book.getName(),book.getAuthor());
		   dirPageService.dirPageCrawl(isCrawlCover_DirError,book, tomes, chapters, httpclient);
		   for(int ii=0;ii<tomes.size();ii++) {
			  Tome tmpTome=tomes.get(ii);
			  if(!tomeDao.exist(book.getId(),tmpTome.getTomeOrder())) {
				  tomeDao.insert(tmpTome);
			  }
		   }
		   Chapter latestChapter=chapterDao.getChapter(site.getId(),book.getId(),dbbBook.getNewChapterName(),dbbBook.getNewChapterUrl());
		   if(latestChapter==null) {
			  if(chapters.size()!=0) { 
			    Chapter tempFirstChapter=chapters.get(0);
			    Chapter dbFirstChapter=chapterDao.getChapter(site.getId(),book.getId(),tempFirstChapter.getName(),tempFirstChapter.getUrl());
			    if(dbFirstChapter==null) {
			      LOG.info("[Website "+site.getName()+"] can not find the old first chapter.");
				  chapterDao.deleteChapters(site.getId(),book.getId());
				  chapterDao.insertBatch(chapters,WebsiteRun.insertBatchNum);
			    } else {
				  for(int jj=0;jj<chapters.size();jj++) {
					Chapter tempChapter=chapters.get(jj);
					try {
					   if(chapterDao.getChapter(site.getId(),dbbBook.getId(),tempChapter.getName(),tempChapter.getUrl())==null) {
						  chapterDao.insert(tempChapter);
					   }
					} catch(Exception e) {
					  LOG.info("[Website "+site.getName()+"] has repeated chapter.");
					  continue;
					}
			      }
			   }
		    }
		   } else {
			  Iterator<Chapter> itChapter=chapters.iterator();
			  while(itChapter.hasNext()) {
				  Chapter tmpChapter=itChapter.next();
				  if(tmpChapter.getChapterId()<=latestChapter.getChapterId()) {
					  itChapter.remove();
				  }
			  }
			  chapterDao.insertBatch(chapters,WebsiteRun.insertBatchNum);
		   }
		   if(chapters.size()!=0) {
		     if(book.getNewChapterUrl()!=null) {
		        bookDao.updateLatestChapter(book.getId(),book.getNewChapterName(),book.getNewChapterUrl(),book.getWebUpdateTime());
		     } else {
			    Chapter theLastChapter=chapters.get(chapters.size()-1);
			    bookDao.updateLatestChapter(book.getId(),theLastChapter.getName(),theLastChapter.getUrl(),book.getWebUpdateTime()); 
		     }
		   }
		   set.remove(book.getId());
		}
	}
    
	public CopyOnWriteArraySet<Long> getSet() {
		return set;
	}

	public void setSet(CopyOnWriteArraySet<Long> set) {
		this.set = set;
	}
	
	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public HttpClient getHttpclient() {
		return httpclient;
	}

	public void setHttpclient(HttpClient httpclient) {
		this.httpclient = httpclient;
	}

	public CoverPageService getCoverPageService() {
		return coverPageService;
	}

	public void setCoverPageService(CoverPageService coverPageService) {
		this.coverPageService = coverPageService;
	}

	public DirPageService getDirPageService() {
		return dirPageService;
	}

	public void setDirPageService(DirPageService dirPageService) {
		this.dirPageService = dirPageService;
	}

	public BookDao getBookDao() {
		return bookDao;
	}

	public void setBookDao(BookDao bookDao) {
		this.bookDao = bookDao;
	}

	public TomeDao getTomeDao() {
		return tomeDao;
	}

	public void setTomeDao(TomeDao tomeDao) {
		this.tomeDao = tomeDao;
	}

	public ChapterDao getChapterDao() {
		return chapterDao;
	}

	public void setChapterDao(ChapterDao chapterDao) {
		this.chapterDao = chapterDao;
	}

}
