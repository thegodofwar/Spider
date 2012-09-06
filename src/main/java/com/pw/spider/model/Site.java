package com.pw.spider.model;

public class Site {
   
	private int id;
	
	private String name;
	
	private String url;
	
	private String charset;
	
	private int type;
	
	private int weight;
	
	private int useProxy;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getUseProxy() {
		return useProxy;
	}

	public void setUseProxy(int useProxy) {
		this.useProxy = useProxy;
	}
	
}
