package com.briup.web;

import org.junit.Before;
import org.junit.Test;

import com.briup.bean.Course;
import com.briup.bean.Student;
import com.briup.dao.ICourseDao;
import com.briup.dao.IStudentDao;
import com.briup.dao.impl.CourseDaoImpl;
import com.briup.dao.impl.StudentDaoImpl;

//多对多的级联查询
public class TestDaoCascadeCourseAndStudent {

	ICourseDao courseDao ;
	IStudentDao studentDao;
	@Before
	public void before(){
		 courseDao = new CourseDaoImpl();
		 studentDao = new StudentDaoImpl();
	}
	
	@Test
	public void courseCascadeStudent() throws Exception{
		Course course = courseDao.get(15L, Student.class);
		System.out.println(course);
	}
	@Test
	public void StudentCascadeCourse() throws Exception{
		Student student = studentDao.get(108L, Course.class);
		System.out.println(student);
	}
	
	
	
}
