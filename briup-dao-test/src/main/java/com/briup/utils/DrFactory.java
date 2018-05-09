package com.briup.utils;

public class DrFactory {

	static DrDataSource da;
	public static DrDataSource getDDS(){
		if(da!=null){
			return da;
		}else{
			da = new DrDataSource();
			return da;
		}
	}
	
}
