package com.briup.service.impl;

import com.briup.dao.impl.StudentDaoImpl;
import com.briup.bean.Student;
import com.briup.service.IStudentService;
import com.briup.base.jdbc.dao.IBaseDao;
import com.briup.base.jdbc.service.BaseServiceImpl;

public class StudentServiceImpl extends BaseServiceImpl<Student,java.lang.Long> implements IStudentService{
	@Override
	public IBaseDao<Student,java.lang.Long> getDao() {
		return new StudentDaoImpl();
	}
}