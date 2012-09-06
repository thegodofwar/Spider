package com.pw.spider.dao;

import java.util.List;

import com.pw.spider.model.Chapter;

public interface ChapterDao {
    public long insert(Chapter chapter);
    
    public boolean insertBatch(List<Chapter> chapters,int batchNum);
    
    public Chapter getChapter(int siteId,long bookId, String name, String url);
    
    public boolean updateKV(int siteId,long id,long kv,int type);
    
    public boolean updateKVS(int siteId,long id,String kvs,int type);
    
    public long getKVCount(int siteId);
    
    public long getKVSCount(int siteId);
    
    public List<Chapter> getKVChapters(int siteId,long from,int limit);
    
    public List<Chapter> getKVSChapters(int siteId,long from,int limit);
    
    public boolean deleteChapters(int siteId,long bookId);
    
    public List<Chapter> getChaptersByBookId(int siteId,long bookId);
    
    public long getHotKVSCount(int siteId,int hot);
    
    public List<Chapter> getHotKVSChapters(int siteId,int hot,long from,int limit);
    
}
