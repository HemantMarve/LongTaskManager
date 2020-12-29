package com.atlan.entity;

public class JobErrorResponse {
	private int status;
	private String message;
	private long date;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public JobErrorResponse(int status, String message, long date) {
		
		this.status = status;
		this.message = message;
		this.date = date;
	}
}
