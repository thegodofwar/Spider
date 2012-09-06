package com.pw.spider.model;

import java.util.Date;

/** 
 * Correspond to book in DB
 * 
 * @author liufukun
 * 
 */
public class Book {
   
	private long id;
	
	private long bookId;
	
	private int siteId;
	
	private String name;
	
	private String author;
	
	private String url;
	
	private String dirUrl;
	
	private String coverUrl;
	
	private String brief;
	
	private String category;
	
	private String newChapterName;
	
	private String newChapterUrl;
	
	private Date updateTime;
	
	private Date webUpdateTime;//the time of book update in web site
	
	private Date createTime;//the time of first insert
	
	private int firstTag;//not store in DB,it just a tag
	
	private int hot;//0:not hot book,1:hot book
	
	public int getHot() {
		return hot;
	}

	public void setHot(int hot) {
		this.hot = hot;
	}

	public Date getWebUpdateTime() {
		return webUpdateTime;
	}

	public void setWebUpdateTime(Date webUpdateTime) {
		this.webUpdateTime = webUpdateTime;
	}
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public int getFirstTag() {
		return firstTag;
	}

	public void setFirstTag(int firstTag) {
		this.firstTag = firstTag;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getBookId() {
		return bookId;
	}

	public void setBookId(long bookId) {
		this.bookId = bookId;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDirUrl() {
		return dirUrl;
	}

	public void setDirUrl(String dirUrl) {
		this.dirUrl = dirUrl;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getNewChapterName() {
		return newChapterName;
	}

	public void setNewChapterName(String newChapterName) {
		this.newChapterName = newChapterName;
	}

	public String getNewChapterUrl() {
		return newChapterUrl;
	}

	public void setNewChapterUrl(String newChapterUrl) {
		this.newChapterUrl = newChapterUrl;
	}

	/*public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}*/
	
	
	
	
}