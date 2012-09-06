package com.pw.spider.dao;

import java.util.Date;
import java.util.List;

import com.pw.spider.model.Book;

public interface BookDao {
    public long insert(Book book);
    
    public boolean insertBacth(List<Book> books,int batchNum);
    
    public List<Book> getBookLimit(long from,long limit);
    
    public Book getBySiteId_Name_Author(int siteid,String bookName,String authorName);
    
    public Book getById(long id);
    
    public boolean updateCover(long id,String brief,String coverUrl);
    
    public boolean updateLatestChapter(long id,String newChapterName,String newChapterUrl,Date webUpdateTime);
    
    public Date getMaxWebUpdateTime(int siteId);
    
    public List<Book> getAllHotBooks(int siteId);
    
}
