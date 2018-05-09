package com.briup.base.jdbc.annocation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多对多关系<br>
 * 添加在多对多的任意一个类 Set<Pojo> pojos 属性上<br>
 * 另一个类 需要添加 Join注解<br>
 *  Join(associationClass=Student.class,associationFieldName="courses")<br>
	private Set<Student> students = new HashSet<Student>(0);
 * 
 * <hr>
  ManyToMany(joinTableName="c_s",joinColumns={<br>
  	&nbsp;&nbsp;&nbsp;JoinColumn(joinPojo = Course.class, joinColumn = "id", foreignKeyColumn = "course_id"),<br>
    &nbsp;&nbsp;&nbsp;JoinColumn(joinPojo=Student.class,joinColumn="id",foreignKeyColumn="student_id")})<br>
	private Set(Course) courses = new HashSet(Course)();<br>
 
 * */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToMany {
	/**
	 * 桥表 表名
	 * */
	String joinTableName();
	/**
	 * 建立桥表的信息
	 * */
	JoinColumn[] joinColumns();
	
	
}