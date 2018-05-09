package com.briup.base.jdbc.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.briup.base.jdbc.util.Condition;

/**
 * 限制条件where 子句
 * */
public class Where{
	/**
	 * 拼接以后的整个where子句
	 * */
	private String pram;
	/**
	 * 排序字句
	 * */
	private String orderBy;
	/**
	 * 记录whereParams 中的列名
	 * */
	private List<String> columnNames = new ArrayList<String>();
	
	
	
	/**
	 * @param colnumName 数据库列名
	 * @param condition 用于封装条件的 枚举类型值 eg:  Condition.EQ
	 * 	<li>如果condition选中的是between and  那么传入的 value 需要是 "1-2"格式	<b>不支持日期Date使用</b>
	 *  <li>如果condition选中的是in 那么传入的 value 需要是 1,2,3 或者 'name1','name2','name3' <b>不支持日期Date使用</b>
	 * @param value 列对应的值
	 * */
	@SuppressWarnings("deprecation")
	public Where(String colnumName, Condition condition, Serializable value){
		if(condition==null){
			return;
		}
		//把属性添加到存放属性的集合中
		columnNames.add(colnumName);
		
		//得到 where 的条件
		String where = Condition.getSqlWhere(condition);
		if(null == colnumName && null == where && value == where){
			return;
		}
		
		if (null == value) {
			if (where.equals("=")) {
				where = " is";
			}else{
				where = " not ";
			}
			this.pram = " where " + colnumName + " " + where + " null";
		}else{
			if ("like".equals(where)) {
				this.pram = " where " + colnumName + " "  + where + " '%" + value + "%'";
			}else if(where.indexOf("between")!=-1){
				// where name between ${1} and ${2}
				int indexOf = value.toString().indexOf("-");
				//System.out.println(colnumName+"  WherePrams.java_58_ "+value);
				if(indexOf!=-1){
					String[] split = value.toString().split("[-]");
					where = where.toString().replace("${1}", split[0]);
					where = where.toString().replace("${2}", split[1]);
					this.pram = " where " + colnumName + " "+ where;
				}else{
					try {
						throw new Exception("使用between and 但是传入的value 不符合规则 \r例如: 1-5");
					} catch (Exception e) {
						e.printStackTrace();
						return ;
					}
				}
			}else if(where.indexOf("in")!=-1){
				/*StringBuffer inpram = new StringBuffer();
				System.out.println(value instanceof Collection);
				if (value instanceof Collection) {
					Iterator iterator = ((Collection) value).iterator();
					boolean hasNext = iterator.hasNext();
					if(hasNext){
						String msg = iterator.next().toString();
						inpram.append("'"+msg+"',");
					}
				}
				String sbinpram = inpram.toString();
				//得到的需要的value值
				String whereValue = sbinpram.substring(0,sbinpram.length()-1);*/
				where = where.replace("${value}", (CharSequence) value);
				this.pram = " where " + colnumName + " "+ where;
				//System.out.println("WherePrams.java_75_:"+pram);
			}else{
				if(value instanceof Date){
					Date date = (Date)value;
					this.pram = " where " + colnumName + " " + where + " to_date('" +date.toLocaleString()+ "','yyyy-mm-dd hh24:mi:ss')";
				}else{
					this.pram = " where " + colnumName + " " + where + " '" + value + "'";
				}
			}
		}
	}
		
	/**
	 * and条件
	 * @param columnName 列名
	 * @param condition 用于封装条件的 枚举类型值 eg:  Condition.EQ
	 * @param value 列值
	 * @return  拼接以后的Where条件对象
	 */
	public Where and(String columnName,  Condition condition, Serializable value){
		//把属性添加到存放属性的集合中
		columnNames.add(columnName);
		String where = Condition.getSqlWhere(condition);
		if (null == value) {
			if (where.equals("=")) {
				where = " is";
			} else {
				where = " not ";
			}
			this.pram = " and " + columnName + " " + where + " null";
		} else {
			if ("like".equals(where)) {
				this.pram += " and " + columnName + " " + where + " '%" + value + "%'";
			}else{
				this.pram += " and " + columnName + " " + where + " '" + value + "'";
			}
		}
		
		return this;
	}
	/**
	 * 向where字句中添加or条件
	 * @param columnName 列名
	 * @param condition 用于封装条件的 枚举类型值 eg:  Condition.EQ
	 * @param value 列值
	 * @return
	 */
	public Where or(String columnName,  Condition condition, Serializable value){
		//把属性添加到存放属性的集合中
		columnNames.add(columnName);
		String where = Condition.getSqlWhere(condition);
		if (null == value) {
			if ("=".equals(where)) {
				where = " is ";
			} else {
				where = " not ";
			}
			this.pram = " or " + columnName + " " + where + " null";
		} else {
			if ("like".equals(where)) {
				this.pram += " or " + columnName + " " + where + " '%" + value + "%'";
			}else{
				this.pram += " or " + columnName + " " + where + " '" + value + "'";
			}
		}
		return this;
	}
	
	/**
	 * 排序<br>
	 * <li> id
	 * <li> id asc,name desc
	 * 
	 * @param order 排序规则
	 * */
	public Where orderBy(String order){
		if(this.orderBy != null){
			this.orderBy += "," + order;
		}else{
			this.orderBy = order;
		}
		return this;
	}

	@Override
	public String toString() {
		return "WherePrams [pram=" + pram + "]";
	}
	
	/**
	 * 获取prams
	 * @return
	 */
	public String getWhere(){
		String p = " ";
		p += null == this.pram ? "" : this.pram;
		p += null == this.orderBy ? "" : (" order by " + this.orderBy);
		return p;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}
}
