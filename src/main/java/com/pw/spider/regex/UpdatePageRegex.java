package com.pw.spider.regex;

public class UpdatePageRegex {
	private String mainHtml; // default index:1
	
	private String bookBasicInfo;
	
	private String newChapterUrlValidate;// default index:1
	
	private String bookIndexUrlValidate;// default index:1
	
	private String dirUrlValidate;// default index:1
	
	private String updateTimeFormat;
	
	private int bookIndexUrlReplaceType;
	
	private String bookIndexUrlTemplate;
	
	private String bookIndexUrlReplaceStr;
	
	private int dirUrlReplaceType;
	
	private String dirUrlTemplate;
	
	private String dirUrlReplaceStr;

	private int bookNameIndex;
    
    private int authorNameIndex;
    
    private int categoryIndex;
    
    private int bookIdIndex;
    
    private int newChapterNameIndex;
    
    private int newChapterUrlIndex;
    
    private int newChapterUpdateTimeIndex;
    
    private int bookIndexUrlIndex;
    
    private int bookDirUrlIndex;
    
    public String getDirUrlTemplate() {
		return dirUrlTemplate;
	}

	public void setDirUrlTemplate(String dirUrlTemplate) {
		this.dirUrlTemplate = dirUrlTemplate;
	}

	public String getDirUrlReplaceStr() {
		return dirUrlReplaceStr;
	}

	public void setDirUrlReplaceStr(String dirUrlReplaceStr) {
		this.dirUrlReplaceStr = dirUrlReplaceStr;
	}
    
    public int getDirUrlReplaceType() {
		return dirUrlReplaceType;
	}

	public void setDirUrlReplaceType(int dirUrlReplaceType) {
		this.dirUrlReplaceType = dirUrlReplaceType;
	}
    
    public String getBookIndexUrlReplaceStr() {
		return bookIndexUrlReplaceStr;
	}

	public void setBookIndexUrlReplaceStr(String bookIndexUrlReplaceStr) {
		this.bookIndexUrlReplaceStr = bookIndexUrlReplaceStr;
	}
    
    public String getBookIndexUrlTemplate() {
		return bookIndexUrlTemplate;
	}

	public void setBookIndexUrlTemplate(String bookIndexUrlTemplate) {
		this.bookIndexUrlTemplate = bookIndexUrlTemplate;
	}
    
    public int getBookIndexUrlReplaceType() {
		return bookIndexUrlReplaceType;
	}

	public void setBookIndexUrlReplaceType(int bookIndexUrlReplaceType) {
		this.bookIndexUrlReplaceType = bookIndexUrlReplaceType;
	}
    
    public String getDirUrlValidate() {
		return dirUrlValidate;
	}

	public void setDirUrlValidate(String dirUrlValidate) {
		this.dirUrlValidate = dirUrlValidate;
	}
    
    public String getBookIndexUrlValidate() {
		return bookIndexUrlValidate;
	}

	public void setBookIndexUrlValidate(String bookIndexUrlValidate) {
		this.bookIndexUrlValidate = bookIndexUrlValidate;
	}
    
    public String getUpdateTimeFormat() {
		return updateTimeFormat;
	}

	public void setUpdateTimeFormat(String updateTimeFormat) {
		this.updateTimeFormat = updateTimeFormat;
	}
    
    public String getNewChapterUrlValidate() {
		return newChapterUrlValidate;
	}

	public void setNewChapterUrlValidate(String newChapterUrlValidate) {
		this.newChapterUrlValidate = newChapterUrlValidate;
	}
    
    public String getMainHtml() {
		return mainHtml;
	}

	public void setMainHtml(String mainHtml) {
		this.mainHtml = mainHtml;
	}
	
	public String getBookBasicInfo() {
		return bookBasicInfo;
	}

	public void setBookBasicInfo(String bookBasicInfo) {
		this.bookBasicInfo = bookBasicInfo;
	}
	
	public int getBookNameIndex() {
		return bookNameIndex;
	}

	public void setBookNameIndex(int bookNameIndex) {
		this.bookNameIndex = bookNameIndex;
	}

	public int getAuthorNameIndex() {
		return authorNameIndex;
	}

	public void setAuthorNameIndex(int authorNameIndex) {
		this.authorNameIndex = authorNameIndex;
	}

	public int getCategoryIndex() {
		return categoryIndex;
	}

	public void setCategoryIndex(int categoryIndex) {
		this.categoryIndex = categoryIndex;
	}

	public int getBookIdIndex() {
		return bookIdIndex;
	}

	public void setBookIdIndex(int bookIdIndex) {
		this.bookIdIndex = bookIdIndex;
	}

	public int getNewChapterNameIndex() {
		return newChapterNameIndex;
	}

	public void setNewChapterNameIndex(int newChapterNameIndex) {
		this.newChapterNameIndex = newChapterNameIndex;
	}

	public int getNewChapterUrlIndex() {
		return newChapterUrlIndex;
	}

	public void setNewChapterUrlIndex(int newChapterUrlIndex) {
		this.newChapterUrlIndex = newChapterUrlIndex;
	}

	public int getNewChapterUpdateTimeIndex() {
		return newChapterUpdateTimeIndex;
	}

	public void setNewChapterUpdateTimeIndex(int newChapterUpdateTimeIndex) {
		this.newChapterUpdateTimeIndex = newChapterUpdateTimeIndex;
	}

	public int getBookIndexUrlIndex() {
		return bookIndexUrlIndex;
	}

	public void setBookIndexUrlIndex(int bookIndexUrlIndex) {
		this.bookIndexUrlIndex = bookIndexUrlIndex;
	}

	public int getBookDirUrlIndex() {
		return bookDirUrlIndex;
	}

	public void setBookDirUrlIndex(int bookDirUrlIndex) {
		this.bookDirUrlIndex = bookDirUrlIndex;
	}
    
}
