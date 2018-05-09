package com.briup.web;
//测试 一对一的 级联查询

import org.junit.Before;
import org.junit.Test;

import com.briup.bean.Book;
import com.briup.bean.School;
import com.briup.dao.IBookDao;
import com.briup.dao.ISchoolDao;
import com.briup.dao.impl.BookDaoImpl;
import com.briup.dao.impl.SchoolDaoImpl;

public class TestDaoCascadeBookAndSchool {

	IBookDao bookDao ;
	ISchoolDao schoolDao;
	@Before
	public void before(){
		bookDao = new BookDaoImpl();
		schoolDao = new SchoolDaoImpl();
	}
	//成功
	@Test
	public void schoolCascadeBook() throws Exception{
		//得到的 对象 : PrimaryForeign [主表名:tbl_school, 主表主键名:id, 外键表名:tbl_book, 外键列名:book_id, 桥表信息: null]
		
		
		/*
		 select tbl_school.*
		 from tbl_school,tbl_book
		 where tbl_school.book_id = tbl_book.id
		 	and tbl_school.id = 45;
		 
		  */

		School school = schoolDao.get(3L, Book.class);
		System.out.println(school);
	}
	//成功
	@Test
	public void bookCascadeSchool()throws Exception{
		
		//PrimaryForeign [主表名:tbl_book, 主表外键列名:null, 引用表主键名:id, 引用表名:tbl_school, 桥表信息: null]
		
		/*
		 select tbl_book.*
		 from tbl_book,tbl_school
		 where tbl_book.id = tbl_school.book_id
		 and tbl_book.id = 46;
		 
		  */
		
		Book book = bookDao.get(99L, School.class);
		System.out.println(book);
	}
	
}
