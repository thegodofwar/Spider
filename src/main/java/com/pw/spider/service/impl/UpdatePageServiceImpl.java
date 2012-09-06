package com.pw.spider.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pw.spider.Util.BooleanObject;
import com.pw.spider.Util.Constants;
import com.pw.spider.Util.HttpCrawler;
import com.pw.spider.Util.JsonUtil;
import com.pw.spider.Util.SpringIoCUtil;
import com.pw.spider.dao.BookDao;
import com.pw.spider.dao.EntryDao;
import com.pw.spider.dao.RegularDao;
import com.pw.spider.dao.SiteDao;
import com.pw.spider.exception.SpiderException;
import com.pw.spider.model.Book;
import com.pw.spider.model.Entry;
import com.pw.spider.model.Regular;
import com.pw.spider.model.Site;
import com.pw.spider.regex.UpdatePageRegex;
import com.pw.spider.service.CoverPageService;
import com.pw.spider.service.UpdatePageService;

public class UpdatePageServiceImpl implements UpdatePageService {
    
	public static final Logger LOG=Logger.getLogger(UpdatePageServiceImpl.class.getName());
	
	private RegularDao regularDao;
	
	private EntryDao entryDao;
	
	private SiteDao siteDao;
	
	@Override
	public List<Book> updatePageCrawl(Site site,Entry entry,int pageIndex,HttpClient httpClient) {
	   String name=site.getName();	   
	   Regular updatePageRegular=regularDao.get(site.getId(),Constants.BOOK_UPDATE_PAGE_CRAWL_TYPE); 
	   UpdatePageRegex updatePageRegex=JsonUtil.Json2UpdatePageRegex(updatePageRegular.getRegex());
	   String updatePageUrl=entry.getUrl().replaceAll("\\[\\[Num\\]\\]",(pageIndex+1)+"");
	   
	   String updatePageContent=HttpCrawler.crawl(site.getName()+": book update page",httpClient, updatePageUrl, updatePageRegular.getCharset());
	  
	   if(updatePageContent==null||updatePageContent.equals(Constants.SERVER_BUSY)
	       ||updatePageContent.equals(Constants.NOT_FOUNDSTR)||updatePageContent.equals("302")) {
		   LOG.error("[Website "+name+"] Crawl the book update page "+updatePageUrl+" failed.");
		   SpiderException downLoadException=new SpiderException(site.getId(),-1L,updatePageUrl,Constants.UPDATE_NETWORK_ERR);
		   downLoadException.saveException();
		   return null;
	   }
	   
	   Pattern p=Pattern.compile(updatePageRegex.getMainHtml());
	   Matcher m=p.matcher(updatePageContent);
	   String mainUpdatePageContent=null;
	   List<Book> books=new ArrayList<Book>();
	   if(m.find()) {
		   mainUpdatePageContent=m.group(1).trim();
	   } else {
		   LOG.error("[Website "+name+"] the regular can not match mainUpdatePageContent.");
		   SpiderException updateRegexException=new SpiderException(site.getId(),-1L,updatePageUrl,Constants.PAGE_REGEX_ERR);
		   updateRegexException.saveException();
		   return books;
	   }
	   p=Pattern.compile(updatePageRegex.getBookBasicInfo());
	   m=p.matcher(mainUpdatePageContent);
	   int errorCount=0;
	   while(m.find()) {
		   Book book=new Book();
		   book.setSiteId(site.getId());
		   //crawl bookName
		   if(!bookNameCrawl(name,m,updatePageRegex,book,updatePageUrl)) {
			   errorCount++;
			   continue;
		   }
		   //crawl author
		   if(!authorNameCrawl(name,m,updatePageRegex,book,updatePageUrl)) {
			   continue;
		   }
		   //crawl bookId
		   if(!bookIdCrawl(name,m,updatePageRegex,book,updatePageUrl)) {
			   continue;
		   }
		   //crawl newChapterName
		   if(!newChapterNameCrawl(name,m,updatePageRegex,book,updatePageUrl)) {
			   continue;
		   }
		   //crawl newChapterUrl
		   if(!newChapterUrlCrawl(name,m,updatePageRegex,book,site,updatePageUrl)) {
			   continue;
		   }
		   //crawl updateTime
		   if(!updateTimeCrawl(name,m,updatePageRegex,book,updatePageUrl)) {
			   continue;
		   }
		   //crawl coverUrl
		   if(!coverUrlCrawl(name,m,updatePageRegex,book,site,updatePageUrl)) {
			   continue;
		   }
		   //crawl dirUrl
		   if(!dirUrlCrawl(name,m,updatePageRegex,book,site,updatePageUrl)) {
			   continue;
		   }
		   if(errorCount>10) {
			  LOG.error("[Website "+name+"] your book update page regular is not correct because of website changing.");
			  SpiderException updateRegexException=new SpiderException(site.getId(),-1L,updatePageUrl,Constants.PAGE_REGEX_ERR);
			  updateRegexException.saveException();
			  break; 
		   }
		   books.add(book);
	   }
	   if(books.size()<10) {
		   LOG.error("[Website "+name+"] your book update page regular is not correct because of website changing.");
		   SpiderException updateRegexException=new SpiderException(site.getId(),-1L,updatePageUrl,Constants.PAGE_REGEX_ERR);
		   updateRegexException.saveException();
	   }
	   return books;
	}
    
	public boolean bookNameCrawl(String name,Matcher m,UpdatePageRegex updatePageRegex,Book book,String updatePageUrl) {
		  String bookName=m.group(updatePageRegex.getBookNameIndex());
		  bookName=bookName == null ? "" : bookName.trim();
		  bookName=bookName.trim();
		  if (bookName.equals("")) {
			  LOG.warn("[Website "+name+"] one book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" that has no bookName in book Update page.");
		      return false; 
		  } else if (bookName.length() > 60) {
			  LOG.warn("[Website "+name+"] the book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" is to long bookName!(>60)");
		      return false;
		  }
		   book.setName(bookName.replaceAll("&nbsp;", " "));
		   return true;
	}
	
	public boolean authorNameCrawl(String name,Matcher m,UpdatePageRegex updatePageRegex,Book book,String updatePageUrl) {
		   String authorName=m.group(updatePageRegex.getAuthorNameIndex());
		   authorName=authorName == null ? "" : authorName.trim();
		   if(authorName.equals("")) {
			  LOG.warn("[Website "+name+"] one book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" that has no authorName in book Update page.");
			  book.setAuthor("");
			  return true; 
		   } else if(authorName.length() > 60) {
			  LOG.warn("[Website "+name+"] the book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" is to long authorName!"+authorName+"(>60)"); 
		      return false;
		   }
		   book.setAuthor(authorName.replaceAll("&nbsp;", " "));
		   return true;
	}
	
	public boolean bookIdCrawl(String name,Matcher m,UpdatePageRegex updatePageRegex,Book book,String updatePageUrl) {
		  String bookId=m.group(updatePageRegex.getBookIdIndex());
		  bookId=bookId == null ? "" : bookId.trim();
		  if(bookId.equals("")) {
			  LOG.warn("[Website "+name+"] one book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" that has no bookId in book Update page.");
			  return false; 
		  } 
		  try {
		    book.setBookId(Long.parseLong(bookId));
		  } catch(Exception e) {
			LOG.error("[Website "+name+"] bookId in book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" Update page is wrong.");
			return false;
		  }
		  return true;
	}
	
	public boolean newChapterNameCrawl(String name,Matcher m,UpdatePageRegex updatePageRegex,Book book,String updatePageUrl) {
		  String newChapterName=m.group(updatePageRegex.getNewChapterNameIndex());
		  newChapterName=newChapterName == null ? "" : newChapterName.trim();
		  if(newChapterName.equals("")) {
			  LOG.warn("[Website "+name+"] one book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" that has no newChapterName in book Update page."); 
			  return false;
		  } else if(newChapterName.length() > 200) {
			  LOG.warn("[Website "+name+"] the book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" is to long newChapterName!"+newChapterName+"(>200)");
		      return false;
		  }
		  book.setNewChapterName(newChapterName.replaceAll("&nbsp;", " "));
		  return true;
	}
	
	public boolean newChapterUrlCrawl(String name,Matcher m,UpdatePageRegex updatePageRegex,Book book,Site site,String updatePageUrl) {
		 int newChapterUrlIndex=updatePageRegex.getNewChapterUrlIndex();
		 if(newChapterUrlIndex==-1) {
			book.setNewChapterUrl(null);
			return true; 
		 }
		 String newChapterUrl=m.group(newChapterUrlIndex);
		 newChapterUrl=newChapterUrl == null ? "" : newChapterUrl.trim();
		 if(newChapterUrl.equals("")) {
			  LOG.warn("[Website "+name+"] one book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" that has no newChapterUrl in book Update page.");
			  return false;
		 }
		 if(newChapterUrl.indexOf("/") == 0) {
			 newChapterUrl=site.getUrl()+newChapterUrl;
		 }
		 if(newChapterUrl.length() > 200) {
			 LOG.warn("[Website "+name+"] the book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" is to long newChapterUrl!"+newChapterUrl+"(>200)");
			 return false;
		 }
		 //validate
		 Pattern p=Pattern.compile(updatePageRegex.getNewChapterUrlValidate());
		 Matcher newM=p.matcher(newChapterUrl);
		 if(newM.find()) {
			 String bookIdValidate=newM.group(1);
			 if(bookIdValidate==null||!bookIdValidate.equals(book.getBookId()+"")) {
				 LOG.warn("[Website "+name+"] the book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" that has wrong BookId in newChapterUrl"); 
				 return false;
			 } 
		 } else {
			 LOG.warn("[Website "+name+"] the book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" that has no BookId in newChapterUrl");
			 return false;
		 }
		 book.setNewChapterUrl(newChapterUrl);
		 return true;
	}
	
	public boolean updateTimeCrawl(String name,Matcher m,UpdatePageRegex updatePageRegex,Book book,String updatePageUrl) {
		String updateTimeStr=m.group(updatePageRegex.getNewChapterUpdateTimeIndex());
		if(updateTimeStr==null||updateTimeStr.equals("")) {
			LOG.warn("[Website "+name+"] the book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" that has wrong updateTime.");
			return false;
		}
		Calendar nowC=Calendar.getInstance();
		SimpleDateFormat sdf=new SimpleDateFormat(updatePageRegex.getUpdateTimeFormat());
		Date updateTime=null;
		try {
			updateTime=sdf.parse(updateTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		nowC.setTime(updateTime);
		if(nowC.get(Calendar.YEAR)==1970) {
		   nowC.set(Calendar.YEAR,Calendar.getInstance().get(Calendar.YEAR));
		}
		if(nowC.getTime().after(new Date())) {
		   nowC.set(Calendar.YEAR,Calendar.getInstance().get(Calendar.YEAR)-1);
		}
		book.setWebUpdateTime(nowC.getTime());
		return true;
	}
	
	public boolean coverUrlCrawl(String name,Matcher m,UpdatePageRegex updatePageRegex,Book book,Site site,String updatePageUrl) {
		int index = updatePageRegex.getBookIndexUrlIndex();
		String Url="";
		if(index>0) {
			Url=m.group(index);
			if(Url!=null&&Url.indexOf("/") == 0) {
			   Url=site.getUrl()+Url;
			}
		} else {
			if(updatePageRegex.getBookIndexUrlReplaceType()==1) {
				Url = updatePageRegex.getBookIndexUrlTemplate().replace(
				           updatePageRegex.getBookIndexUrlReplaceStr(),book.getBookId()+"");
			} else if(updatePageRegex.getBookIndexUrlReplaceType()==2) {
				String bookIdStr=book.getBookId()+"";
				if (bookIdStr.length() <= 3) {
					Url = "0/" + bookIdStr;
				} else {
					Url = bookIdStr.substring(0,bookIdStr.length()-3)+"/"+bookIdStr;
				}
				Url=updatePageRegex.getBookIndexUrlTemplate().replace(
						updatePageRegex.getBookIndexUrlReplaceStr(),Url);
			}
		}
		//validate
		if(Url!=null&&!Url.trim().equals("")) {
			  Pattern p=Pattern.compile(updatePageRegex.getBookIndexUrlValidate());
			  Matcher newM=p.matcher(Url);
			  if(newM.find()) {
				  String bookIdValidate=newM.group(1);
				  if(bookIdValidate==null||!bookIdValidate.equals(book.getBookId()+"")) {
						 LOG.warn("[Website "+name+"] the book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" that has wrong BookId in coverUrl"); 
						 return false;
				  } 
				  book.setUrl(Url);
				  return true;
			  } else {
				  LOG.warn("[Website "+name+"] the book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" that has no BookId in coverUrl");
				  return false;
			  }
		}
		LOG.warn("[Website "+name+"] the book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" that has no coverUrl");
		return false;
	}
	
	public boolean dirUrlCrawl(String name,Matcher m,UpdatePageRegex updatePageRegex,Book book,Site site,String updatePageUrl) {
		int index = updatePageRegex.getBookDirUrlIndex();
		String dirUrl="";
		if(index>0) {
			dirUrl=m.group(updatePageRegex.getBookDirUrlIndex());
			if(dirUrl!=null&&dirUrl.indexOf("/") == 0) {
			   dirUrl=site.getUrl()+dirUrl;
		    }
		} else {
			if(updatePageRegex.getDirUrlReplaceType()==1) {
				dirUrl=updatePageRegex.getDirUrlTemplate().replace(
				           updatePageRegex.getDirUrlReplaceStr(),book.getBookId()+"");
			} else if(updatePageRegex.getDirUrlReplaceType()==2) {
				String bookIdStr=book.getBookId()+"";
				if (bookIdStr.length() <= 3) {
					dirUrl = "0/" + bookIdStr;
				} else {
					dirUrl = bookIdStr.substring(0,bookIdStr.length()-3)+"/"+bookIdStr;
				}
				dirUrl=updatePageRegex.getDirUrlTemplate().replace(
						updatePageRegex.getDirUrlReplaceStr(),dirUrl);
			}
		}
		//validate
		if(dirUrl!=null&&!dirUrl.trim().equals("")) {
			Pattern p=Pattern.compile(updatePageRegex.getDirUrlValidate());
			Matcher newM=p.matcher(dirUrl);
			if(newM.find()) {
			   String bookIdValidate=newM.group(1);
			   if(bookIdValidate==null||!bookIdValidate.equals(book.getBookId()+"")) {
					 LOG.warn("[Website "+name+"] the book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" that has wrong BookId in dirUrl"); 
					 return false;
			   } 
			   book.setDirUrl(dirUrl);
			   return true;
			} else {
				LOG.warn("[Website "+name+"] the book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" that has no BookId in dirUrl");
				return false;
			}
		}
		LOG.warn("[Website "+name+"] the book with name <<"+book.getName()+">> and updatePageUrl="+updatePageUrl+" that has no dirUrl");
		return false;
	}
	
	@Autowired 
	public void setRegularDao(RegularDao regularDao) {
		this.regularDao = regularDao;
	}
    
	@Autowired 
	public void setEntryDao(EntryDao entryDao) {
		this.entryDao = entryDao;
	}
    
	@Autowired 
	public void setSiteDao(SiteDao siteDao) {
		this.siteDao = siteDao;
	}
	
	public static void main(String args[]) {
		UpdatePageService updatePageService=(UpdatePageService)SpringIoCUtil.getBean("updatePageService");
		CoverPageService coverPageService=(CoverPageService)SpringIoCUtil.getBean("coverPageService");
		HttpClient httpclient=HttpCrawler.createMultiThreadClient(400, 80, 6000, 9000);
		SiteDao siteDao=(SiteDao)SpringIoCUtil.getBean("siteDao");
		Site site=siteDao.getSiteByName("121xs");
		EntryDao entryDao=(EntryDao)SpringIoCUtil.getBean("entryDao");
		Entry entry=entryDao.getBySiteId(site.getId());
		List<Book> books=updatePageService.updatePageCrawl(site,entry, 0, httpclient);
		BookDao bookDao=(BookDao)SpringIoCUtil.getBean("bookDao");
		long start=System.currentTimeMillis();
		for(int i=0;i<books.size();i++) {
			coverPageService.coverPageCrawl(new BooleanObject(),books.get(i), httpclient);
			//bookDao.insert(b);
		}
		bookDao.insertBacth(books,20);
		long interval=System.currentTimeMillis()-start;
		LOG.info("interval="+interval);
		httpclient.getConnectionManager().shutdown();
	}
	
}
