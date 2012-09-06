package com.pw.spider.main.cover_dir;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.pw.spider.Util.SpringIoCUtil;
import com.pw.spider.Util.XMLUtil;
import com.pw.spider.dao.EntryDao;
import com.pw.spider.dao.SiteDao;
import com.pw.spider.model.Entry;
import com.pw.spider.model.Site;

public class MainCover_DirRun {
	public static final int CRAWL_INTERVAL=Integer.parseInt(XMLUtil.loadValueByKey("crawlCover_DirInterval"));
	
	public static final Logger LOG=Logger.getLogger(MainCover_DirRun.class.getName());
	
    public static void main(String args[]) {
    	      //crawl("qingdi");
              crawlAll();    	
    }
    
    public static void crawl(String siteName) {
    	//get each site and entry
    	SiteDao siteDao=(SiteDao)SpringIoCUtil.getBean("siteDao");
		Site site=siteDao.getSiteByName(siteName);
        //do scheduling crawling task
		// int corePoolSize=Runtime.getRuntime().availableProcessors()*3;
		// ScheduledExecutorService scheduledPool=Executors.newScheduledThreadPool(corePoolSize );
		// scheduledPool.scheduleWithFixedDelay(new WebsiteRun(site,entry),1,CRAWL_INTERVAL,TimeUnit.SECONDS);
        doCrawl(site);
    }
    
    public static void crawlAll() {
    	SiteDao siteDao=(SiteDao)SpringIoCUtil.getBean("siteDao");
		List<Site> sites=siteDao.getAll();
		if(sites==null||sites.size()==0) {
			LOG.error("No any site in DB.");
			return;
		}
		for(int i=0;i<sites.size();i++) {
			final Site s=sites.get(i);
			new Thread(new Runnable() {
				public void run() {
					doCrawl(s);
				}
			}).start();
		}
    }
    
    public static void doCrawl(Site site) {
    	EntryDao entryDao=(EntryDao)SpringIoCUtil.getBean("entryDao");
		while(true) {
			Entry entry=entryDao.getBySiteId(site.getId());
			WebsiteRun websiteRun=new WebsiteRun(site,entry);
			websiteRun.hotRun();
		    websiteRun.run();
		    if(websiteRun.getIsCrawlCover_DirError().error.get()==true) {
		    	break;
		    }
			try {
				Thread.sleep(CRAWL_INTERVAL*1000);
			} catch (InterruptedException e) {
				LOG.error("",e);
			}
		}
    }
    
}
