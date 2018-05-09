package com.briup.web;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.briup.base.jdbc.bean.SimplePage;
import com.briup.base.jdbc.bean.Where;
import com.briup.base.jdbc.util.Condition;
import com.briup.bean.Book;
import com.briup.dao.IBookDao;
import com.briup.dao.ISchoolDao;
import com.briup.dao.impl.BookDaoImpl;
import com.briup.dao.impl.SchoolDaoImpl;

public class TestPage {
	IBookDao bookDao ;
	ISchoolDao schoolDao;
	@Before
	public void before(){
		bookDao = new BookDaoImpl();
		schoolDao = new SchoolDaoImpl();
	}
	/*
	 
	 select id as id,name as name,price as price,publish_address as publishAddress,dob as dob,num as num 
	 from (select tbl_book.* ,rownum as myrownum from tbl_book where rownum <= 3) where myrownum >= 3

	 
	  */
	
	@Test
	public void page() throws Exception{
		Where where = new Where("id", Condition.GT, 10);
		SimplePage<Book> page = bookDao.page(2, 2, where);
		System.out.println(page.getList());
	}
	
//	select id as id,name as name,price as price,publish_address as publishAddress,dob as dob,num as num  
//  from tbl_book where id >= (select id from tbl_book  name like '%小%' limit 20, 1)  and  name like '%小%'  LIMIT 2 
	
	
	//page
	@Test
	public void bookPage(){
		try {
			Where where = new Where("name", Condition.LIKE, "小");
			SimplePage<Book> page = bookDao.page(3, 1, where);
			List<Book> list = page.getList();
			if(list!=null){
				for(Book book :list){
					System.out.println(book);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	
}
