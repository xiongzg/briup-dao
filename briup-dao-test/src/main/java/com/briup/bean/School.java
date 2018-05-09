package com.briup.bean;

import com.briup.base.jdbc.annocation.OneToOne;
import com.briup.base.jdbc.annocation.TableName;
import com.briup.base.jdbc.bean.Pojo;

@TableName(name="tbl_school")
public class School extends Pojo{
	
	private Long id;
	
	private String name;
	@OneToOne(foreignKeyColumn="book_id",reference=Book.class)
	private Book book;
	
	
	@Override
	public String toString() {
		return "School [id=" + id + ", name=" + name + ", book=" + book + "]";
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
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
}
