package com.pw.spider.Util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BooleanObject {
	
	public volatile AtomicInteger coverErrorCount=new AtomicInteger(0);
	
	public volatile AtomicInteger dirErrorCount=new AtomicInteger(0);
	
	public volatile AtomicBoolean coverBusy=new AtomicBoolean(false);
	
	//public volatile AtomicBoolean dirBusy=new AtomicBoolean(false);
	
	public volatile AtomicBoolean error=new AtomicBoolean(false);
	
	public volatile AtomicInteger continueCoverTimeout=new AtomicInteger(0);
	
	public volatile AtomicInteger continueDirTimeout=new AtomicInteger(0);
	
	public volatile AtomicBoolean coverTimeout=new AtomicBoolean(false);
	
	public volatile AtomicBoolean dirTimeout=new AtomicBoolean(false);
   
}
