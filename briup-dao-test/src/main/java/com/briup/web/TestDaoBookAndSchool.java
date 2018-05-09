package com.briup.web;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.briup.base.jdbc.bean.SimplePage;
import com.briup.base.jdbc.bean.Where;
import com.briup.base.jdbc.util.Condition;
import com.briup.bean.Book;
import com.briup.bean.School;
import com.briup.dao.IBookDao;
import com.briup.dao.ISchoolDao;
import com.briup.dao.impl.BookDaoImpl;
import com.briup.dao.impl.SchoolDaoImpl;

public class TestDaoBookAndSchool {
	IBookDao bookDao;
	ISchoolDao schoolDao;

	@Before
	public void before() {
		bookDao = new BookDaoImpl();
		schoolDao = new SchoolDaoImpl();
	}

	@Test
	public void saveBook() throws Exception {
		int i = 10;
		Book book = new Book();
		book.setDob(new Date());
		book.setName("小明_" + i);
		book.setPrice(10.01 + i);
		book.setPublishAddress("南昌_" + i);
		bookDao.save(book);
	}

	@Test
	public void saveSchool() throws Exception {
		School school = new School();
		school.setName("小学校");
		schoolDao.save(school);
	}

	@Test
	public void saveSchoolAndBook() throws Exception {

		Book book = new Book();
		book.setDob(new Date());
		book.setName("小明1");
		book.setPrice(10.012);
		book.setPublishAddress("南昌2");
		bookDao.save(book);

		School school = new School();
		school.setName("小学校");
		school.setBook(book);
		schoolDao.save(school);
	}

	// 保存 基本测试完成。。。
	@Test
	public void updateBook() throws Exception {
		Book book = new Book();
		book.setId(12L);
		// book.setName("新小明");
		// book.setNum(10);
		// book.setDob(new Date(10000000));
		book.setPublishAddress("昆山");
		Where where = new Where("name", Condition.EQ, "小明");
		bookDao.update(book, where);
	}

	@Test
	public void updateSchool() throws Exception {
		Book book = new Book();
		book.setId(46L);
		School school = new School();
		school.setName("zs小学");
		school.setId(45L);
		school.setBook(book);
		schoolDao.update(school);
	}

	// 更新删除完成
	@Test
	public void deleteBook() throws Exception {
		// bookDao.delete(21L);
		Where where = new Where("name", Condition.EQ, null);
		bookDao.deleteByWherePrams(where);
	}

	// 查询....
	@Test
	public void findBook() throws Exception {
		Book book = bookDao.get(41L);
		System.out.println(book);

	}

	@Test
	public void findSchool() throws Exception {
		School school = schoolDao.get(45L);
		System.out.println(school);
	}

	@Test
	public void findSchoolPage() throws Exception {
		SimplePage<School> page = schoolDao.page(10, 1, null);
		System.out.println(page);
	}

	
	
	
}
