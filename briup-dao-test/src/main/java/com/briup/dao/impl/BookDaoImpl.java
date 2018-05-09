package com.briup.dao.impl;

import com.briup.bean.Book;
import java.sql.Connection;

import com.briup.base.jdbc.dao.BaseDaoImpl;
import com.briup.dao.IBookDao;

public class BookDaoImpl extends BaseDaoImpl<Book,java.lang.Long> implements IBookDao{
	@Override
	public Connection getConnection() {
		return null;
	}
}