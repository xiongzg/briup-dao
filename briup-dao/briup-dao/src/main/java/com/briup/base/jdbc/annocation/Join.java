package com.briup.base.jdbc.annocation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.briup.base.jdbc.bean.Pojo;

/**
 *在多对多关系的另一方<Br>
 *写在属性上<br>
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Join{
	/**
	 * 关联的类.class
	 * */
	 Class<? extends Pojo> associationClass();
	 /**
	  * 关联的类下某属性名
	  * */
	 String associationFieldName();
}
