package com.briup.dao.impl;

import com.briup.bean.Student;
import java.sql.Connection;

import com.briup.base.jdbc.dao.BaseDaoImpl;
import com.briup.dao.IStudentDao;

public class StudentDaoImpl extends BaseDaoImpl<Student,java.lang.Long> implements IStudentDao{
	@Override
	public Connection getConnection() {
		return null;
	}
}