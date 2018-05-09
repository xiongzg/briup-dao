package com.briup.bean;

import java.util.HashSet;
import java.util.Set;

import com.briup.base.jdbc.annocation.Join;
import com.briup.base.jdbc.annocation.TableName;
import com.briup.base.jdbc.bean.Pojo;
@TableName(name="tbl_course")
public class Course  extends Pojo{
	private Long id;
	private String name;
	@Join(associationClass=Student.class,associationFieldName="courses")
	private Set<Student> students = new HashSet<Student>(0);
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
	public Set<Student> getStudents() {
		return students;
	}
	public void setStudents(Set<Student> students) {
		this.students = students;
	}
	@Override
	public String toString() {
		return "Course [id=" + id + ", name=" + name + ", students=" + students + "]";
	}
	
}
