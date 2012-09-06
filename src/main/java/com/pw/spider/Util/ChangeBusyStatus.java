package com.pw.spider.Util;

import java.util.concurrent.atomic.AtomicBoolean;

public class ChangeBusyStatus implements Runnable {
   private AtomicBoolean busy;
   
   public AtomicBoolean getBusy() {
	return busy;
   }

   public void run() {
	   busy.getAndSet(false);
   }
   
   public void setBusy(AtomicBoolean busy) {
		this.busy = busy;
   }
   
   public ChangeBusyStatus(AtomicBoolean busy) {
		this.busy=busy;
   }
   
}
