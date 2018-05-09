package com.briup.service.impl;

import com.briup.dao.impl.BookDaoImpl;
import com.briup.bean.Book;
import com.briup.service.IBookService;
import com.briup.base.jdbc.dao.IBaseDao;
import com.briup.base.jdbc.service.BaseServiceImpl;

public class BookServiceImpl extends BaseServiceImpl<Book,java.lang.Long> implements IBookService{
	@Override
	public IBaseDao<Book,java.lang.Long> getDao() {
		return new BookDaoImpl();
	}
}