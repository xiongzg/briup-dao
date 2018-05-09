package com.briup.web;

import org.junit.Before;
import org.junit.Test;

import com.briup.bean.Group;
import com.briup.bean.User;
import com.briup.dao.IGroupDao;
import com.briup.dao.IUserDao;
import com.briup.dao.impl.GroupDaoImpl;
import com.briup.dao.impl.UserDaoImpl;

//测试级联查询 Group 和 User
public class TestDaoCascadeGroupAndUser {

	IGroupDao groupDao;
	IUserDao userDao;
	@Before
	public void before(){
		groupDao = new GroupDaoImpl();
		userDao = new UserDaoImpl();
	}
	//查询User 并且级联查询User下的Group属性
	//成功
	@Test
	public void userCascadeGroup() throws Exception{
		User user = userDao.get(3L, Group.class);
		System.out.println("------"+user);
		System.out.println("------------"+user.getGroup());
	}
	//成功
	@Test
	public void groupCascadeUser(){
		try {
			Group group = groupDao.get(4L,User.class);
			System.out.println("-=-=-="+group);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
