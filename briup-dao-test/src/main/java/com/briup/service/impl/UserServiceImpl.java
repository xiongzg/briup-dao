package com.briup.service.impl;

import com.briup.dao.impl.UserDaoImpl;
import com.briup.bean.User;
import com.briup.service.IUserService;
import com.briup.base.jdbc.dao.IBaseDao;
import com.briup.base.jdbc.service.BaseServiceImpl;

public class UserServiceImpl extends BaseServiceImpl<User,java.lang.Long> implements IUserService{
	@Override
	public IBaseDao<User,java.lang.Long> getDao() {
		return new UserDaoImpl();
	}
}