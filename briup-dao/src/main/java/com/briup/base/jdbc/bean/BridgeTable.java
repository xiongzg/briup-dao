package com.briup.base.jdbc.bean;
/**
 * 存放桥表关系
 * */
public class BridgeTable {
	/**
	 * 桥表名
	 * */	
	private String bridgeName;
	/**
	 * 桥表中的列名 one
	 * */
	private String oneforignKeyColumn;
	/**
	 * 桥表中列名one 引用的列
	 * */
	private String oneJoinColumn;
	/**
	 * 桥表中列名one 引用的列 位于的表名
	 * */
	private String oneJoinTableName;
	/**
	 * 桥表中的列名two
	 * */
	private String twoforignKeyColumn;
	/**
	 * 桥表中列名two 引用的列
	 * */
	private String twoJoinColumn;
	/**
	 * 桥表中列名two 引用的列 位于的表名
	 * */
	private String twoJoinTableName;
	
	public String getBridgeName() {
		return bridgeName;
	}
	
	public void setBridgeName(String bridgeName) {
		this.bridgeName = bridgeName;
	}
	
	public String getOneforignKeyColumn() {
		return oneforignKeyColumn;
	}
	public void setOneforignKeyColumn(String oneforignKeyColumn) {
		this.oneforignKeyColumn = oneforignKeyColumn;
	}
	public String getOneJoinColumn() {
		return oneJoinColumn;
	}
	public void setOneJoinColumn(String oneJoinColumn) {
		this.oneJoinColumn = oneJoinColumn;
	}
	public String getOneJoinTableName() {
		return oneJoinTableName;
	}
	public void setOneJoinTableName(String oneJoinTableName) {
		this.oneJoinTableName = oneJoinTableName;
	}
	public String getTwoforignKeyColumn() {
		return twoforignKeyColumn;
	}
	public void setTwoforignKeyColumn(String twoforignKeyColumn) {
		this.twoforignKeyColumn = twoforignKeyColumn;
	}
	public String getTwoJoinColumn() {
		return twoJoinColumn;
	}
	public void setTwoJoinColumn(String twoJoinColumn) {
		this.twoJoinColumn = twoJoinColumn;
	}
	public String getTwoJoinTableName() {
		return twoJoinTableName;
	}
	public void setTwoJoinTableName(String twoJoinTableName) {
		this.twoJoinTableName = twoJoinTableName;
	}
	@Override
	public String toString() {
		return "桥表 [桥表名=" + bridgeName + ", 桥表列_1=" + oneforignKeyColumn
				+ ", 桥表列_1_引用列名=" + oneJoinColumn + ", 桥表列_1_引用表名=" + oneJoinTableName
				+ ", 桥表列_2=" + twoforignKeyColumn + ", 桥表列_2_引用列名=" + twoJoinColumn
				+ ", 桥表列_2_引用表名=" + twoJoinTableName + "]";
	}
	
}
