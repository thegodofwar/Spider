package com.pw.spider.model;

public class Tome {
	private long id;

	private long bookId;

	private String name;

	private int tomeOrder;

	private int tomeMatchStart;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBookId() {
		return bookId;
	}

	public void setBookId(long bookId) {
		this.bookId = bookId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTomeOrder() {
		return tomeOrder;
	}

	public void setTomeOrder(int tomeOrder) {
		this.tomeOrder = tomeOrder;
	}

	public int getTomeMatchStart() {
		return tomeMatchStart;
	}

	public void setTomeMatchStart(int tomeMatchStart) {
		this.tomeMatchStart = tomeMatchStart;
	}
}
