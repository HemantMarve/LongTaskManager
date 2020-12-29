package com.atlan.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CsvStructure {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id=0;
private String jobid;
String lastName,designation,contact,firstName,salary,city;

public long getId() {
	return id;
}

public void setId(long id) {
	this.id = id;
}

public CsvStructure(String jobid,String firstName, String lastName, String designation, String contact, String salary, String city) {
	this.jobid=jobid;
	this.firstName = firstName;
	this.lastName = lastName;
	this.designation = designation;
	this.contact = contact;
	this.salary = salary;
	this.city = city;
}

public String getJobid() {
	return jobid;
}

public void setJobid(String jobid) {
	this.jobid = jobid;
}

public CsvStructure() {
	// TODO Auto-generated constructor stub
}

public String getFirstName() {
	return firstName;
}

public void setFirstName(String firstName) {
	this.firstName = firstName;
}

public String getLastName() {
	return lastName;
}

public void setLastName(String lastName) {
	this.lastName = lastName;
}

public String getDesignation() {
	return designation;
}

public void setDesignation(String designation) {
	this.designation = designation;
}

public String getContact() {
	return contact;
}

public void setContact(String contact) {
	this.contact = contact;
}

public String getSalary() {
	return salary;
}

public void setSalary(String salary) {
	this.salary = salary;
}

public String getCity() {
	return city;
}

public void setCity(String city) {
	this.city = city;
}

}
