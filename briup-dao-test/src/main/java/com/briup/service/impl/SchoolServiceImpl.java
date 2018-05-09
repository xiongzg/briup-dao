package com.briup.service.impl;

import com.briup.dao.impl.SchoolDaoImpl;
import com.briup.bean.School;
import com.briup.service.ISchoolService;
import com.briup.base.jdbc.dao.IBaseDao;
import com.briup.base.jdbc.service.BaseServiceImpl;

public class SchoolServiceImpl extends BaseServiceImpl<School,java.lang.Long> implements ISchoolService{
	@Override
	public IBaseDao<School,java.lang.Long> getDao() {
		return new SchoolDaoImpl();
	}
}