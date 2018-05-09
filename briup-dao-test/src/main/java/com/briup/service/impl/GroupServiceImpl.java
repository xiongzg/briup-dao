package com.briup.service.impl;

import com.briup.dao.impl.GroupDaoImpl;
import com.briup.bean.Group;
import com.briup.service.IGroupService;
import com.briup.base.jdbc.dao.IBaseDao;
import com.briup.base.jdbc.service.BaseServiceImpl;

public class GroupServiceImpl extends BaseServiceImpl<Group,java.lang.Long> implements IGroupService{
	@Override
	public IBaseDao<Group,java.lang.Long> getDao() {
		return new GroupDaoImpl();
	}
}