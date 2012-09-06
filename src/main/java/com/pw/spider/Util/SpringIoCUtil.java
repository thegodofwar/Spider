package com.pw.spider.Util;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringIoCUtil implements ApplicationContextAware{
	private static final Logger LOG = Logger.getLogger(SpringIoCUtil.class.getName());
	private static volatile ApplicationContext context = null;
	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		// TODO Auto-generated method stub
		LOG.info("set application context");
		context = arg0;
	}
	
	public static Object getBean(String name) {
		if(context == null){
			synchronized(SpringIoCUtil.class){
				if(context == null)
					new ClassPathXmlApplicationContext("spring-beans.xml");
			}
		}
		if(context == null)
			return null;
		return context.getBean(name);
	}

}
