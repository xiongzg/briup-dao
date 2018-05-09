package com.briup.base.jdbc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Create {
	static private Properties properties = new Properties();
	static {

		String path = Create.class.getClassLoader().getResource("").getPath();
		// System.out.println(this.getClass().getClassLoader().getResource(""));
		// String path = "src";
		// System.out.println("连接对象的 路径path: "+path);
		File file = new File(path, "baseDao.properties");
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			properties.load(fis);
			properties.clone();
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * 根据baseDao.properties文件自动生成 项目dao层和service层
	 * */
	@SuppressWarnings("unchecked")
	public static void createBaseDaoAndService() {
		
		CreateBaseDaoAndService baseDao = new CreateBaseDaoAndService(properties.getProperty("baseDaoPackage"), properties.getProperty("baseServicePackage"), Boolean.parseBoolean(properties.getProperty("userSpring")),Boolean.parseBoolean(properties.getProperty("coverageDaoAndService")));
		//CreateBaseDaoAndService baseDao = new CreateBaseDaoAndService(properties.getProperty("baseDaoPackage"), properties.getProperty("baseServicePackage"), Boolean.parseBoolean(properties.getProperty("userSpring")),properties.getProperty("createSqlSession"),Boolean.parseBoolean(properties.getProperty("coverageDaoAndService")));
		Class<? extends Serializable> forName = null;
		try {
			forName = (Class<? extends Serializable>) Class.forName(properties.getProperty("idClassName"));
			baseDao.create(properties.getProperty("pojoFileQueryName"),forName );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据baseDao.properties文件自动生成 项目对应数据库表
	 * */
	public static void createTable() {
		try {
			CreateTable createTable = new CreateTable();
			createTable.setOnDeleteCascade(properties.getProperty("onDeleteCascade"));
			createTable.setOnDeleteSetNull(properties.getProperty("onDeleteSetNull"));
			Class.forName(properties.getProperty("driver"));
			Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("username"), properties.getProperty("password"));
			createTable.create(properties.getProperty("pojoFileQueryName"), connection, properties.getProperty("username").toUpperCase(),Boolean.parseBoolean(properties.getProperty("coverageTable")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
