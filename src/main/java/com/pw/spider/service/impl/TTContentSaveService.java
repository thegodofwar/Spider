package com.pw.spider.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.apache.log4j.Logger;

import com.pw.spider.Util.SpringIoCUtil;

public class TTContentSaveService {
	private static final Logger LOG = Logger.getLogger(TTContentSaveService.class);
	private MemcachedClient client = (MemcachedClient) SpringIoCUtil.getBean("xmemcachedClient");
	
	public boolean saveText(String key, String value){
		try {
			boolean ret = client.set(key, 0, value);
			return ret;
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			LOG.error("ttserver get timeout exception" + e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			LOG.error("ttserver get interrupted exception" + e);
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			LOG.error("ttserver get memcache exception" + e);
		}
		return false;
	}
	
	public boolean saveImage(String key, byte[] data){
		try {
			boolean ret = client.set(key, 0, data);
			return ret;
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			LOG.error("ttserver get timeout exception" + e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			LOG.error("ttserver get interrupted exception" + e);
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			LOG.error("ttserver get memcache exception" + e);
		}
		return false;
	}
}