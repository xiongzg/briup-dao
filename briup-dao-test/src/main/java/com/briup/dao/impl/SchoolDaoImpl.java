package com.briup.dao.impl;

import com.briup.bean.School;
import java.sql.Connection;

import com.briup.base.jdbc.dao.BaseDaoImpl;
import com.briup.dao.ISchoolDao;

public class SchoolDaoImpl extends BaseDaoImpl<School,java.lang.Long> implements ISchoolDao{
	@Override
	public Connection getConnection() {
		return null;
	}
}