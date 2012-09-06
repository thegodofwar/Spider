package com.pw.spider.Util;

import java.io.IOException;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpCrawler {
	
	public static final Logger LOG=Logger.getLogger(HttpCrawler.class.getName());
	
    public static String crawl(String name,HttpClient httpclient, String url,String encoding) {
    	String webPageContent=null;
    	HttpGet httpget = new HttpGet(url);
		httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/534.3 (KHTML, like Gecko) Chrome/6.0.472.63 Safari/534.3"); 
		HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception,int executionCount,HttpContext context) {
			  if (executionCount >= Integer.parseInt(XMLUtil.loadValueByKey("retry"))) {
					// Do not retry if over max retry count
					return false;
				}
				if (exception instanceof NoHttpResponseException) {
					// Retry if the server dropped connection on us
					return true;
				}
				if (exception instanceof SSLHandshakeException) {
					// Do not retry on SSL handshake exception
					return false;
				}
				HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
				if (idempotent) {
					// Retry if the request is considered idempotent
					return true;
				}
				return false;
			}
		};
		((DefaultHttpClient)httpclient).setHttpRequestRetryHandler(myRetryHandler);
		HttpEntity entity =null;
		try {
			HttpResponse response = httpclient.execute(httpget);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				entity = response.getEntity();
				if (entity != null) {
					webPageContent = EntityUtils.toString(entity, encoding);
					LOG.info("[Website "+name+"] download url " + url + " success");
					EntityUtils.consume(entity);
					httpget.abort();
					return webPageContent;
				}
			}
			if(code!=302) {//in order to avoid printing to much 302 error in log
			  LOG.warn("[Website "+name+"] try download failed, url="+ url +", status=" +code);
			}
			if(entity!=null) {
			   EntityUtils.consume(entity);
			}
			httpget.abort();
			if(code==500) {
			  return Constants.SERVER_BUSY;
			} else if(code==404) {
			  return Constants.NOT_FOUNDSTR;
			} else if(code==302) {
		      return "302";
			} else {
			  return null;
			}
		} catch (Exception e) {
			LOG.warn("[Website "+name+"] try download failed " + url);
			if(entity!=null) {
			  try {
				EntityUtils.consume(entity);
			  } catch (IOException e1) {
				LOG.warn("",e);
			  }
			}
			httpget.abort();
		} 
		LOG.warn("[Website "+name+"] failed to download " + url);
		return webPageContent;
    }
    
    public static byte[] crawlPic(String name,HttpClient httpclient, String url,String chapterUrl) {
    	byte[] bytes=null;
    	HttpGet httpget = new HttpGet(url);
    	httpget.setHeader("Referer",chapterUrl);
		httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/534.3 (KHTML, like Gecko) Chrome/6.0.472.63 Safari/534.3"); 
		HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception,int executionCount,HttpContext context) {
			  if (executionCount >= Integer.parseInt(XMLUtil.loadValueByKey("retry"))) {
					// Do not retry if over max retry count
					return false;
				}
				if (exception instanceof NoHttpResponseException) {
					// Retry if the server dropped connection on us
					return true;
				}
				if (exception instanceof SSLHandshakeException) {
					// Do not retry on SSL handshake exception
					return false;
				}
				HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
				if (idempotent) {
					// Retry if the request is considered idempotent
					return true;
				}
				return false;
			}
		};
		((DefaultHttpClient)httpclient).setHttpRequestRetryHandler(myRetryHandler);
		HttpEntity entity =null;
		try {
			HttpResponse response = httpclient.execute(httpget);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				entity = response.getEntity();
				if (entity != null) {
					bytes=EntityUtils.toByteArray(entity);
					LOG.info("[Website "+name+"] download url " + url + " success");
					EntityUtils.consume(entity);
					httpget.abort();
					return bytes;
				}
			}
			LOG.error("[Website "+name+"] try download failed, url="+ url +", status=" +code+" Referer="+chapterUrl);
			if(entity!=null) {
			   EntityUtils.consume(entity);
			}
			httpget.abort();
		} catch (Exception e) {
			LOG.error("[Website "+name+"] try download failed " + url,e);
			if(entity!=null) {
			  try {
				EntityUtils.consume(entity);
			  } catch (IOException e1) {
				LOG.error("",e);
			  }
			}
			httpget.abort();
		} 
		LOG.error("[Website "+name+"] failed to download " + url);
		return bytes;
    }
    
    public static HttpClient createMultiThreadClient(int maxCon, int maxConPerRoute){
    	SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(
		         new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(schemeRegistry);
		cm.setMaxTotal(maxCon);
		cm.setDefaultMaxPerRoute(maxConPerRoute);
		HttpHost localhost = new HttpHost("locahost", 80);
		cm.setMaxForRoute(new HttpRoute(localhost), 100);
		HttpClient client = new DefaultHttpClient(cm);
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 6*1000);
		HttpConnectionParams.setSoTimeout(params, 10*1000);
		HttpClientParams.setRedirecting(params, false);
		return client;
    }
    
    public static HttpClient createMultiThreadClient(int maxCon, int maxConPerRoute,int connectionTimeout,int soTimeout){
    	SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(
		         new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(schemeRegistry);
		cm.setMaxTotal(maxCon);
		cm.setDefaultMaxPerRoute(maxConPerRoute);
		HttpHost localhost = new HttpHost("locahost", 80);
		cm.setMaxForRoute(new HttpRoute(localhost), 100);
		HttpClient client = new DefaultHttpClient(cm);
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
		HttpConnectionParams.setSoTimeout(params, soTimeout);
		HttpClientParams.setRedirecting(params, false);
		return client;
    } 
    
    public static HttpClient createRedirectMultiThreadClient(int maxCon, int maxConPerRoute,int connectionTimeout,int soTimeout){
    	SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(
		         new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(schemeRegistry);
		cm.setMaxTotal(maxCon);
		cm.setDefaultMaxPerRoute(maxConPerRoute);
		HttpHost localhost = new HttpHost("locahost", 80);
		cm.setMaxForRoute(new HttpRoute(localhost), 100);
		HttpClient client = new DefaultHttpClient(cm);
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
		HttpConnectionParams.setSoTimeout(params, soTimeout);
		HttpClientParams.setRedirecting(params, true);
		return client;
    } 
    
    public static void main(String args[]) {
    	String dUrl=args[0];
    	String enCo=args[1];
    	HttpClient httpclient=HttpCrawler.createMultiThreadClient(400,80,6000,9000);//http://www.121xs.com/html/6/6425/1883969.html
        String str=HttpCrawler.crawl("", httpclient, dUrl, enCo);
        LOG.info(str);
        // String strContent=HttpCrawler.crawl("121xs", httpclient, "http://www.121xs.com/html/6/6425/1884067.html", "gbk");//http://www.121xs.com/html/6/6151/1770835.html
       // System.out.println(strContent);
    	/*byte[] bytes1=HttpCrawler.crawlPic("121xs download content image",httpclient,"http://www.121xs.com/files/article/attachment/6/6151/1770835/282746.gif");
    	byte[] bytes2=HttpCrawler.crawlPic("121xs download content image",httpclient,"http://www.121xs.com/files/article/attachment/6/6151/1770835/282747.gif");
    	byte[] bytes=new byte[bytes1.length+bytes2.length];
    	System.arraycopy(bytes1,0,bytes,0,bytes1.length);
    	System.arraycopy(bytes2,0,bytes,bytes1.length,bytes2.length);
    	String imagePath = "D:\\image\\0.gif";
    	FileImageOutputStream imageOutput=null;
		try {
			imageOutput = new FileImageOutputStream(new File(imagePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	if(bytes==null||bytes.length==0) {
    		System.out.println("the byte array is empty!");
    	}
    	try {
			imageOutput.write(bytes, 0, bytes.length);
			imageOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
    }
}
