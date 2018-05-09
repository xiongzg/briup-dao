package com.briup.dao.impl;

import com.briup.bean.User;
import java.sql.Connection;

import com.briup.base.jdbc.dao.BaseDaoImpl;
import com.briup.dao.IUserDao;

public class UserDaoImpl extends BaseDaoImpl<User,java.lang.Long> implements IUserDao{
	@Override
	public Connection getConnection() {
		return null;
	}
}