package com.briup.bean;

import java.util.HashSet;
import java.util.Set;

import com.briup.base.jdbc.annocation.TableName;
import com.briup.base.jdbc.annocation.TempField;
import com.briup.base.jdbc.bean.Pojo;

@TableName(name="tbl_group")
public class Group extends Pojo{
	
	private Long id;
	
	private String name;
	@TempField
	private Set<User> users = new HashSet<User>(0);

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + ", users=" + users + "]";
	}
	
}
