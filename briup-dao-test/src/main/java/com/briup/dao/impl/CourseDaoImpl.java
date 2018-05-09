package com.briup.dao.impl;

import com.briup.bean.Course;
import java.sql.Connection;

import com.briup.base.jdbc.dao.BaseDaoImpl;
import com.briup.dao.ICourseDao;

public class CourseDaoImpl extends BaseDaoImpl<Course,java.lang.Long> implements ICourseDao{
	@Override
	public Connection getConnection() {
		return null;
	}
}