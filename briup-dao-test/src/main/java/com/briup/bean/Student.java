package com.briup.bean;

import java.util.HashSet;
import java.util.Set;

import com.briup.base.jdbc.annocation.JoinColumn;
import com.briup.base.jdbc.annocation.ManyToMany;
import com.briup.base.jdbc.annocation.TableName;
import com.briup.base.jdbc.bean.Pojo;

@TableName(name = "tbl_student")
public class Student extends Pojo {
	private Long id;
	private String name;
	@ManyToMany(joinTableName = "c_s", joinColumns = {
			@JoinColumn(joinPojo = Course.class, joinColumn = "id", foreignKeyColumn = "course_id"),
			@JoinColumn(joinPojo=Student.class,joinColumn="id",foreignKeyColumn="student_id")})
	private Set<Course> courses = new HashSet<Course>();

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

	public Set<Course> getCourses() {
		return courses;
	}

	public void setCourses(Set<Course> courses) {
		this.courses = courses;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", courses=" + courses + "]";
	}

}
