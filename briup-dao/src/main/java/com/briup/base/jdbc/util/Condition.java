package com.briup.base.jdbc.util;
/**
 * 条件的枚举<Br>
 * 使用当前类去获取各种 条件 <br>
 * <li> EQ     = 
 * <li> NOT   != 
 * <li> LIKE  like 
 * <li> GT    >
 * <li> GTE   >=
 * <li> LT    <
 * <li> LTE   <=
 * <li> IN   in
 * <li> BetweenAnd   in
 * 
 * */
public enum Condition {

	EQ,NOT,LIKE,GT,LT,IN,LTE,GTE,BetweenAnd;
	
	public static String getSqlWhere(Condition condition){
		switch (condition) {
		case EQ:
			return "=";
		case NOT:
			return "!=";
		case LIKE:
			return "like";	
		case GT:
			return ">";	
		case LT:
			return "<";	
		case IN:
			return "in (${value})";
		case LTE:
			return "<=";
		case GTE:
			return ">=";
		case BetweenAnd:
			return "between ${1} and ${2}";
		default:
			return "=";	
		}
	}
}
