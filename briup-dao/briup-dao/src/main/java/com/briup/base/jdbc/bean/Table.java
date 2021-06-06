package com.briup.base.jdbc.bean;

import java.util.HashMap;
import java.util.Map;
/**
 * 封装自动建表的数据
 * */
public class Table {
	/**
	 * 表名
	 * */
	private String tableName;
	/**
	 * key 列名   value  列的数据库类型
	 * */
	private Map<String, JavaType> column = new HashMap<String, JavaType>();
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Map<String, JavaType> getColumn() {
		return column;
	}
	public void setColumn(Map<String, JavaType> column) {
		this.column = column;
	}
	@Override
	public String toString() {
		return "Table [tableName=" + tableName + ", column=" + column + "]";
	}
}
