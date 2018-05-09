package com.briup.service.impl;

import com.briup.dao.impl.CourseDaoImpl;
import com.briup.bean.Course;
import com.briup.service.ICourseService;
import com.briup.base.jdbc.dao.IBaseDao;
import com.briup.base.jdbc.service.BaseServiceImpl;

public class CourseServiceImpl extends BaseServiceImpl<Course,java.lang.Long> implements ICourseService{
	@Override
	public IBaseDao<Course,java.lang.Long> getDao() {
		return new CourseDaoImpl();
	}
}