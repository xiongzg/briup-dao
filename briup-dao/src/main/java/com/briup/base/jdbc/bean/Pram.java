package com.briup.base.jdbc.bean;


/**
 * Pojo字段封装类<br>
 * <li> property : field 属性名
 * <li> property : value 属性值
 */
public class Pram {
	/**
	 * 属性名
	 * */
	private String field;
	/**
	 * 属性值
	 * */
	private Object value;

	public Pram(){}
	
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Pram(String field, Object value) {
		super();
		this.field = field;
		this.value = value;
	}

	@Override
	public String toString() {
		return "Pram [field=" + field + ", value=" + value + "]\r";
	}
}
