package com.briup.web;

import org.junit.Before;
import org.junit.Test;

import com.briup.base.jdbc.bean.Where;
import com.briup.base.jdbc.util.Condition;
import com.briup.bean.Group;
import com.briup.bean.User;
import com.briup.dao.IGroupDao;
import com.briup.dao.IUserDao;
import com.briup.dao.impl.GroupDaoImpl;
import com.briup.dao.impl.UserDaoImpl;

public class TestDaoGroupAndUser {
	IGroupDao groupDao;
	IUserDao userDao;
	@Before
	public void before(){
		groupDao = new GroupDaoImpl();
		userDao = new UserDaoImpl();
	}
	
	@Test
	public void saveUser() throws Exception{
		User user = new User();
		user.setName("小明1");
		userDao.save(user);
	}
	@Test
	public void saveGroup() throws Exception{
		Group group = new Group();
		group.setId(10L);
		group.setName("we");
		groupDao.save(group);
	}
	@Test
	public void saveUserAndGroup() throws Exception{
		
		Group group = new Group();
		group.setName("rng");
		groupDao.save(group);
		
		User user = new User();
		user.setGroup(group);
		user.setName("小明1");
		userDao.save(user);
	}
	@Test
	public void saveGroupAndUser() throws Exception{
		
		Group group = new Group();
		group.setName("we");
		groupDao.save(group);
		
		User user = new User();
		user.setGroup(group);
		user.setName("小狗");
		userDao.save(user);
		
		User user2 = new User();
		user2.setGroup(group);
		user2.setName("草莓");
		userDao.save(user2);
		User user3 = new User();
		user3.setGroup(group);
		user3.setName("厂长");
		userDao.save(user3);

		
		
	}
	//保存 完成 
	@Test
	public void updateUser() throws Exception{
		Group g = new Group();
		g.setId(54L);
		User user = new User();
		user.setGroup(g);
		user.setId(47L);
		user.setName("小明2");
		userDao.update(user);
	}
	
	@Test
	public void updateGroup() throws Exception{
		
		Group g = new Group();
		g.setId(54L);
		g.setName("小红");
		groupDao.update(g);
	}
	//更新完成
	@Test
	public void updateDeleteGroup() throws Exception{
		Where where = new Where("id", Condition.IN,"52");
		groupDao.deleteByWherePrams(where);
	}
	@Test
	public void updateDeleteUser() throws Exception{
		Where where = new Where("id", Condition.IN,"57");
		userDao.deleteByWherePrams(where);
	}
	//删除完成
	
	@Test
	public void selectUser() throws Exception{
		User user = userDao.get(59L);
		System.out.println(user);
	}
	@Test
	public void selectGroup(){
		try {
			Group group = groupDao.get(56L);
			System.out.println(group);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
