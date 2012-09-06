package com.pw.spider.Util;

import com.pw.spider.dao.SiteDao;

public class TestSpring {
   
	public static void main(String args[]) {
		SiteDao siteDao1=(SiteDao)SpringIoCUtil.getBean("siteDao");
		SiteDao siteDao2=(SiteDao)SpringIoCUtil.getBean("siteDao");
		System.out.println(siteDao1==siteDao2);
	}
	
}
