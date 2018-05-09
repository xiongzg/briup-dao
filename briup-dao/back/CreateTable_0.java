package com.briup.base.jdbc.util;

import static com.briup.base.jdbc.sqldata.SqlStaticData.createAddForeignKeySql;
import static com.briup.base.jdbc.sqldata.SqlStaticData.createDropConstraintSql;
import static com.briup.base.jdbc.sqldata.SqlStaticData.createTableConstraintSql;
import static com.briup.base.jdbc.sqldata.SqlStaticData.createTableSql;
import static com.briup.base.jdbc.sqldata.SqlStaticData.createUnionPrimaryKey;
import static com.briup.base.jdbc.sqldata.SqlStaticData.createUnique;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
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
import com.briup.base.jdbc.bean.JavaType;
import com.briup.base.jdbc.bean.Pojo;
import com.briup.base.jdbc.bean.Table;
/**
 * 自动建表代码<br>
 * 完成覆盖<br>
 * 
 * */
public class CreateTable {
	
	/**
	 * 级联删除
	 * */
	private Boolean onDeleteCascade = false;
	

	
	/**
	 * 级联删除设置为null
	 * */
	private Boolean onDeleteSetNull = false;
	
	/**
	 * @param beanFileQualifiedName pojo.java文件的src全限定名
	 * @param connection 连接对象
	 * @param username   连接数据库 用户名  
	 * @param append     是否覆盖表   true : 覆盖    false : 不覆盖 
	 * <li>注 : 覆盖 为true 只能操作没有外键关联的表
	 * <hr>
	 *  CreateTable create = new CreateTable();<br>
	 *	create.create("src/main/java/com/briup/bean/Book.java",connection,"连数据库的用户名",true);<br>
	 * */
	public void create(String beanFileQualifiedName,Connection connection,String username,boolean append){
		
		if(onDeleteCascade && onDeleteSetNull){
			System.err.println("Base项目:建表模块...错误...baseDao.properties文件中onDeleteCascade 和 onDeleteSetNull 不能同时为true");
			return;
		}
		
		//用于存放建表对象,每个对象就是一张表
		List<Table> tables = new ArrayList<Table>();
		//用于存放 执行添加外键关系的sql
		Set<String> foreignKeyList = new HashSet<String>();
		
		//用于存放删除外键关系的sql
		Set<String> deleteForeignKeyList = new HashSet<String>(); 
		
		//用于存放删除表的sql
		//List<String> deleteTableList = new ArrayList<String>(); 
		
		try {
			
			Statement statement = connection.createStatement();
			
			File fileroot = new File("");
			//当前项目的根目录 : F:\Briup\Briup_Work_space\sxdx\workspace\jd1802
			String path = fileroot.getCanonicalPath() ; 
			//F:\Briup\Briup_Work_space\sxdx\workspace\mybatis_test\src\main\java\com\briup\xzg\base\mybatis_test\bean\Book.java
			File filepojo = new File(path,beanFileQualifiedName);
			//System.out.println("pojo类 的 路径  : "+filepojopath);
			//父目录
			String parent = filepojo.getParent();
			File fileParent = new File(parent);
			boolean directory = fileParent.isDirectory();
			if(directory){
				//获取bean文件夹下所有的文件
				File[] listFiles = fileParent.listFiles();
				for(File childFile : listFiles){
					//获取了当前包下 每个类的全限定名
					String pojoQualified = toQualifiedNameByFile(childFile);
					//System.out.println("类的全限定名 : "+pojoQualified);
					Class<?> forName = Class.forName(pojoQualified);
					String tableName =null;
					if(forName.isAnnotationPresent(TableName.class)){
						//获得@TableName上表名
						tableName = forName.getAnnotation(TableName.class).name();
						//System.out.println(tableName);
					}else{
						tableName = forName.getSimpleName().toLowerCase();
					}
					
					//获得该表下所有的外键关联关系
					//如果有外键关联关系  就把对应的删除外键的 sql 保存到 deleteForeignKeyList
					//System.out.println("得到的表名 : "+tableName);
					
					
					if(append){
						//查询外键的sql
						String fksql = createTableConstraintSql(tableName);
						ResultSet fkset = statement.executeQuery(fksql);
						while(fkset.next()){
							String constraint_name = fkset.getString("CONSTRAINT_NAME");
							String deleteContraintSql = createDropConstraintSql(tableName, constraint_name);
							deleteForeignKeyList.add(deleteContraintSql);
						}
					}
				
					
					DatabaseMetaData metaData = connection.getMetaData();
					ResultSet tableset = metaData.getTables(null, username.toUpperCase(),tableName.toUpperCase(), new String[]{"TABLE"});
					//false     存在 true  ---> 跳过
					//false    不存在 false ---> 执行
					//true     存在 true   ---> 执行
					//true    不存在 false ---> 执行
					
					if(append==false && tableset.next()==true){//有下一个 为true 
						continue;
					}
					
					
					Table table = new Table();
					table.setTableName(tableName);
					//用于存放当前表的 列 和数据类型
					Map<String, JavaType> columnMap = new HashMap<String, JavaType>();
					table.setColumn(columnMap);
					//当前类所有的属性
					Field[] declaredFields = forName.getDeclaredFields();
					for(Field field:declaredFields){//变量pojo类的属性
						//System.out.println("当前操作的属性:"+field);
						
						if (!field.isAnnotationPresent(TempField.class)) {
							//列名
							String columnName  = null;
			    			if (field.isAnnotationPresent(ColumnName.class)) {
			    				columnName= field.getAnnotation(ColumnName.class).name();
			    			}else{
			    				//列名
			    				columnName = toTableString(field.getName());
			    			}
			    			
			    			//如果包含@OneToOne注解
			    			if(field.isAnnotationPresent(OneToOne.class)){
			    				
			    				
			    				OneToOne oneToOne = field.getAnnotation(OneToOne.class);
			    				//添加外键, 外键唯一 
			    				columnName= oneToOne.foreignKeyColumn();
			    				Class<? extends Pojo> reference = oneToOne.reference();
			    				//引用表的名字
			    				String referenceTableName = null;
			    				//是否包含@TableName注解
			    				if(reference.isAnnotationPresent(TableName.class)){
			    					referenceTableName = reference.getAnnotation(TableName.class).name();
			    				}else{
			    					referenceTableName = reference.getSimpleName();
			    				}
			    				
			    				String alterAddForeign = createAddForeignKeySql(tableName, columnName, referenceTableName, "id");
			    				
			    				
			    				foreignKeyList.add(alterAddForeign);
			    				
			    				
			    				
			    				//外键唯一
			    				//String fkunique = "alter table "+tableName+" add constraint "+tableName+"_"+columnName+"_unique unique("+columnName+")";
			    				String fkunique = createUnique(tableName, columnName);
			    				foreignKeyList.add(fkunique);
			    				
			    				//一对一 完成
			    			}else if(field.isAnnotationPresent(OneToMany.class)){
			    				//添加外键
			    				
			    				OneToMany oneToMany = field.getAnnotation(OneToMany.class);
			    				//添加外键, 外键唯一 
			    				columnName= oneToMany.foreignKeyColumn();
			    				Class<? extends Pojo> reference = oneToMany.reference();
			    				//引用表的名字
			    				String referenceTableName = null;
			    				//是否包含@TableName注解
			    				if(reference.isAnnotationPresent(TableName.class)){
			    					referenceTableName = reference.getAnnotation(TableName.class).name();
			    				}else{
			    					referenceTableName = reference.getSimpleName();
			    				}
			    				String alterAddForeign = createAddForeignKeySql(tableName, columnName, referenceTableName, "id");
			    				foreignKeyList.add(alterAddForeign);
			    				
			    			}else if(field.isAnnotationPresent(ManyToMany.class)){
			    				
			    				
			    				
			    				//添加桥表
			    				//使用联合主键
			    				//两个字段分别是两张表的外键列
			    				ManyToMany mtm = field.getAnnotation(ManyToMany.class);
			    				//桥表名
			    				String joinTableName = mtm.joinTableName();
			    				//System.out.println("桥表名: "+joinTableName);
			    				JoinColumn[] joinColumns = mtm.joinColumns();
			    				JoinColumn joinColumn1 = joinColumns[0];
			    				JoinColumn joinColumn2 = joinColumns[1];
			    				//桥表 列1
			    				String colmun1 = joinColumn1.foreignKeyColumn();
			    				//桥表列2
			    				String colmun2 = joinColumn2.foreignKeyColumn();
			    				//System.out.println("桥表列 1 ："+colmun1);
			    				//System.out.println("桥表列 2 ："+colmun2);
			    				//创建桥表的sql
			    				Table tableBridge = new Table();
			    				tableBridge.setTableName(joinTableName);
								Map<String, JavaType> columnMapBridge= new HashMap<String, JavaType>();
								columnMapBridge.put(colmun2, JavaType.Long);
								columnMapBridge.put(colmun1, JavaType.Long);
								tableBridge.setColumn(columnMapBridge);
								//建立桥表完成
								tables.add(tableBridge);	
								
								//给桥表添加联合主键
								//String bridgeWPK = "alter table "+joinTableName+" add(primary key("+colmun1+","+colmun2+"))";
								//System.out.println("联合主键: "+bridgeWPK);
								String bridgeWPK = createUnionPrimaryKey(joinTableName, colmun1, colmun2);
								foreignKeyList.add(bridgeWPK);
								
								
								
								
								
								
								//创建x_y.java文件
								
								/*   4_26 想到 on delete cascade 去除
								String msg = tableBridge.getTableName();
								String bigClassName = msg.replaceFirst(""+msg.charAt(0),(""+msg.charAt(0)).toUpperCase() );//桥表类名
								
								//	包名
								String packageName = pojoQualified.substring(0,pojoQualified.lastIndexOf("."));
								System.out.println("获得包名 : "+packageName);
								
								//4.25 想到 动态生成一个桥表类
								//桥表对应的pojo类
			    				File birFile = new File(parent,bigClassName+".java");
			    				FileWriter wf = new FileWriter(birFile);
			    				wf.append("package "+packageName+";\r\n");
			    				wf.append("\r\n");
			    				wf.append("import com.briup.base.mybatis.annocation.ColumnName;\r\n");
			    				wf.append("import com.briup.base.mybatis.annocation.TableName;\r\n");
			    				wf.append("import com.briup.base.mybatis.bean.Pojo;\r\n\r\n");
			    				wf.append("@TableName(name=\""+bigClassName.toLowerCase()+"\")\r\n");
			    				wf.append("public class "+bigClassName+"  extends Pojo{\r\n\r\n");
			    					wf.append("\t@ColumnName(name=\""+colmun1+"\")\r\n");
			    					wf.append("\tprivate Long "+colmun1+";  \r\n\r\n");
			    					wf.append("\t@ColumnName(name=\""+colmun2+"\")\r\n");
			    					wf.append("\tprivate Long "+colmun2+";  \r\n\r\n");
			    					
			    					//get方法
			    					wf.append("\tpublic Long get"+colmun1.replaceFirst(""+colmun1.charAt(0),(""+colmun1.charAt(0)).toUpperCase() )+"() {\r\n");
			    					wf.append("\t\treturn "+colmun1+";\r\n\t}\r\n");
			    					
			    					wf.append("\tpublic Long get"+colmun2.replaceFirst(""+colmun2.charAt(0),(""+colmun2.charAt(0)).toUpperCase() )+"() {\r\n");
			    					wf.append("\t\treturn "+colmun2+";\r\n\t}\r\n");
			    					
			    					//set方法
			    					wf.append("\tpublic void set"+colmun1.replaceFirst(""+colmun1.charAt(0),(""+colmun1.charAt(0)).toUpperCase() )+"(Long "+colmun1+") {\r\n");
			    					wf.append("\t\tthis."+colmun1+" = "+colmun1+";\r\n\t}\r\n");
			    					wf.append("\tpublic void set"+colmun2.replaceFirst(""+colmun2.charAt(0),(""+colmun2.charAt(0)).toUpperCase() )+"(Long "+colmun2+") {\r\n");
			    					wf.append("\t\tthis."+colmun2+" = "+colmun2+";\r\n\t}\r\n");
			    					
			    				wf.append("}");
			    				wf.flush();
			    				wf.close(); 4_26 想到 on delete cascade 去除  end */
			    				
			    				
			    				
			    				
			    				
								
								//给桥表的两个列添加外键关联
								// "alter table "+joinTableName+" add()";
								
								//4-11 多对多级联删除 完成不了 去除 多对多是外键关系start
								Class<? extends Pojo> reference1 = joinColumn1.joinPojo();
								String referenceTableName1 = null;
			    				//是否包含@TableName注解
			    				if(reference1.isAnnotationPresent(TableName.class)){
			    					referenceTableName1 = reference1.getAnnotation(TableName.class).name();
			    				}else{
			    					referenceTableName1 = reference1.getSimpleName();
			    				}
			    				Class<? extends Pojo> reference2 = joinColumn2.joinPojo();
			    				String referenceTableName2 = null;
			    				//是否包含@TableName注解
			    				if(reference2.isAnnotationPresent(TableName.class)){
			    					referenceTableName2 = reference2.getAnnotation(TableName.class).name();
			    				}else{
			    					referenceTableName2 = reference2.getSimpleName();
			    				}
								 String fk_refenence_1 =createAddForeignKeySql(joinTableName, colmun1, referenceTableName1, joinColumn1.joinColumn());
								 String fk_refenence_2 =createAddForeignKeySql(joinTableName, colmun2, referenceTableName2, joinColumn2.joinColumn());
								
								 //删除联合主键的sql
								 //String delete_fk_refenence_1 ="alter table "+joinTableName+" drop constraint "+joinTableName+"_"+colmun1+"_fk";
								 //String delete_fk_refenence_2 ="alter table "+joinTableName+" drop constraint "+joinTableName+"_"+colmun2+"_fk";
								 
								 if(append){
										String fksql = createTableConstraintSql(tableName);										
										ResultSet fkset = statement.executeQuery(fksql);
										while(fkset.next()){
											String constraint_name = fkset.getString("CONSTRAINT_NAME");
											String delete_fk_refenence = createDropConstraintSql(tableName, constraint_name);
											
											deleteForeignKeyList.add(delete_fk_refenence);
										}
									}
								 
								// deleteForeignKeyList.add(delete_fk_refenence_1);
								// deleteForeignKeyList.add(delete_fk_refenence_2);
								 
								 
								 foreignKeyList.add(fk_refenence_1);
								 foreignKeyList.add(fk_refenence_2);//4-11 多对多级联删除 完成不了 去除 多对多是外键关系end
								 continue;
			    			}else{
			    				//System.out.println("else");
			    			}
			    			String fieldType = field.getGenericType().toString();
			    			//全限定名
			    			if(fieldType.indexOf("String")!=-1){
			    				columnMap.put(columnName, JavaType.String);
			    			}else if(fieldType.indexOf("Date")!=-1){
			    				columnMap.put(columnName, JavaType.Date);
			    			}else if(fieldType.indexOf("Boolean")!=-1){
			    				columnMap.put(columnName, JavaType.Boolean);
			    			}else{
			    				columnMap.put(columnName, JavaType.Long);
			    			}
						}
					}
					//添加到集合 table中  
					tables.add(table);	
				}
				
				System.out.println("------------------------------");
				for(String fkSql : deleteForeignKeyList){
					System.out.println(fkSql);
				}
				
				System.out.println("------------------------------");
				
				//如果覆盖 先删除所有的 表外键
				if(append){
					for(String fksql : deleteForeignKeyList){
						System.out.println("Base项目\t删除外键 :"+fksql);
						statement.execute(fksql);
					}
				}
				//使用Table集合创建表 获得sql集合
				//获得所有
				Map<String,String> sqls = getSqlByTables(tables);
				Set<String> keySet = sqls.keySet();
				for(String tableName : keySet){
					//获得建表的sql语句
					String sql = sqls.get(tableName);
					DatabaseMetaData metaData = connection.getMetaData();
					ResultSet tableset = metaData.getTables(null, username.toUpperCase(),tableName.toUpperCase(), new String[]{"TABLE"});
					
					if(tableset.next()){//重新建立表
						//..........先删除外键
						if(append){
							System.out.println("Base项目\t重新建表 : "+sql);
							statement.execute("drop table "+tableName);
							statement.execute(sql);
						}
					}else{//表在数据库不存在 建立新表
						System.out.println("Base项目\t自动建表 : "+sql);
						statement.execute(sql);
					}
				}//建立所有表 完成....end
				//开始建立 表和表的关系
				for(String fksql:foreignKeyList){
					System.out.println("Base项目\t建立外键关联: "+fksql);
					statement.execute(fksql);//----------------
				}
				
				statement.close();
				connection.close();
			}else{
				throw new Exception("不是一个文件夹");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 辅助方法 : 使用Table集合创建表 获得sql集合<br>
	 * @return key 表名   value 建表的sql
	 * */
	public Map<String,String> getSqlByTables(List<Table> tables){
		Map<String, String> map = new HashMap<String, String>();
		for(Table t:tables){
			Map<String, JavaType> column = t.getColumn();
			map.put(t.getTableName(), createTableSql(t.getTableName(), column));
		}
		return map;
	}
	
	/*public Map<String,String> getSqlByTables(List<Table> tables){
		Map<String, String> map = new HashMap<String, String>();
		for(Table t:tables){
			StringBuffer sb = new StringBuffer("create table "+t.getTableName()+"( ");
			Map<String, JavaType> column = t.getColumn();
			Set<String> keySet = column.keySet();
			for(String key:keySet){//字段名
				//数据类型
				JavaType value = column.get(key);
				sb.append(key).append(" ").append(value);
				if("id".equals(key.toLowerCase())){
					sb.append(" primary key, ");
				}else{
					sb.append(",");
				}
			}
			String string = sb.toString();
			String createTableSql = string.substring(0,string.length()-1)+")";
			map.put(t.getTableName(), createTableSql);
		}
		return map;
	}*/
	
	/**
	 * 辅助方法 ： 传入一个文件 把这个文件的文件名路径 变成 全限定名返回<br>
	 * egF:\Briup\Briup_Work_space\sxdx\workspace\mybatis_test\src\main\java\com\briup\xzg\base\mybatis_test\bean\Book.java<br>
		 return com.briup.xzg.base.mybatis_test.bean.Book
	 * */
	public String toQualifiedNameByFile(File pojoFile){
		String filepojopath = pojoFile.getPath();
		//获得src路径 : src\main\java\com\briup\xzg\base\mybatis_test\bean\Book.java
		String srcPath = filepojopath.substring(filepojopath.indexOf("src"),filepojopath.length()-5);
		
		String replace = srcPath.replace("\\", "=");
		String[] split = replace.split("=");
		//[src, main, java, com, briup, xzg, base, mybatis_test, bean, Book]
		
		//控制 切割的数组是否进行 拼接
		boolean flagAdd =false;
		StringBuffer sb = new StringBuffer();
		for(int i = 1;i<split.length;i++){
			String string = split[i];
			
			if("com".equals(string)){
				flagAdd = true;
			}
			if(flagAdd){
				sb.append(string).append(".");
			}
		}
		//获得pojo类的全限定名 : com.briup.xzg.base.mybatis_test.bean.Book
		String pojoQualifiedName = sb.substring(0, sb.length()-1);
		return pojoQualifiedName;
	}
	public static void maina(String[] args) throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE", "tom", "tom");
		
		CreateTable t = new CreateTable();
		t.create("src/main/java/com/briup/xzg/base/mybatis_test/bean/Book.java",connection,"tom",true);
		
	}
	/*@Test
	public void test(){
		
		String msg = "a\\b\\c";
		String replace = msg.replace("\\", "=");
		String[] split = replace.split("=");
		System.out.println(Arrays.toString(split));
		
	}*/
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
	
	public Boolean getOnDeleteCascade() {
		return onDeleteCascade;
	}

	public void setOnDeleteCascade(String onDeleteCascade) {
		this.onDeleteCascade = Boolean.parseBoolean(onDeleteCascade);
	}

	public Boolean getOnDeleteSetNull() {
		return onDeleteSetNull;
	}
	
	public void setOnDeleteSetNull(String onDeleteSetNull) {
		this.onDeleteSetNull = Boolean.parseBoolean(onDeleteSetNull);
	}
}
