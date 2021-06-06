package com.briup.base.jdbc.bean;

/**
 * 用于封装主键和外键列的关系
 * */
public class PrimaryForeign {

	//主键表名
	private String primaryTableName;
	//引用表的主键
	private String foreignTablePrimaryKey;
	
	//外键表名
	private String foreignTableName;
	//主表的外键列名
	private String primaryForeignKey;
	
	//多对多的桥表关系
	private BridgeTable bridgeTable;
	
	
	public String getPrimaryTableName() {
		return primaryTableName;
	}


	public void setPrimaryTableName(String primaryTableName) {
		this.primaryTableName = primaryTableName;
	}




	public String getForeignTablePrimaryKey() {
		return foreignTablePrimaryKey;
	}


	public void setForeignTablePrimaryKey(String foreignTablePrimaryKey) {
		this.foreignTablePrimaryKey = foreignTablePrimaryKey;
	}


	public String getForeignTableName() {
		return foreignTableName;
	}


	public void setForeignTableName(String foreignTableName) {
		this.foreignTableName = foreignTableName;
	}


	public String getPrimaryForeignKey() {
		return primaryForeignKey;
	}


	public void setPrimaryForeignKey(String primaryForeignKey) {
		this.primaryForeignKey = primaryForeignKey;
	}


	public BridgeTable getBridgeTable() {
		return bridgeTable;
	}


	public void setBridgeTable(BridgeTable bridgeTable) {
		this.bridgeTable = bridgeTable;
	}


	@Override
	public String toString() {
		return "PrimaryForeign [主表名:" + primaryTableName + ", 主表外键列名:" + primaryForeignKey + ", 引用表主键名:" + foreignTablePrimaryKey 
				+ ", 引用表名:" + foreignTableName  + ", 桥表信息: "
				+ bridgeTable + "]";
	}
	
	
	
	
}
