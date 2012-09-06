package com.pw.spider.regex;

public class ContentPageRegex {
    private String contentRegex;
    
    private String imgTypeStartStr;//if content is word-type,then null
    
    private String imgContentRegex;
    
	public String getImgTypeStartStr() {
		return imgTypeStartStr;
	}

	public void setImgTypeStartStr(String imgTypeStartStr) {
		this.imgTypeStartStr = imgTypeStartStr;
	}

	public String getImgContentRegex() {
		return imgContentRegex;
	}

	public void setImgContentRegex(String imgContentRegex) {
		this.imgContentRegex = imgContentRegex;
	}

	public String getContentRegex() {
		return contentRegex;
	}

	public void setContentRegex(String contentRegex) {
		this.contentRegex = contentRegex;
	}
    
    
}
