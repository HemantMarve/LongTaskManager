package com.atlan.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Job {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
  long id= 0;
  String status,filename;
  long records;
public long getRecords() {
	return records;
}
public void setRecords(long records) {
	this.records = records;
}
public Job() {}
public long getId() {
	return id;
}

public void setId(long id) {
	this.id = id;
}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}

public String getFilename() {
	return filename;
}

public void setFilename(String filename) {
	this.filename = filename;
}

public Job(long id, String status, String filename) {
	
	this.id = id;
	this.status = status;
	this.filename = filename;
}
  
}
