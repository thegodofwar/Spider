package com.pw.spider.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.log4j.Logger;


public class XMLUtil {
	public static final Logger LOG=Logger.getLogger(XMLUtil.class.getName());
	private static Properties prop = null;
	static {
		LOG.info(XMLUtil.class.getResource("/" + Constants.CONFIG).getPath());
	    String realPath = XMLUtil.class.getResource("/" + Constants.CONFIG).getPath();
	    try {
			InputStream in = new FileInputStream(new File(realPath));
			prop = new Properties();
			prop.loadFromXML(in);
		} catch (FileNotFoundException e) {
			LOG.error("the config file not exists",e);
			prop = null;
		}catch (InvalidPropertiesFormatException e) {
			LOG.error("",e);
			prop = null;
		} catch (IOException e) {
			LOG.error("",e);
			prop = null;
		}
	}
	public static String loadValueByKey(String key) {
		if(prop == null)
			return null;
		
		String value = prop.getProperty(key, "");
		return value; 
	}
	
	public static void main(String args[]) {
		LOG.info(loadValueByKey("retry"));
	}
}
