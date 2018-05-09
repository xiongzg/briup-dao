package com.briup.base.jdbc.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.briup.base.jdbc.annocation.ColumnName;
import com.briup.base.jdbc.annocation.JoinColumn;
import com.briup.base.jdbc.annocation.ManyToMany;
import com.briup.base.jdbc.annocation.OneToMany;
import com.briup.base.jdbc.annocation.OneToOne;
import com.briup.base.jdbc.annocation.TableName;
import com.briup.base.jdbc.annocation.TempField;
import com.briup.base.jdbc.bean.Pojo;
import com.briup.base.jdbc.bean.Pram;
import com.briup.base.jdbc.bean.PrimaryForeign;
import com.briup.base.jdbc.util.GenericsUtils;
import com.briup.test.bean.Course;
import com.briup.test.bean.Group;
import com.briup.test.bean.Student;
import com.briup.test.bean.User;

/**
 * Sql生成工具类
 * @param <T> 要生成Sql的实体类
 */
@SuppressWarnings("all")
public class SqlUtil {

	
	
	
	/**
	 * 通过Class获取生成对应类 需要查询的列 即:select 列...
	 * @param po pojo类对象的class
	 * @return 属性集合
	 * 
	 *		 BookABC:
         	[Pram [field=id as id, value=null]
        		, Pram [field=name as name, value=null]
        		, Pram [field=price as price, value=null]
        		, Pram [field=publish_address as publishAddress, value=null]
        		, Pram [field=dob as dob, value=null]
        		, Pram [field=num as num, value=null]
	 * 
	 */
	public List<Pram> getPramListOfSelect(Class<? extends Pojo> po){
		List<Pram> list = new ArrayList<Pram>();
		Class<? extends Pojo> thisClass = po;
	    Field[] fields = thisClass.getDeclaredFields();
	    	try {
	    		Object o = thisClass.newInstance();
	    		for(Field f : fields){
	    			boolean annotationPresent = f.isAnnotationPresent(ManyToMany.class);
	    			if(annotationPresent){
	    				continue;
	    			}
	    			if (!f.isAnnotationPresent(TempField.class)) {
	    				String fieldName = f.getName();
	    				//判断是否是boolean类型
		    			String getf = "get";
		    			String fieldType = f.getGenericType().toString();
		    			if (fieldType.indexOf("boolean") != -1 ) {
		    				getf = "is";
						}
		    			if (f.isAnnotationPresent(ColumnName.class)) {
		    				String columnName = f.getAnnotation(ColumnName.class).name();
		    				Method get = thisClass.getMethod(getf + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
		    				Object getValue = get.invoke(o);
		    				Pram pram = new Pram(columnName + " as " + fieldName, getValue);
							list.add(pram);
		    			}else{
		    				String columnName = toTableString(fieldName);
		    				Method get = thisClass.getMethod(getf + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
		    				Object getValue = get.invoke(o);
		    				Pram pram = new Pram(columnName + " as " + fieldName, getValue);
							list.add(pram);
						}
					}
	    		}
			}  catch (Exception e) {
				e.printStackTrace();
			}
		return list;
	}
	
	/**
	 * 通过传入的pojo类对象 返回该pojo对象的非id非@TempField属性的 属性名和属性值集合<Br>
	 * 
	 *   		
        		BookABC
        		getPramListofStatic : BaseDaoImpl.java___327:
        		[Pram [field=id, value=22]
        		, Pram [field=name, value=hansdfsdfsdfsddom]
        		, Pram [field=price, value=190.2]
        		, Pram [field=publish_address, value=昆山sdfsdf出版社]
        		, Pram [field=dob, value=Sat Apr 28 08:58:02 CST 2018]
        		, Pram [field=num, value=1029]
	 * 
	 * */
	public static<T extends Pojo> List<Pram> getPramListofStatic(Pojo pojo){
		List<Pram> list = new ArrayList<Pram>();
		Class<? extends Pojo> thisClass = pojo.getClass();
		//获取当前传入 pojo类 下所有属性
	    Field[] fields = thisClass.getDeclaredFields();
	    	try {
	    		for(Field field : fields){
	    			//不是id属性 没有添加@TempField注解的属性进入判断内
		    		if(!field.isAnnotationPresent(TempField.class)){//oracle不需要主键自动增长  如果是主键自动增长 使用下面的判断条件
		    		//if(!f.getName().equalsIgnoreCase("ID") && !f.isAnnotationPresent(TempField.class)){
		    			//属性名
		    			String fName = field.getName();
		    			//判断是否是boolean类型
		    			String getf = "get";
		    			String fieldType = field.getGenericType().toString();
		    			//System.out.println("属性的数据类型+"+fieldType);
		    			if (fieldType.indexOf("boolean") != -1) {
		    				getf = "is";
						}
		    			
		    			if (field.isAnnotationPresent(ColumnName.class)) {//包含 @ColnumdName 注解进入判断
		    				//获得添加@ColnumName 注解的 列名
		    				String fieldName = field.getAnnotation(ColumnName.class).name();
		    				//判断当前类型是否是 pojo的子类型
		    				Object isPojo = GenericsUtils.isPojo(field);
		    				if(isPojo!=null){//是子类型
		    					Method getPojo = thisClass.getMethod(getf + fName.substring(0, 1).toUpperCase() + fName.substring(1));
		    					//调用了pojo类的get方法/is方法获得返回值getValue
		    					Object getObject = getPojo.invoke(pojo);//得到对象
		    					Class<? extends Object> isPojoClass = getObject.getClass();
		    					Method getIdmethod = isPojoClass.getMethod("getId");
		    					Object getValue = getIdmethod.invoke(getObject);
		    					Pram pram = new Pram(fieldName, getValue);
		    					list.add(pram);
		    				}else{//不是子类型
		    					Method get = thisClass.getMethod(getf + fName.substring(0, 1).toUpperCase() + fName.substring(1));
		    					//调用了pojo类的get方法/is方法获得返回值getValue
		    					Object getValue = get.invoke(pojo);
		    					if(fieldType.indexOf("Date") != -1){//如果是date类型 
		    						Date date = (Date) getValue;
		    						Pram pram = new Pram(fieldName,date);
		    						list.add(pram);
		    					}else if(fieldType.indexOf("Boolean") != -1){//4_28_如果是boolean类型
		    						Boolean bool = Boolean.parseBoolean(getValue.toString());
		    						Pram pram = new Pram(fieldName,bool);
		    						list.add(pram);
		    						System.out.println("SqlUtil.java_布尔类型:"+pram);
		    					}else{//普通类型
		    						Pram pram = new Pram(fieldName, getValue);
		    						list.add(pram);
		    					}
		    				}
		    			}else if(field.isAnnotationPresent(ManyToMany.class)){
		    				//学生
		    				/*@ManyToMany(joinTableName = "c_s", joinColumns = {
		    				@JoinColumn(joinPojo = Course.class, joinColumn = "id", foreignKeyColumn = "course_id"),
		    				@JoinColumn(joinPojo=Student.class,joinColumn="id",foreignKeyColumn="student_id")})*/
		    				ManyToMany mtm = field.getAnnotation(ManyToMany.class);
		    				//桥表 名
		    				String joinTableName = mtm.joinTableName();
		    				JoinColumn[] joinColumns = mtm.joinColumns();
		    				
		    				JoinColumn column0 =joinColumns[0];
		    				JoinColumn column1 = joinColumns[1];
		    				
		    				
		    				String column0string = column0.joinPojo().toString();
		    				String column1string = column1.joinPojo().toString();
		    				
		    				//规定  当前类 的列 为 column0
		    				//桥表第一个列
		    				String foreignKeyColumn0 = null;
		    				//桥表第二个列
		    				String foreignKeyColumn1 = null;
		    				
		    				//当前类 对象 == 第一个JoinPojo 属性
		    				if(pojo.getClass().toString().equals(column0string)){
		    					foreignKeyColumn0 = column0.foreignKeyColumn();
		    					foreignKeyColumn1 = column1.foreignKeyColumn();
		    				}else{
		    					foreignKeyColumn0 = column1.foreignKeyColumn();
		    					foreignKeyColumn1 = column0.foreignKeyColumn();
		    				}
		    				
		    				
		    				
		    				Serializable foreignKeyColumn0_value = (Serializable) thisClass.getMethod("getId").invoke(pojo);
		    				Pram pram = new Pram("mtm,"+field.getName(), joinTableName+","+foreignKeyColumn0+","+foreignKeyColumn1);
		    				list.add(pram);
		    				
		    			}else{//不包含 @ColnumdName 注解进入判断
		    				String fieldName = new SqlUtil().toTableString(fName);
		    				//System.out.println("SqlUtil.java__140__驼峰标识符 进行转换为 _ 的标识符 结果 :  "+fieldName);
		    				Method get = thisClass.getMethod(getf + fName.substring(0, 1).toUpperCase() + fName.substring(1));
		    				Object getValue = get.invoke(pojo);
		    				if(getValue==null){
		    					continue;
		    				}
		    				if(fieldType.indexOf("Date") != -1){//如果是date类型 
	    						Date date = (Date) getValue;
	    						//System.out.println("添加 : "+fieldName+"---"+date.getClass());
	    						Pram pram = new Pram(fieldName,date);
								list.add(pram);
			    			}else if(fieldType.indexOf("Boolean") != -1){//4_28_如果是boolean类型
	    						Boolean bool = Boolean.parseBoolean(getValue.toString());
	    						Pram pram = new Pram(fieldName,bool);
	    						list.add(pram);
	    						System.out.println("SqlUtil.java_布尔类型:"+pram);
	    					}else{
			    				Pram pram = new Pram(fieldName, getValue);
			    				list.add(pram);
			    			}
						}
		    		}
	    		}
			}  catch (Exception e) {
				e.printStackTrace();
			}
		return list;
	}
	
	
	
	
	
	/**
	 * 
	 * 把 属性值设置到 传入pojo对象的属性中<Br>
	 * 
	 * 
	 * @param pojo pojo类对象
	 * @param fieldName 属性名
	 * @param fileValue 属性值
	 */
	public static boolean setFileValue(Pojo pojo, String fieldName, Serializable fileValue){
		//date : 2018-03-23 09:13:41.0
		//把从数据库查询的列名 全部改为 属性名 
		//eg : 列名 USER_ADDRESS  改为 属性名 userAddress 和类中属性名一致
		//导致效率会低
		Field[] fields = pojo.getClass().getDeclaredFields();
		for(int i =0;i<fields.length;i++){
			Field field = fields[i];
			//属性名
			String name = field.getName();
			if(name.toUpperCase().equals(fieldName.toUpperCase())){
				fieldName = name;
			}
		}
		
		Class<? extends Pojo> thisClass = pojo.getClass();
		try {
			//属性名 是 Id 
			if ("ID".equalsIgnoreCase(fieldName)) {
				try {
					//获取当前pojo对象 该属性名的Field对象
					Field field = thisClass.getDeclaredField(fieldName);
					String calssName = field.getType().getName();
					if (calssName.equals("int") || calssName.equals("java.lang.Integer")) {
						if (Integer.MAX_VALUE >  new Integer("" + fileValue)) {
							Integer val = new Integer("" + fileValue);
							Method method = thisClass.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), field.getType());
							//调用set方法把属性值添加到当前pojo对象的属性中
							method.invoke(pojo, val);
							return true;
						}else{
							throw new Exception("ID type is not a corresponding type at " + "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1) + "\n"
									+ "the will give value type is " + fileValue.getClass().getName() + "\n" 
									+ "the filed type type is " + field.getType().getName());
						}
					}else if(calssName.equals("long") || calssName.equals("java.lang.Long")){
						Long val = new Long("" + fileValue);
						Method method = thisClass.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), field.getType());
						method.invoke(pojo, val);
						return true;
					}else{
						Method method = thisClass.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), field.getType());
						method.invoke(pojo, fileValue);
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (null != fileValue) {//如果不是id 属性值不为空 就会执行 也是把属性值设置到属性中
				//System.out.println("SqlUtil.java_513_"+fieldName+"___SqlUtil.java___得到className___:"+fileValue);
				Method method = null;
				//System.out.println("........"+fileValue.getClass().getName());
				if (fileValue instanceof String) {
					method = thisClass.getMethod("set" +fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), String.class);
				}else if(fileValue instanceof Integer){
					method = thisClass.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), Integer.class);
				}else if(fileValue instanceof Long){
					method = thisClass.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), Long.class);
				}else if(fileValue instanceof Double){
					method = thisClass.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), Double.class);
				}else if(fileValue instanceof Short){
					method = thisClass.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), Short.class);
				}else if(fileValue instanceof Date){
					method = thisClass.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), Date.class);
					//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//5-2号做Query.java类时候修改
					fileValue = sdf.parse(((Date) fileValue).toLocaleString());
				}else if(fileValue instanceof Timestamp){
					//System.out.println("SqlUtil.java_532_当前属性为Timestamp");
					method = thisClass.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), Date.class);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss");
					fileValue = sdf.parse(fileValue.toString());
				}
				else{
					return false;
				}
				method.invoke(pojo, fileValue);
			}
			return true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//-----------------------------------------------------------
	
	/**
	 * 获取pojo类对应的表名
	 * @param pojoClazz pojo类的Class对象
	 * @return 表名
	 */
	public String getTableName(Class<? extends Pojo> pojoClazz){
		if(pojoClazz.isAnnotationPresent(TableName.class)){
			return pojoClazz.getAnnotation(TableName.class).name();
		}else{
			//String tName = toTableString(po.getSimpleName());
			return pojoClazz.getSimpleName();
		}
	}
	
	
	
	/**
	 * 调用getXxx方法获得返回值
	 * @param po 类的class
	 * @param fieldName  属性名
	 * @return 属性值
	 */
	public static<T> Serializable getFileValue(Class<T> po, String fieldName){
		try {
			Method method = po.getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
			Object o = po.newInstance();
			Object invoke = method.invoke(o);
			return null == invoke ? null : (Serializable)invoke;
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 调用getXxx方法获得返回值 重载
	 * @param pojo pojo的子类
	 * @param fieldName 属性名 
	 * @return 属性值
	 */
	public Serializable getFileValue(Pojo pojo, String fieldName){
		try {
			Class<? extends Pojo> cla = pojo.getClass();
			Method method = cla.getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
			Object o = pojo;
			Object invoke = method.invoke(o);
			return null == invoke ? null : (Serializable)invoke;
		}catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	
	/**
	 * 获取实体类的某个字段
	 * @param t     pojo 的 class对象
	 * @param fieldName  pojo 的属性名
	 * @return
	 */
	public Field getField(Class<?> t, String fieldName){
		Field[] fields = t.getDeclaredFields();
		for (Field field : fields) {
			
			if (field.getName().toUpperCase().equals(fieldName.toUpperCase())) {
				return field;
			}
		}
		return null;
	}
	/**
	 * 通过属性对象，获得该属性对应数据库的列名<Br>
	 * @param field 属性对象
	 * @return 当前属性对应的列名
	 * */
	public String getColumnNameByField(Field field){
		String columnName = null;
		if (!field.isAnnotationPresent(TempField.class)) {
			//判断是否是boolean类型
			if (field.isAnnotationPresent(ColumnName.class)) {
				columnName = field.getAnnotation(ColumnName.class).name();
			}else{
				columnName = toTableString(field.getName());
			}
		}
		return columnName;
	}
	
	
	/**
	 * 获取实体类对应的表名
	 * @param pojo pojo类
	 * @return  该pojo类对应的表名
	 * */
	public String getTableName(Pojo pojo){
		Class<? extends Pojo> c = pojo.getClass();
		if(c.isAnnotationPresent(TableName.class)){
			return c.getAnnotation(TableName.class).name();
		}else{
			String className = pojo.getClass().getSimpleName();
			String tName = toTableString(className);
			String poName = tName.substring(tName.length() - 2, tName.length());
			if("po".equals(poName)){
				tName = tName.substring(0,tName.length() - 3);
			}
			return tName;
		}
	}
	
	
	

	
	/**
	 * 驼峰标识转下划线标识<br>
	 * @param text 类中属性名  xxxXxxx
	 * @return String  xxx_xxxx
	 */
	public String toTableString(String text){
		String tName = text.substring(0, 1).toLowerCase();
		for(int i = 1; i < text.length(); i++){
			if(!Character.isLowerCase(text.charAt(i))){
				tName += "_";
			}
			tName += text.substring(i, i + 1);
		}
		//System.out.println(tName+"驼峰转换 : "+tName.toLowerCase());
		return tName.toLowerCase();
	}
	
	
	/**
	 * 辅助方法<br>
	 * 传入 目标类.class  和  目标类中某属性的类型.class<br>
	 * 返回目标类中该属性类型的field
	 * */
	public Field toFiedlByTargetClass(Class<? extends Pojo> entityClass,Class<? extends Pojo> otherClass){
		
		Field[] entityFields = entityClass.getDeclaredFields();
		for(Field entifyField :entityFields){
			Class<?> type = entifyField.getType();
			if(type.equals(Set.class) || type.equals(List.class)){
				Type genericType = entifyField.getGenericType();
				String typeString = null;
				if (ParameterizedType.class.isAssignableFrom(genericType.getClass())) {
					for (Type t1 : ((ParameterizedType) genericType).getActualTypeArguments()) {
						typeString = t1.toString();
					}
				}
				if(typeString!=null && typeString.equals(otherClass.toString())){
					return entifyField;
				}
			}
		}
		return  null;
	}
	
	/**
	 * 传入 entityClass 和 很多class <br>
	 * 通过传入的对象 构建 该类和其他类 表和表的关系
	 * 
	 * */
	public PrimaryForeign toTableFKOne(Class<? extends Pojo> entityClass,Class<? extends Pojo> otherFieldClass){
	
		
		//用来存放 属性和 该属性对应的类关系
		Map<Field, Class<? extends Pojo>> map = new HashMap<Field, Class<? extends Pojo>>();
		
		//得到当前类中包含 另一个类型的 field 属性对象
		Field entifyField = toFiedlByTargetClass(entityClass, otherFieldClass);
		
		
		PrimaryForeign pf = new PrimaryForeign();
		pf.setPrimaryTableName(getTableName(entityClass));
		pf.setPrimaryKey("id");
		pf.setForeignTableName(getTableName(otherFieldClass));
		
		if(entifyField.isAnnotationPresent(TempField.class)){//说明要去另一个类中查找这样的 表和表的关系
			// 获得 当前类  属性对应的列名
			String columnNameByField = getColumnNameByField(entifyField);
			Field[] otherFields = otherFieldClass.getDeclaredFields();
			for(Field othField:otherFields){
				
				Class<?> otherType = othField.getType();
				
				String otherTypeString = null;
				if(otherType.equals(Set.class) || otherType.equals(List.class)){
					Type othergenericType = othField.getGenericType();

					if (ParameterizedType.class.isAssignableFrom(othergenericType.getClass())) {
						for (Type t1 : ((ParameterizedType) othergenericType).getActualTypeArguments()) {
							//System.out.println("-=-=-"+t1.toString()+"-----"+otherClass.toString());
							otherTypeString = t1.toString();
						}
					}
				}
				if(otherTypeString!=null && otherTypeString.equals(entityClass.toString())){//另外的类中属性的类和当前类型一致
					if(othField.isAnnotationPresent(OneToOne.class)){
						//得到外键列
						pf.setForeignKey(othField.getAnnotation(OneToOne.class).foreignKeyColumn());
					}else if(othField.isAnnotationPresent(OneToMany.class)){
						pf.setForeignKey(othField.getAnnotation(OneToMany.class).foreignKeyColumn());
					}else if(othField.isAnnotationPresent(ManyToMany.class)){
						
						//多对多的关系
						ManyToMany mtm = othField.getAnnotation(ManyToMany.class);
						
						//桥表对象
						BridgeTable bt = new BridgeTable();
						
						//设置桥表名
						bt.setBridgeName(mtm.joinTableName());
						
						
						JoinColumn[] joinColumns = mtm.joinColumns();
						
						//设置桥表第一个列名
						bt.setOneforignKeyColumn(joinColumns[0].foreignKeyColumn());
						
						bt.setOneJoinColumn("id");
						
						bt.setOneJoinTableName(getTableName(joinColumns[0].joinPojo()));
						
						//设置桥表第二个列名
						bt.setTwoforignKeyColumn(joinColumns[1].foreignKeyColumn());
						bt.setTwoJoinColumn("id");
						bt.setTwoJoinTableName(getTableName(joinColumns[1].joinPojo()));
						pf.setBridgeTable(bt);
					}else{}
				}
			}
		}else{
			//
			Class<?> otherType = entifyField.getType();
			String otherTypeString = null;
			if(otherType.equals(Set.class) || otherType.equals(List.class)){
				Type othergenericType = entifyField.getGenericType();

				if (ParameterizedType.class.isAssignableFrom(othergenericType.getClass())) {
					for (Type t1 : ((ParameterizedType) othergenericType).getActualTypeArguments()) {
						//System.out.println("-=-=-"+t1.toString()+"-----"+otherClass.toString());
						otherTypeString = t1.toString();
					}
				}
			}
			//System.out.println("..."+otherTypeString+"---"+otherClass);
			
			if(otherTypeString!=null && otherTypeString.equals(otherFieldClass.toString())){//另外的类中属性的类和当前类型一致
				if(entifyField.isAnnotationPresent(OneToOne.class)){
					//得到外键列
					pf.setForeignKey(entifyField.getAnnotation(OneToOne.class).foreignKeyColumn());
				}else if(entifyField.isAnnotationPresent(OneToMany.class)){
					pf.setForeignKey(entifyField.getAnnotation(OneToMany.class).foreignKeyColumn());
				}else if(entifyField.isAnnotationPresent(ManyToMany.class)){
					
					//多对多的关系
					ManyToMany mtm = entifyField.getAnnotation(ManyToMany.class);
					
					//桥表对象
					BridgeTable bt = new BridgeTable();
					
					//设置桥表名
					bt.setBridgeName(mtm.joinTableName());
					
					
					JoinColumn[] joinColumns = mtm.joinColumns();
					
					//设置桥表第一个列名
					bt.setOneforignKeyColumn(joinColumns[0].foreignKeyColumn());
					System.out.println("1:"+joinColumns[0].foreignKeyColumn());
					bt.setOneJoinColumn("id");
					
					bt.setOneJoinTableName(getTableName(joinColumns[0].joinPojo()));
					System.out.println("1:"+getTableName(joinColumns[0].joinPojo()));
					
					//设置桥表第二个列名
					bt.setTwoforignKeyColumn(joinColumns[1].foreignKeyColumn());
					System.out.println("2:"+joinColumns[1].foreignKeyColumn());
					bt.setTwoJoinColumn("id");
					bt.setTwoJoinTableName(getTableName(joinColumns[1].joinPojo()));
					System.out.println("2:"+getTableName(joinColumns[1].joinPojo()));
					pf.setBridgeTable(bt);
				}else{}
			}
		}
		return pf;
	}
	
	/**
	 * 传入 entityClass 和 很多class <br>
	 * 通过传入的对象 构建 该类和其他类 表和表的关系
	 * 
	 * */
	public List<PrimaryForeign> toTableFKMany(Class<? extends Pojo> entityClass,Class<? extends Pojo>...tableNameClass){
		List<PrimaryForeign> listpf = new ArrayList<PrimaryForeign>();
		
		//用来存放 属性和 该属性对应的类关系
		Map<Field, Class<? extends Pojo>> map = new HashMap<Field, Class<? extends Pojo>>();
		
		//传入多个pojo对象的 集合
		List<Class<? extends Pojo>> list = Arrays.asList(tableNameClass);
		
		Field[] entityFields = entityClass.getDeclaredFields();
		for(Field entifyField :entityFields){
			Class<?> type = entifyField.getType();
			for(int i =0;i<list.size();i++){
				
				Class<? extends Pojo> otherClass = list.get(i);//另外的class
				
				String typeString = null;
				
				if(type.equals(Set.class) || type.equals(List.class)){
					Type genericType = entifyField.getGenericType();

					if (ParameterizedType.class.isAssignableFrom(genericType.getClass())) {
						for (Type t1 : ((ParameterizedType) genericType).getActualTypeArguments()) {
							//System.out.println("-=-=-"+t1.toString()+"-----"+otherClass.toString());
							typeString = t1.toString();
						}
					}
				}
				if(typeString!=null && typeString.equals(otherClass.toString())){//当前类 包含其他类对象 //进行封装操作
					
					PrimaryForeign pf = new PrimaryForeign();
					pf.setPrimaryTableName(getTableName(entityClass));
					pf.setPrimaryKey("id");
					pf.setForeignTableName(getTableName(otherClass));
					//map.put(entifyField, otherClass);//把数据添加到map中
					
					if(entifyField.isAnnotationPresent(TempField.class)){//说明要去另一个类中查找这样的 表和表的关系
						// 获得 当前类  属性对应的列名
						String columnNameByField = getColumnNameByField(entifyField);
						Field[] otherFields = otherClass.getDeclaredFields();
						for(Field othField:otherFields){
							
							Class<?> otherType = othField.getType();
							
							String otherTypeString = null;
							if(otherType.equals(Set.class) || otherType.equals(List.class)){
								Type othergenericType = othField.getGenericType();

								if (ParameterizedType.class.isAssignableFrom(othergenericType.getClass())) {
									for (Type t1 : ((ParameterizedType) othergenericType).getActualTypeArguments()) {
										//System.out.println("-=-=-"+t1.toString()+"-----"+otherClass.toString());
										otherTypeString = t1.toString();
									}
								}
							}
							if(otherTypeString!=null && otherTypeString.equals(entityClass.toString())){//另外的类中属性的类和当前类型一致
								if(othField.isAnnotationPresent(OneToOne.class)){
									//得到外键列
									pf.setForeignKey(othField.getAnnotation(OneToOne.class).foreignKeyColumn());
								}else if(othField.isAnnotationPresent(OneToMany.class)){
									pf.setForeignKey(othField.getAnnotation(OneToMany.class).foreignKeyColumn());
								}else if(othField.isAnnotationPresent(ManyToMany.class)){
									
									//多对多的关系
									ManyToMany mtm = othField.getAnnotation(ManyToMany.class);
									
									//桥表对象
									BridgeTable bt = new BridgeTable();
									
									//设置桥表名
									bt.setBridgeName(mtm.joinTableName());
									
									
									JoinColumn[] joinColumns = mtm.joinColumns();
									
									//设置桥表第一个列名
									bt.setOneforignKeyColumn(joinColumns[0].foreignKeyColumn());
									
									bt.setOneJoinColumn("id");
									
									bt.setOneJoinTableName(getTableName(joinColumns[0].joinPojo()));
									
									//设置桥表第二个列名
									bt.setTwoforignKeyColumn(joinColumns[1].foreignKeyColumn());
									bt.setTwoJoinColumn("id");
									bt.setTwoJoinTableName(getTableName(joinColumns[1].joinPojo()));
									pf.setBridgeTable(bt);
								}else{}
							}
						}
					}else{
						//测试没有通过。。。。接着这里写。
						Class<?> otherType = entifyField.getType();
						String otherTypeString = null;
						if(otherType.equals(Set.class) || otherType.equals(List.class)){
							Type othergenericType = entifyField.getGenericType();

							if (ParameterizedType.class.isAssignableFrom(othergenericType.getClass())) {
								for (Type t1 : ((ParameterizedType) othergenericType).getActualTypeArguments()) {
									//System.out.println("-=-=-"+t1.toString()+"-----"+otherClass.toString());
									otherTypeString = t1.toString();
								}
							}
						}
						//System.out.println("..."+otherTypeString+"---"+otherClass);
						
						if(otherTypeString!=null && otherTypeString.equals(otherClass.toString())){//另外的类中属性的类和当前类型一致
							if(entifyField.isAnnotationPresent(OneToOne.class)){
								//得到外键列
								pf.setForeignKey(entifyField.getAnnotation(OneToOne.class).foreignKeyColumn());
							}else if(entifyField.isAnnotationPresent(OneToMany.class)){
								pf.setForeignKey(entifyField.getAnnotation(OneToMany.class).foreignKeyColumn());
							}else if(entifyField.isAnnotationPresent(ManyToMany.class)){
								
								//多对多的关系
								ManyToMany mtm = entifyField.getAnnotation(ManyToMany.class);
								
								//桥表对象
								BridgeTable bt = new BridgeTable();
								
								//设置桥表名
								bt.setBridgeName(mtm.joinTableName());
								
								
								JoinColumn[] joinColumns = mtm.joinColumns();
								
								//设置桥表第一个列名
								bt.setOneforignKeyColumn(joinColumns[0].foreignKeyColumn());
								System.out.println("1:"+joinColumns[0].foreignKeyColumn());
								bt.setOneJoinColumn("id");
								
								bt.setOneJoinTableName(getTableName(joinColumns[0].joinPojo()));
								System.out.println("1:"+getTableName(joinColumns[0].joinPojo()));
								
								//设置桥表第二个列名
								bt.setTwoforignKeyColumn(joinColumns[1].foreignKeyColumn());
								System.out.println("2:"+joinColumns[1].foreignKeyColumn());
								bt.setTwoJoinColumn("id");
								bt.setTwoJoinTableName(getTableName(joinColumns[1].joinPojo()));
								System.out.println("2:"+getTableName(joinColumns[1].joinPojo()));
								pf.setBridgeTable(bt);
							}else{}
						}
					}
					//把得到的主键外键关系保存到集合中 用于返回
					listpf.add(pf);
				}
			}
		}
		return listpf;
	}
	/**
	 * 通过map得到对应的等值连接条件
	 * */
	public void toTableFk(Map<Field, Class<? extends Pojo>> map){
		Set<Field> keySet = map.keySet();
		for(Field key:keySet){
			Class<? extends Pojo> otherClass = map.get(key);
			//获得属性对应的列名
			String columnNameByField = getColumnNameByField(key);
			
			Field[] otherFields = otherClass.getDeclaredFields();
			for(Field othField:otherFields){
				
			}
			
		}
	}
	public static void main(String[] args) {
		SqlUtil sqlutil = new SqlUtil();
//		[PrimaryForeign [主表名:tbl_student, 主表主键名:id, 外键表名:tbl_course, 外键列名:null, 桥表信息: 桥表 [桥表名=c_s, 桥表列_1=course_id, 桥表列_1_引用列名=id, 桥表列_1_引用表名=tbl_course, 桥表列_2=student_id, 桥表列_2_引用列名=id, 桥表列_2_引用表名=tbl_student]]]

	//	System.out.println(sqlutil.toFiedlByTargetClass(Course.class, Student.class));
		
		List<PrimaryForeign> tableFK = sqlutil.toTableFKMany(Student.class, Course.class);
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * 获取查询sql的字段列表
	 * @param pojo pojo类对象
	 * @return 该pojo类属性的集合
	 * 
	 * 
	 */
	/*public List<Pram> getPramListOfSelectaaaa(Pojo pojo){
		List<Pram> list = new ArrayList<Pram>();
		Class<? extends Pojo> thisClass = pojo.getClass();
	    Field[] fields = thisClass.getDeclaredFields();
	    for(Field f : fields){
	    	try {
	    		if (!f.isAnnotationPresent(TempField.class)) {
	    			String fName = f.getName();
	    			//判断是否是boolean类型
	    			String get = "get";
	    			String fieldType = f.getGenericType().toString();
	    			if (fieldType.indexOf("boolean") != -1 || fieldType.indexOf("Boolean") != -1) {
	    				get = "is";
					}
	    			if (f.isAnnotationPresent(ColumnName.class)) {
	    				String fieldName = f.getAnnotation(ColumnName.class).name();
						Pram pram = new Pram(fieldName + " as " + fName, thisClass.getMethod(get + fName.substring(0, 1).toUpperCase() + fName.substring(1)).invoke(pojo));
						list.add(pram);
	    			}else{
	    				String fieldName = toTableString(fName);
	    				Pram pram = new Pram(fieldName + " as " + fName, thisClass.getMethod(get + fName.substring(0, 1).toUpperCase() + fName.substring(1)).invoke(pojo));
						list.add(pram);
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}	
	    }
		return list;
	}*/
	
	
	
	
	
	
	

	/*public List<Pram> getPramList(T po){
		List<Pram> list = new ArrayList<>();
		Class<? extends Pojo> thisClass = po.getClass();
	    Field[] fields = thisClass.getDeclaredFields();
	    	try {
	    		for(Field f : fields){
		    		if(!f.getName().equalsIgnoreCase("ID") && !f.isAnnotationPresent(TempField.class)){
		    			String fName = f.getName();
		    			
		    			//判断是否是boolean类型
		    			String getf = "get";
		    			String fieldType = f.getGenericType().toString();
		    			if (fieldType.indexOf("boolean") != -1 || fieldType.indexOf("Boolean") != -1) {
		    				getf = "is";
						}
		    			if (f.isAnnotationPresent(ColumnName.class)) {
		    				String fieldName = f.getAnnotation(ColumnName.class).name();
		    				Method get = thisClass.getMethod(getf + fName.substring(0, 1).toUpperCase() + fName.substring(1));
		    				Object getValue = get.invoke(po);
		    				Pram pram = new Pram(fieldName, getValue);
							list.add(pram);
		    			}else{
		    				String fieldName = toTableString(fName);
		    				Method get = thisClass.getMethod(getf + fName.substring(0, 1).toUpperCase() + fName.substring(1));
		    				Object getValue = get.invoke(po);
		    				Pram pram = new Pram(fieldName , getValue);
							list.add(pram);
						}
		    		}
	    		}
			}  catch (Exception e) {
				e.printStackTrace();
			}
		return list;
	}*/
	
	
	
	
	/*
	 * 辅助方法
	 * @param thisClass 传入pojo类对象.class
	 * @param f     当前的属性 Field
	 * @param getf  当前属性 对应的get方法
	 * @param fName 当前属性名
	 * @param pojo  当前对象
	 * @param fieldType 当前属性父类toString
	 * @param list 最终需要返回的list
	 * */
	/*public static List<Pram> createListParam(Class<? extends Pojo> thisClass,Field f,String getf,String fName,Pojo pojo,String fieldType,List<Pram> list) {
		try {
			//获得添加@ColnumName 注解的 列名
			String fieldName = f.getAnnotation(ColumnName.class).name();
			//判断当前类型是否是 pojo的子类型
			Object isPojo = GenericsUtils.isPojo(f);
			if(isPojo!=null){//是子类型
				Method getPojo = thisClass.getMethod(getf + fName.substring(0, 1).toUpperCase() + fName.substring(1));
				//调用了pojo类的get方法/is方法获得返回值getValue
				Object getObject = getPojo.invoke(pojo);//得到对象
				Class<? extends Object> isPojoClass = getObject.getClass();
				Method getIdmethod = isPojoClass.getMethod("getId");
				Object getValue = getIdmethod.invoke(getObject);
				Pram pram = new Pram(fieldName, getValue);
				list.add(pram);
			}else{//不是子类型
				Method get = thisClass.getMethod(getf + fName.substring(0, 1).toUpperCase() + fName.substring(1));
				//调用了pojo类的get方法/is方法获得返回值getValue
				Object getValue = get.invoke(pojo);
				if(fieldType.indexOf("Date") != -1){//如果是date类型 
					Date date = (Date) getValue;
					
					Pram pram = new Pram(fieldName,"date:"+date.toLocaleString());
					list.add(pram);
				}else{//普通类型
					Pram pram = new Pram(fieldName, getValue);
					list.add(pram);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}*/
	
	
	
	
	/*
	 类.class 获得表名
	  public String getTableNameByClazzaaaa(Class<? extends Pojo> pojo) {
		if(pojo.isAnnotationPresent(TableName.class)){
			return pojo.getAnnotation(TableName.class).name();
		}else{
			String tName = toTableString(pojo.getSimpleName());
			if("po".equals(tName.substring(tName.length() - 3, tName.length() - 1))){
				tName = tName.substring(0,tName.length() - 3);
			}
			return tName;
		}
	}*/
	
	
	/*
	 * 通过Class获取生成对应Sql查询的字段
	 * @param po
	 * @return
	 */
	/*public List<Pram> getPramListByBean(Class<T> po){
		List<Pram> list = new ArrayList<Pram>();
		Class<?> thisClass = po;
	    Field[] fields = thisClass.getDeclaredFields();
	    try {
    		Object o = thisClass.newInstance();
    		for(Field f : fields){
	    		if(!f.getName().equalsIgnoreCase("ID") && !f.isAnnotationPresent(TempField.class)){
	    			
	    			String fName = f.getName();
	    			
	    			//判断是否是boolean类型
	    			String getf = "get";
	    			String fieldType = f.getGenericType().toString();
	    			if (fieldType.indexOf("boolean") != -1 || fieldType.indexOf("Boolean") != -1) {
	    				getf = "is";
					}
	    			if (f.isAnnotationPresent(ColumnName.class)) {
	    				String fieldName = f.getAnnotation(ColumnName.class).name();
	    				Method get = thisClass.getMethod(getf + fName.substring(0, 1).toUpperCase() + fName.substring(1));
	    				Object getValue = get.invoke(o);
	    				Pram pram = new Pram(fieldName + " as " + fName, getValue);
						list.add(pram);
	    			}else{
	    				String fieldName = toTableString(fName);
	    				Method get = thisClass.getMethod(getf + fName.substring(0, 1).toUpperCase() + fName.substring(1));
	    				Object getValue = get.invoke(o);
	    				Pram pram = new Pram(fieldName + " as " + fName, getValue);
						list.add(pram);
					}
	    			
	    		}
    		}
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}*/
	
	/*
	 * 通过Class获取生成对应Sql查询的字段
	 * @param po
	 * @return
	 */
	/*public List<Pram> getPramListByBeanOfSelect(Class<T> po){
		List<Pram> list = new ArrayList<Pram>();
		Class<?> thisClass = po;
	    Field[] fields = thisClass.getDeclaredFields();
	    try {
    		Object o = thisClass.newInstance();
    		for(Field f : fields){
    			if (!f.isAnnotationPresent(TempField.class)) {
    				String fName = f.getName();
    				//判断是否是boolean类型
	    			String getf = "get";
	    			String fieldType = f.getGenericType().toString();
	    			if (fieldType.indexOf("boolean") != -1 || fieldType.indexOf("Boolean") != -1) {
	    				getf = "is";
					}
	    			if (f.isAnnotationPresent(ColumnName.class)) {
	    				String fieldName = f.getAnnotation(ColumnName.class).name();
	    				Method get = thisClass.getMethod(getf + fName.substring(0, 1).toUpperCase() + fName.substring(1));
	    				Object getValue = get.invoke(o);
	    				Pram pram = new Pram(fieldName + " as " + fName, getValue);
						list.add(pram);
	    			}else{
	    				String fieldName = toTableString(fName);
	    				Method get = thisClass.getMethod(getf + fName.substring(0, 1).toUpperCase() + fName.substring(1));
	    				Object getValue = get.invoke(o);
	    				Pram pram = new Pram(fieldName + " as " + fName, getValue);
						list.add(pram);
					}
				}
    		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}*/
	
	/*
	 * 通过Class获取生成对应Sql字段
	 * @param po pojo类的class对象
	 * @return 该pojo的属性集合
	 * 
	 * 
	 *  baseDaoImpl.java_构造器_79 得到的查询sqlParms  :
	   
	    [Pram [field=name, value=null]
		, Pram [field=price, value=null]
		, Pram [field=publish_address, value=null]
		, Pram [field=dob, value=null]
		, Pram [field=num, value=null]
     *
	 */
	/*public List<Pram> getPramList(Class<T> po){
		List<Pram> list = new ArrayList<Pram>();
		Class<? extends Pojo> thisClass = po;
	    Field[] fields = thisClass.getDeclaredFields();
	    	try {
	    		Object o = thisClass.newInstance();
	    		for(Field f : fields){
		    		if(!f.getName().equalsIgnoreCase("ID") && !f.isAnnotationPresent(TempField.class)){
		    			String fName = f.getName();
		    			//判断是否是boolean类型
		    			String getf = "get";
		    			String fieldType = f.getGenericType().toString();
		    			if (fieldType.indexOf("boolean") != -1 || fieldType.indexOf("Boolean") != -1) {
		    				getf = "is";
						}
		    			if (f.isAnnotationPresent(ColumnName.class)) {
		    				String fieldName = f.getAnnotation(ColumnName.class).name();
		    				Method get = thisClass.getMethod(getf + fName.substring(0, 1).toUpperCase() + fName.substring(1));
		    				Object getValue = get.invoke(o);
		    				Pram pram = new Pram(fieldName, getValue);
							list.add(pram);
		    			}else{
		    				String fieldName = toTableString(fName);
		    				Method get = thisClass.getMethod(getf + fName.substring(0, 1).toUpperCase() + fName.substring(1));
		    				Object getValue = get.invoke(o);
		    				Pram pram = new Pram(fieldName , getValue);
							list.add(pram);
						}
		    		}
	    		}
			} catch (Exception e) {
				e.printStackTrace();
			}
		return list;
	}*/
	/*
	 * 获取Sql字段名
	 * @param po
	 * @return
	 */
	/*public String getTableNameByBean(Class<? extends Pojo> po){
		if(po.isAnnotationPresent(TableName.class)){
			return po.getAnnotation(TableName.class).name();
		}else{
			String tName = toTableString(po.getSimpleName());
			if("po".equals(tName.substring(tName.length() - 3, tName.length() - 1))){
				tName = tName.substring(0,tName.length() - 3);
			}
			return tName;
		}
	}*/
	
}
