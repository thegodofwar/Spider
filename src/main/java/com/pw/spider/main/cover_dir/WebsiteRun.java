package com.pw.spider.main.cover_dir;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;

import com.pw.spider.Util.BooleanObject;
import com.pw.spider.Util.Constants;
import com.pw.spider.Util.HttpCrawler;
import com.pw.spider.Util.SpringIoCUtil;
import com.pw.spider.Util.XMLUtil;
import com.pw.spider.dao.BookDao;
import com.pw.spider.dao.EntryDao;
import com.pw.spider.dao.SiteDao;
import com.pw.spider.dao.TomeDao;
import com.pw.spider.dao.ChapterDao;
import com.pw.spider.exception.SpiderException;
import com.pw.spider.model.Book;
import com.pw.spider.model.Chapter;
import com.pw.spider.model.Entry;
import com.pw.spider.model.Site;
import com.pw.spider.model.Tome;
import com.pw.spider.service.CoverPageService;
import com.pw.spider.service.DirPageService;
import com.pw.spider.service.UpdatePageService;
import com.pw.spider.service.impl.CoverPageServiceImpl;

public class WebsiteRun implements Runnable {
    
	public static final Logger LOG=Logger.getLogger(WebsiteRun.class.getName());
	
	public static final int connectionTimeout=Integer.parseInt(XMLUtil.loadValueByKey("connectionTimeout"));
	
	public static final int soTimeout=Integer.parseInt(XMLUtil.loadValueByKey("soTimeout"));
	
	public static final int insertBatchNum=Integer.parseInt(XMLUtil.loadValueByKey("insertBatchNum"));
	
    private UpdatePageService updatePageService=(UpdatePageService)SpringIoCUtil.getBean("updatePageService");
	
	private CoverPageService coverPageService=(CoverPageService)SpringIoCUtil.getBean("coverPageService");
	
	private DirPageService dirPageService=(DirPageService)SpringIoCUtil.getBean("dirPageService");
	
	private BookDao bookDao=(BookDao)SpringIoCUtil.getBean("bookDao");
	
	private TomeDao tomeDao=(TomeDao)SpringIoCUtil.getBean("tomeDao");
	
	private ChapterDao chapterDao=(ChapterDao)SpringIoCUtil.getBean("chapterDao");
	
	private EntryDao entryDao=(EntryDao)SpringIoCUtil.getBean("entryDao");
	
	private volatile BooleanObject isCrawlCover_DirError=new BooleanObject();
	
	public volatile CopyOnWriteArraySet<Long> set=new CopyOnWriteArraySet<Long>(); 
	
	private Site site;
	
	private Entry entry;
	
	public WebsiteRun(Site site,Entry entry) {
		this.site=site;
		this.entry=entry;
	}
	
	@Override
	public void run() {
		int threadNum=Runtime.getRuntime().availableProcessors()*3;
		//threadPool used for crawling book cover page and book dir page thread
        ExecutorService threadPool=Executors.newFixedThreadPool(15);
        
        HttpClient httpclient=HttpCrawler.createMultiThreadClient(400, 80,connectionTimeout,soTimeout);
         
        Date lastSpideTime=entry.getLastTime();
        Date latestUpdateTime=null;
        
        int continueTimeoutError=0;
        int updatePageCrawlIndex=entryDao.getUpdatePageCrawlIndex(entry.getSiteId());
        if(updatePageCrawlIndex>=entry.getTotalPage()) {
        	LOG.error("[Website "+site.getName()+"] the updatePageCrawlIndex in DB is more than or equal to total page number.");
        	isCrawlCover_DirError.error.getAndSet(true);
        	threadPool.shutdownNow();
        	httpclient.getConnectionManager().shutdown();
        	return;
        }
        
        for(int i=updatePageCrawlIndex;i<entry.getTotalPage();i++) {
        	List<Book> books=updatePageService.updatePageCrawl(site,entry,i,httpclient);
        	
        	//continue crawling if download someone book update page failed.
        	//stop crawling if book update page regular changed.
        	if(books==null) {
        	   continueTimeoutError++;
        	   if(continueTimeoutError==10) {
        		  LOG.error("[Website "+site.getName()+"] stop crawling because of book update page downloading timeout failed continous 10 times.");
        		  SpiderException timeOutError=new SpiderException(site.getId(),-2,"[Website "+site.getName()+"]update timeout.",Constants.STOP_TIMEOUT);
        		  timeOutError.saveException();
        		  isCrawlCover_DirError.error.getAndSet(true);
        		  latestUpdateTime=null;
        		  entryDao.updateUpdatePageCrawlIndex(entry.getSiteId(),i-10+1);
           	      break;
        	   }
        	   if(i==entry.getTotalPage()-1) {
             	  entryDao.updateUpdatePageCrawlIndex(entry.getSiteId(),0);
               } else {
             	  entryDao.updateUpdatePageCrawlIndex(entry.getSiteId(), i);
               }
        	   continue;
        	} else if(books.size()<10) {
        	   LOG.error("[Website "+site.getName()+"] stop crawling because of book update page regular changing.");
        	   SpiderException regexError=new SpiderException(site.getId(),-2,"[Website "+site.getName()+"]update regex error.",Constants.STOP_UPDATE_REGEX_ERROR);
        	   regexError.saveException();
        	   isCrawlCover_DirError.error.getAndSet(true);
        	   latestUpdateTime=null;
        	   entryDao.updateUpdatePageCrawlIndex(entry.getSiteId(),i);
        	   break;
        	}
        	continueTimeoutError=0;
        	
        	boolean isCrawlOn=true;
        	for(int j=0;j<books.size();j++) {
        		Book book=books.get(j);
        		if(latestUpdateTime==null) {
        		   latestUpdateTime=book.getWebUpdateTime();
        		}
        		isCrawlOn=judgeIsCrawlOn(book,lastSpideTime);
        		if(!isCrawlOn) {
        			entryDao.updateUpdatePageCrawlIndex(entry.getSiteId(),0);
        		    break;	
        		}
        		
        		Book dbBook=bookDao.getBySiteId_Name_Author(site.getId(),book.getName(),book.getAuthor());
        		if(dbBook==null) {
        			book.setCreateTime(new Date());
        			long Id=bookDao.insert(book);//return the primary key id of book 
        			book.setId(Id);
        			book.setFirstTag(1);
        		} else {
        			book.setId(dbBook.getId());
        			book.setBrief(dbBook.getBrief());
        			book.setCoverUrl(dbBook.getCoverUrl());
        			book.setFirstTag(0);
        			book.setHot(dbBook.getHot());
        		}
        		
        		if(!set.contains(book.getId())) {
        		   //if crawl cover page or dir page error,then stop crawling
        	       set.add(book.getId());
        		   CrawlCoverDirRun crawlCoverDirRun=new CrawlCoverDirRun(set,isCrawlCover_DirError,book,site,httpclient,
        	                     coverPageService,dirPageService,bookDao,tomeDao,chapterDao);
        		   threadPool.execute(crawlCoverDirRun);
        		}
        		
        		if(isCrawlCover_DirError.error.get()==true) {
        			isCrawlOn=false;
        			latestUpdateTime=null;
        			entryDao.updateUpdatePageCrawlIndex(entry.getSiteId(),i);
        			break;
        		}
        		  
        		try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					LOG.error("",e);
				}
        		//when crawl cover or dir page busy,update page crawling sleep and stop submitting task again
        		if(isCrawlCover_DirError.coverTimeout.get()==true) {
        			LOG.error("[Website "+site.getName()+"] book update page starts sleeping "+CoverPageServiceImpl.init_serverBusy_sleepTime+" because of crawling book cover page continous timeout error.");
        			try {
						Thread.sleep(CoverPageServiceImpl.init_serverBusy_sleepTime);
						isCrawlCover_DirError.coverTimeout.set(false);
					} catch (InterruptedException e) {
						LOG.error("",e);
					}
        		}
        		if(isCrawlCover_DirError.dirTimeout.get()==true) {
        			LOG.error("[Website "+site.getName()+"] book update page starts sleeping "+CoverPageServiceImpl.init_serverBusy_sleepTime+" because of crawling book dir page continous timeout error.");
        			try {
						Thread.sleep(CoverPageServiceImpl.init_serverBusy_sleepTime);
						isCrawlCover_DirError.dirTimeout.set(false);
					} catch (InterruptedException e) {
						LOG.error("",e);
					}
        		}
        		
        	}
        	
        	if(!isCrawlOn) {
        		break;
        	}
        	if(i==entry.getTotalPage()-1) {
        	  entryDao.updateUpdatePageCrawlIndex(entry.getSiteId(),0);
        	} else {
        	  entryDao.updateUpdatePageCrawlIndex(entry.getSiteId(), i);
        	}
        }
        //stop submitting thread
        threadPool.shutdown();
        //wait until finishing all CrawlCoverDirRun#thread in threadPool 
        try {
			while(!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) {
			}
		} catch (InterruptedException e) {
			LOG.error("",e);
		}
		//close httpclient
		httpclient.getConnectionManager().shutdown();
		//update entry set lastTime=#latestUpdateTime#
        if(latestUpdateTime!=null) {
          Date realLatestUpdateTime=bookDao.getMaxWebUpdateTime(entry.getSiteId());
          if(lastSpideTime==null||lastSpideTime.before(realLatestUpdateTime)) {
         	 entryDao.updateLastTime(entry.getSiteId(),realLatestUpdateTime);
         	 LOG.info("[Website "+site.getName()+"] crawl a loop successfully and wait... "+MainCover_DirRun.CRAWL_INTERVAL+"s");
          } else if(lastSpideTime.after(realLatestUpdateTime)) {
        	 LOG.error("[Website "+site.getName()+"] the lastSpideTime is after latestUpdateTime.");
          } else {
        	 LOG.info("[Website "+site.getName()+"] the lastSpideTime is equal to latestUpdateTime.");
        	 LOG.info("[Website "+site.getName()+"] crawl a loop successfully and wait... "+MainCover_DirRun.CRAWL_INTERVAL+"s");
          }
        } else {
           LOG.error("[Website "+site.getName()+"] no need to update lastTime because of network or regex error.");
        }
	}
	
	public boolean judgeIsCrawlOn(Book book,Date lastSpideTime) {
		if(lastSpideTime==null) {
		  	return true;
		}
		if(lastSpideTime.before(book.getWebUpdateTime())) {
			return true;
		} else if(lastSpideTime.after(book.getWebUpdateTime())) {
			LOG.info("[Website "+site.getName()+"] the lastSpideTime is after book.getUpdateTime().");
			return false;
		} else {
			Book dbBook=bookDao.getBySiteId_Name_Author(site.getId(),book.getName(),book.getAuthor());
			if(dbBook==null) {
				return true;
			} else if(dbBook.getNewChapterName().equals(book.getNewChapterName())
					&&dbBook.getNewChapterUrl().equals(book.getNewChapterUrl())) {
				return false;
			} else {
				return true;
			}
		}
	}
    
    public void hotRun() {	
    	int threadNum=Runtime.getRuntime().availableProcessors()*3;
		//threadPool used for crawling book cover page and book dir page thread
        ExecutorService threadPool=Executors.newFixedThreadPool(10);
        final HttpClient httpclient=HttpCrawler.createMultiThreadClient(400, 80,connectionTimeout,soTimeout);
        
        LOG.info("[Website " + site.getName()+ "] starts crawling hot books...");
        
        List<Book> hotBooks=bookDao.getAllHotBooks(site.getId());
        while(!hotBooks.isEmpty()) {
        	final Book tmpBook=hotBooks.remove(0);
        	threadPool.execute(new Runnable() {
        		public void run() {
        			long hourInteval=(new Date().getTime()-tmpBook.getWebUpdateTime().getTime())/(1000*60*60);
        			if(hourInteval<=6) {
        				return;
        			}
        			LOG.info("[Website " + site.getName()+ "] hot book[id="+tmpBook.getId()+"] with hourInteval="+hourInteval);
        			List<Tome> tomes = new ArrayList<Tome>();
             		List<Chapter> chapters = new ArrayList<Chapter>();
             		
					dirPageService.dirPageCrawl(isCrawlCover_DirError, tmpBook, tomes, chapters, httpclient);
					for (int ii = 0; ii < tomes.size(); ii++) {
						Tome tmpTome = tomes.get(ii);
						if (!tomeDao.exist(tmpBook.getId(), tmpTome.getTomeOrder())) {
							tomeDao.insert(tmpTome);
						}
					}
					if (chapters.size() != 0) {
						Chapter tempFirstChapter = chapters.get(0);
						Chapter dbFirstChapter = chapterDao.getChapter(site.getId(), tmpBook.getId(), tempFirstChapter.getName(), tempFirstChapter.getUrl());
						if (dbFirstChapter == null) {
							LOG.info("[Website "+ site.getName()+ "] hot book can not find the old first chapter.");
							chapterDao.deleteChapters(site.getId(), tmpBook.getId());
							chapterDao.insertBatch(chapters,WebsiteRun.insertBatchNum);
						} else {
							for (int jj = chapters.size()-1; jj>=0; jj--) {
								Chapter tempChapter = chapters.get(jj);
								try {
									if (chapterDao.getChapter(site.getId(),tmpBook.getId(),tempChapter.getName(),tempChapter.getUrl()) == null) {
										chapterDao.insert(tempChapter);
									} else {
										break;
									}
								} catch (Exception e) {
									LOG.info("[Website " + site.getName()+ "] has repeated chapter.");
									continue;
								}
							}
						}
					}
					if (chapters.size() != 0) {
						Chapter theLastChapter = chapters.get(chapters.size() - 1);
						bookDao.updateLatestChapter(tmpBook.getId(),theLastChapter.getName(), theLastChapter.getUrl(), new Date());
					}
        		}
        	});
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOG.error("",e);
			}
        }
        //stop submitting thread
        threadPool.shutdown();
        //wait until finishing all CrawlCoverDirRun#thread in threadPool 
        try {
			while(!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) {
			}
		} catch (InterruptedException e) {
			LOG.error("",e);
		}
		//close httpclient
		httpclient.getConnectionManager().shutdown();
		LOG.info("[Website " + site.getName()+ "] has crawled all hot books.");
    }
	
	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public Entry getEntry() {
		return entry;
	}

	public void setEntry(Entry entry) {
		this.entry = entry;
	}
	
	public BooleanObject getIsCrawlCover_DirError() {
		return isCrawlCover_DirError;
	}

	public void setIsCrawlCover_DirError(BooleanObject isCrawlCover_DirError) {
		this.isCrawlCover_DirError = isCrawlCover_DirError;
	}
	
	public static void main(String args[]) {
		SiteDao siteDao=(SiteDao)SpringIoCUtil.getBean("siteDao");
		EntryDao entryDao=(EntryDao)SpringIoCUtil.getBean("entryDao");
		Site site=siteDao.getSiteByName("121xs");
		Entry entry=entryDao.getBySiteId(site.getId());
		new Thread(new WebsiteRun(site,entry)).start();
	}
	
}
