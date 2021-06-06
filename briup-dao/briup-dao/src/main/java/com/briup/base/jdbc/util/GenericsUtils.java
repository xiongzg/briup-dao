package com.briup.base.jdbc.util;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.briup.base.jdbc.bean.Pojo;
/**
 * 操作泛型的工具类
 * */
public class GenericsUtils {

	/**
	 * 获得父类泛型类型<br>
	 * @param clazz 父类.class
	 * @param index 下标
	 * */
	public static Class<?> getSuperClassGenricType(Class<?> clazz, int index) {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {

			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (index >= params.length || index < 0) {
			System.err.println("index < 0");
		}
		if (!(params[index] instanceof Class)) {

			return Object.class;
		}
		return (Class<?>) params[index];
	}

	/**
	 * 获得父类泛型类型<br>
	 * @param clazz 父类.class
	 * */
	public static Class<?> getSuperClassGenricType(Class<?> clazz) {

		return getSuperClassGenricType(clazz, 0);
	}


	public static Class<?> getMethodGenericReturnType(Method method, int index) {

		Type returnType = method.getGenericReturnType();

		if (returnType instanceof ParameterizedType) {

			ParameterizedType type = (ParameterizedType) returnType;
			Type[] typeArguments = type.getActualTypeArguments();

			if (index >= typeArguments.length || index < 0) {

				System.err.println("index < 0");
			}
			return (Class<?>) typeArguments[index];
		}
		return Object.class;
	}

	
	public static Class<?> getMethodGenericReturnType(Method method) {

		return getMethodGenericReturnType(method, 0);
	}

	
	public static List<Class<?>> getMethodGenericParameterTypes(Method method,
			int index) {

		List<Class<?>> results = new ArrayList<Class<?>>();
		Type[] genericParameterTypes = method.getGenericParameterTypes();

		if (index >= genericParameterTypes.length || index < 0) {

			System.err.println("index < 0");
		}
		Type genericParameterType = genericParameterTypes[index];

		if (genericParameterType instanceof ParameterizedType) {

			ParameterizedType aType = (ParameterizedType) genericParameterType;
			Type[] parameterArgTypes = aType.getActualTypeArguments();
			for (Type parameterArgType : parameterArgTypes) {
				Class<?> parameterArgClass = (Class<?>) parameterArgType;
				results.add(parameterArgClass);
			}
			return results;
		}
		return results;
	}

	public static List<Class<?>> getMethodGenericParameterTypes(Method method) {

		return getMethodGenericParameterTypes(method, 0);
	}


	public static Class<?> getFieldGenericType(Field field, int index) {

		Type genericFieldType = field.getGenericType();

		if (genericFieldType instanceof ParameterizedType) {

			ParameterizedType aType = (ParameterizedType) genericFieldType;
			Type[] fieldArgTypes = aType.getActualTypeArguments();
			if (index >= fieldArgTypes.length || index < 0) {

				System.err.println("index < 0");
			}
			return (Class<?>) fieldArgTypes[index];
		}
		return Object.class;
	}


	public static Class<?> getFieldGenericType(Field field) {

		return getFieldGenericType(field, 0);
	}
	/**
	 * 判断当前Field是否是pojo的子类型
	 * */
	public static Object isPojo(Field field){
		try {
			Type genericType = field.getGenericType();
			//str = class com.briup.xzg.base.mybatis.mypojo.School
			String str = genericType.toString();
			String className = str.substring(6);
			Object school = Class.forName(className).newInstance();
			if(school instanceof Pojo){
				return school;
			}else{
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
