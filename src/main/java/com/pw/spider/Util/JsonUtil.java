package com.pw.spider.Util;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.pw.spider.regex.ContentPageRegex;
import com.pw.spider.regex.CoverPageRegex;
import com.pw.spider.regex.DirPageRegex;
import com.pw.spider.regex.UpdatePageRegex;

public class JsonUtil {
	
	public static final Logger LOG=Logger.getLogger(JsonUtil.class.getName());
	
	public static String UpdatePageRegex2Json(UpdatePageRegex updatePageRegex) {
		return JSON.toJSONString(updatePageRegex);
	}
	
	public static UpdatePageRegex Json2UpdatePageRegex(String jsonStr) {
		return JSON.parseObject(jsonStr, UpdatePageRegex.class);
	}
	
	public static String CoverPage2Json(CoverPageRegex coverPageRegex) {
		return JSON.toJSONString(coverPageRegex);
	}
	
	public static CoverPageRegex Json2CoverPageRegex(String jsonStr) {
		return JSON.parseObject(jsonStr, CoverPageRegex.class);
	}
	
	public static String DirPageRegex2Json(DirPageRegex dirPageRegex) {
		return JSON.toJSONString(dirPageRegex);
	}
	
	public static DirPageRegex Json2DirPageRegex(String jsonStr) {
		return JSON.parseObject(jsonStr,DirPageRegex.class);
	}
	
	public static String ContentPageRegex2Json(ContentPageRegex contentPageRegex) {
		return JSON.toJSONString(contentPageRegex);
	}
	
	public static ContentPageRegex Json2ContentPageRegex(String jsonStr) {
		return JSON.parseObject(jsonStr,ContentPageRegex.class);
	}
	
	public static void main(String args[]) {
		
	     
	}
}

