package com.briup.bean;

import java.util.Date;

import com.briup.base.jdbc.annocation.TableName;
import com.briup.base.jdbc.annocation.TempField;
import com.briup.base.jdbc.bean.Pojo;

/**
 
  drop table book;
  create table book( 
  	id number, 
  	name varchar2(20), 
  	price  number, 
  	publish varchar2(20) );
  	
  	alter table tbl_book 
  	add(dob date);
  	
  	alter table book 
  	add(num number);
 * 
 */
@TableName(name = "tbl_book")
public class Book extends Pojo {
	private Long id;
	private String name;
	private Double price;
	private String publishAddress;
	private Date dob;
	private Integer num;
	@TempField
	private School school;
	 
	public School getSchool() {
		return school;
	}
	public void setSchool(School school) {
		this.school = school;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getPublishAddress() {
		return publishAddress;
	}
	public void setPublishAddress(String publishAddress) {
		this.publishAddress = publishAddress;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	
	@Override
	public String toString() {
		return "Book [id=" + id + ", name=" + name + ", price=" + price + ", publishAddress=" + publishAddress
				+ ", dob=" + dob + ", num=" + num + ", school=" + school + "]";
	}
	public Book() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Book(String name, Double price, String publishAddress, Date dob, Integer num) {
		super();
		this.name = name;
		this.price = price;
		this.publishAddress = publishAddress;
		this.dob = dob;
		this.num = num;
	}
	public Book(String name, Double price, String publishAddress) {
		super();
		this.name = name;
		this.price = price;
		this.publishAddress = publishAddress;
	}
	
}
