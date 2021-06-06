package com.briup.base.jdbc.annocation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.briup.base.jdbc.bean.Pojo;
/**
 * 桥表的列关联
 * */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JoinColumn {
	/**
	 * 引用的类
	 * */
	Class<? extends Pojo> joinPojo();
	/**
	 * 引用的列名
	 * */
	String joinColumn();
	/**
	 * 当前表的外键列
	 * */
	String foreignKeyColumn();
}
