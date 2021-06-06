package com.briup.base.jdbc.util;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.briup.base.jdbc.bean.Pojo;

/**
 * 查询<br>
 * 准备把sql语句封装为 bean对象
 * */
public class Query {

	



	/**
	 * 通过sql语句将查询的结果封装为pojo对象
	 * */
	public <T extends Pojo> List<T> selectList(String sql,Class<T> pojo,Connection connection){
		//System.out.println("Query 31 查询 :"+sql);
		SqlUtil sqlUtil = new SqlUtil();
		//用于返回的list集合
		List<T> list = new ArrayList<T>();
		//用于封装列名和属性对应关系 key 列名  value 属性
		// {PUBLISHADDRESS=private java.lang.String com.briup.bean.Book.publishAddress, PRICE=private java.lang.Double com.briup.bean.Book.price, NAME=private java.lang.String com.briup.bean.Book.name, DOB=private java.util.Date com.briup.bean.Book.dob, ID=private java.lang.Long com.briup.bean.Book.id, NUM=private java.lang.Integer com.briup.bean.Book.num}
		Map<String, Field> columnAndField = new HashMap<String, Field>();
		Field[] fields = pojo.getDeclaredFields();
		for(Field f : fields){
			String columnName = sqlUtil.getColumnNameByField(f);//列名
			//String tableString = sqlUtil.toTableString(columnName);
			//System.out.println(f.getType().getName().toString());
			if(columnName!=null){
				columnAndField.put(columnName.toUpperCase().trim(), f);
			}
		}
		try {
			//Connection connection = dataSource.getConnection();
			Statement stat = connection.createStatement();
			ResultSet set = stat.executeQuery(sql);
			while(set.next()){
				T ins = pojo.newInstance();//实例对象
				
				ResultSetMetaData metaData = set.getMetaData();
				int columnCount = metaData.getColumnCount();
				for(int i = 1;i<=columnCount;i++){
					
					String columnName = metaData.getColumnName(i);//得到的列名 没有 下划线这样
					if(columnName.indexOf("_")!=-1){
						columnName = columnName.replace("_", "");
					}
					Field field = columnAndField.get(columnName.toUpperCase().trim());//当前列对应的属性
					
					field.setAccessible(true);
					String fieldTypeName = field.getType().getName().toString();//属性 类型 名
					switch (fieldTypeName) {
					case "java.lang.Long":
						Long columnValueLong = set.getLong(columnName);
						SqlUtil.setFileValue(ins, field.getName(), columnValueLong);
						break;
					case "java.lang.Integer":
						Integer columnValueInteger = set.getInt(columnName);
						SqlUtil.setFileValue(ins, field.getName(), columnValueInteger);
						break;
					case "java.lang.Double":
						Double columnValueDouble = set.getDouble(columnName);
						SqlUtil.setFileValue(ins, field.getName(), columnValueDouble);
						break;
					case "java.lang.Boolean":
						Boolean columnValueBoolean = set.getBoolean(columnName);
						SqlUtil.setFileValue(ins, field.getName(), columnValueBoolean);
						break;
					case "java.lang.String":
						String columnValueString = set.getString(columnName);
						SqlUtil.setFileValue(ins, field.getName(), columnValueString);
						break;
					case "java.util.Date":
						Date columnValueDate = set.getDate(columnName);
						SqlUtil.setFileValue(ins, field.getName(),new java.util.Date(columnValueDate.getTime()));
						break;
					default:
						break;
					}
					
				}
				list.add(ins);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(list!=null && list.size()>0){
			return list;
		}else{
			return null;
		}
	}
	
	
	/**
	 * 查询一个值
	 * */
	public Long selectLong(String sql,Connection connection){
		try {
			Statement stat = connection.createStatement();
			ResultSet set = stat.executeQuery(sql);
			set.next();
			Long value = set.getLong(1);
			return value;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0L;
	}
	
	/**
	 * 保存
	 * */
	public int save(String sql,Connection connection){
		try {
			Statement stat = connection.createStatement();
			//System.out.println("Query.java_123_base save: "+sql);
			boolean execute = stat.execute(sql);
			stat.close();
			connection.close();
			if(execute){
				return 1;
			}else{
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 更新
	 * */
	public int update(String sql,Connection connection){
		Statement stat;
		try {
			stat = connection.createStatement();
			//System.out.println("Query 145 Update "+sql);
			int executeUpdate = stat.executeUpdate(sql);
			return executeUpdate;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 删除
	 * */
	public int delete(String sql,Connection connection){
		//System.out.println("Query:157:删除 : "+sql);
		Statement stat;
		try {
			stat = connection.createStatement();
			boolean delete = stat.execute(sql);
			return delete==true?1:0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
