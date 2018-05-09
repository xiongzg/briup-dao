package com.briup.dao.impl;

import com.briup.bean.Group;
import java.sql.Connection;

import com.briup.base.jdbc.dao.BaseDaoImpl;
import com.briup.dao.IGroupDao;

public class GroupDaoImpl extends BaseDaoImpl<Group,java.lang.Long> implements IGroupDao{
	@Override
	public Connection getConnection() {
		return null;
	}
}