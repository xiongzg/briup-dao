package com.briup.base.jdbc.annocation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识某个字段不录入数据库中<Br>
 * 标识该注解的字段将没有增删改查的功能。<br>
 * 一般位于 一对一关系没有写oneToOne的一方<br>
 * 一对多关系没有写OneToMany的一方<br>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface TempField {

	 
}
