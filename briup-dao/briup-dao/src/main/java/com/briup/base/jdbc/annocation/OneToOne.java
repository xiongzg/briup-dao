package com.briup.base.jdbc.annocation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.briup.base.jdbc.bean.Pojo;

/**
 * 添加在外键列上<br>
 * reference 引用的pojo类对象<br>
 * foreignKeyColumn 在当前表建立外键列的名字<br>
 * 注:外键列默认number数据类型
 * */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToOne {
	Class<? extends Pojo> reference();
	String foreignKeyColumn();
}