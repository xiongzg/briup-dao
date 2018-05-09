package com.briup.test;

import com.briup.base.jdbc.util.Create;

public class TC {
	public static void main(String[] args) throws Exception {
		
		Create.createTable();
		Create.createBaseDaoAndService();
		
		//String createSelect = SqlStaticData.createSelect(Course.class, Student.class, null);
		//System.out.println(createSelect);
	}
}
