package com.pw.spider.regex;

public class DirPageRegex {
    private String mainTomeContent;// default index:1
    
    private String tomeName;// default index:1
    
    private String chapterInfo;
    
    private String chapterUrlValidate;// default index:1
    
    private int chapterUrlReplaceType;
    
	private String chapterUrlReplaceStr;
    
	private int chapterNameIndex;
    
    private int chapterUrlIndex;
    
    private int chapterIdIndex;
    
    public String getChapterUrlReplaceStr() {
		return chapterUrlReplaceStr;
	}

	public void setChapterUrlReplaceStr(String chapterUrlReplaceStr) {
		this.chapterUrlReplaceStr = chapterUrlReplaceStr;
	}
    
    public String getChapterUrlValidate() {
		return chapterUrlValidate;
	}

	public void setChapterUrlValidate(String chapterUrlValidate) {
		this.chapterUrlValidate = chapterUrlValidate;
	}

	public int getChapterUrlReplaceType() {
		return chapterUrlReplaceType;
	}

	public void setChapterUrlReplaceType(int chapterUrlReplaceType) {
		this.chapterUrlReplaceType = chapterUrlReplaceType;
	}
    
	public String getMainTomeContent() {
		return mainTomeContent;
	}

	public void setMainTomeContent(String mainTomeContent) {
		this.mainTomeContent = mainTomeContent;
	}

	public String getTomeName() {
		return tomeName;
	}

	public void setTomeName(String tomeName) {
		this.tomeName = tomeName;
	}

	public String getChapterInfo() {
		return chapterInfo;
	}

	public void setChapterInfo(String chapterInfo) {
		this.chapterInfo = chapterInfo;
	}

	public int getChapterNameIndex() {
		return chapterNameIndex;
	}

	public void setChapterNameIndex(int chapterNameIndex) {
		this.chapterNameIndex = chapterNameIndex;
	}

	public int getChapterUrlIndex() {
		return chapterUrlIndex;
	}

	public void setChapterUrlIndex(int chapterUrlIndex) {
		this.chapterUrlIndex = chapterUrlIndex;
	}

	public int getChapterIdIndex() {
		return chapterIdIndex;
	}

	public void setChapterIdIndex(int chapterIdIndex) {
		this.chapterIdIndex = chapterIdIndex;
	}
}
