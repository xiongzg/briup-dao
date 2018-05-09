package com.briup.bean;

import com.briup.base.jdbc.annocation.OneToMany;
import com.briup.base.jdbc.annocation.TableName;
import com.briup.base.jdbc.bean.Pojo;

@TableName(name = "tbl_user")
public class User extends Pojo {
	private Long id;
	private String name;
	@OneToMany(foreignKeyColumn="group_id",reference=Group.class)
	private Group group;

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

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name+"]";
	}

}
