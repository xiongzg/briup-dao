package com.briup.web;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.briup.base.jdbc.bean.Where;
import com.briup.base.jdbc.util.Condition;
import com.briup.bean.Course;
import com.briup.bean.Student;
import com.briup.dao.ICourseDao;
import com.briup.dao.IStudentDao;
import com.briup.dao.impl.CourseDaoImpl;
import com.briup.dao.impl.StudentDaoImpl;

public class TestDaoCourseAndStudent {
	ICourseDao courseDao ;
	IStudentDao studentDao;
	@Before
	public void before(){
		 courseDao = new CourseDaoImpl();
		 studentDao = new StudentDaoImpl();
	}
	
	@Test
	public void testSaveCourse() throws Exception{
		Course course = new Course();
		course.setName("中期检查表");
		courseDao.save(course);
	}
	@Test
	public void testSaveStudent() throws Exception{
		Student student = new Student();
		student.setName("张雪松");
		student.setCourses(new HashSet<Course>());
		studentDao.save(student);
	}
	/**
	 * 保存student 和course 的同时 会去桥表中保存数据<br>
	 * Student 类中的course属性使用了ManyToMany注解<br>
	 * 所以把Set(Course)集合 设置到student对象的属性中 会去保存桥表数据
	 * */
	@Test
	public void testSaveStudentAndCourse() throws Exception{
		Set<Course> courses1 = new HashSet<Course>();
		
		Course c1 = new Course();
		c1.setName("课程1");
		courses1.add(c1);
		Course c2 = new Course();
		c2.setName("课程2");
		courses1.add(c2);
		Course c3 = new Course();
		c3.setName("课程3");
		courses1.add(c3);
		Set<Course> courses2 = new HashSet<Course>();
		
		Course c21 = new Course();
		c21.setName("课程21");
		courses2.add(c21);
		Course c22 = new Course();
		c22.setName("课程22");
		courses2.add(c22);
		Course c23 = new Course();
		c23.setName("课程23");
		courses2.add(c23);
		
		courseDao.save(c1);
		courseDao.save(c2);
		courseDao.save(c3);
		courseDao.save(c21);
		courseDao.save(c22);
		courseDao.save(c23);
		
		
		Student student = new Student();
		student.setName("张雪松");
		student.setCourses(courses2);
		studentDao.save(student);
		
		
		Student student2 = new Student();
		student2.setName("黄虎虎");
		student2.setCourses(courses1);
		studentDao.save(student2);
	}
	/**
	 * 这个保存的时候 只会单纯保存student 和 course 不会去桥表中保存数据<Br>
	 * 在Student对象中是没有courses属性值的<br>
	 * 但是在Course对象中是有Set(Student)属性值，但是Course类中的students属性上是注解TempField所以不会去添加桥表关系。
	 * */
	@Test
	public void testSaveStudentAndCourse_2() throws Exception{
		Set<Student> students= new HashSet<Student>();
		
		Student student = new Student();
		student.setName("张雪松");
		students.add(student);
		studentDao.save(student);
		
		
		Student student2 = new Student();
		student2.setName("黄虎虎");
		students.add(student2);
		studentDao.save(student2);
		
		
		Course c1 = new Course();
		c1.setName("课程1");
		c1.setStudents(students);
		
		Course c2 = new Course();
		c2.setName("课程2");
		c2.setStudents(students);
		
		Course c3 = new Course();
		c3.setName("课程3");
		c3.setStudents(students);
		
		Course c21 = new Course();
		c21.setName("课程21");
		c21.setStudents(students);
		Course c22 = new Course();
		c22.setName("课程22");
		c22.setStudents(students);
		Course c23 = new Course();
		c23.setName("课程23");
		c23.setStudents(students);
		
		courseDao.save(c1);
		courseDao.save(c2);
		courseDao.save(c3);
		courseDao.save(c21);
		courseDao.save(c22);
		courseDao.save(c23);
		
		
	
	}
	
	@Test
	public void testUpdateStudent() throws Exception{
		Student student = new Student();
		student.setId(2L);
		student.setName("张雪松112");
		studentDao.update(student);
	}
	@Test
	public void testUpdateCourse() throws Exception{
		Course course = new Course();
		course.setId(2L);
		course.setName("中期检查表11");
		courseDao.update(course);
	}
	
	@Test
	public void deleteCourse() throws Exception{
		Where w = new Where("id", Condition.NOT, "null");
		courseDao.deleteByWherePrams(w);
	}
	
	
}
