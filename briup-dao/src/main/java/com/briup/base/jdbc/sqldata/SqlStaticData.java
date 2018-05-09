package com.briup.base.jdbc.sqldata;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.briup.base.jdbc.bean.BridgeTable;
import com.briup.base.jdbc.bean.JavaType;
import com.briup.base.jdbc.bean.Pojo;
import com.briup.base.jdbc.bean.Pram;
import com.briup.base.jdbc.bean.PrimaryForeign;
import com.briup.base.jdbc.bean.Where;
import com.briup.base.jdbc.util.Create;
import com.briup.base.jdbc.util.SqlUtil;

/**
 * 存放一些 关于数据库的语法sql<br>
 * */
public class SqlStaticData {
	/**
	 * oracle查询主键的序列
	 * */
	private static String ID_SEQUENCE;
	
	/**
	 * 删除表
	 * */
	private static String DROP_TABLE;
	/**
	 * 创建表前
	 * */
	private static  String CREATE_TABLE_BEFORE;
	/**
	 * 创建表中
	 * */
	private static  String CREATE_TABLE_CENTRE;
	/**
	 * 创建表后
	 * */
	private static  String CREATE_TABLE_AFTER;
	/**
	 * 主键自动增长
	 * */
	public static String CREATE_TABLE_AUTO_INCREMENT;
	/**
	 * 添加表的主键约束条件
	 * */
	private static  String ALTER_TABLE_PRIMARY_KEY;
	
	/**
	 * 添加表的联合主键
	 * */
	public static String ALTER_TABLE_UNION_PRIMARY_KEY;
	
	/**
	 * 添加表的外键约束条件
	 * ${tableName} 表名
	 * ${forekeyColumn} 当前表外键列名
	 * ${referenceTable}  引用表名
	 * ${referenceTableColumn}  引用表中的列名
	 * */
	private static String ALTER_TABLE_FOREIGN_KEY;
	/**
	 * 添加外键唯一约束
	 * */
	private static String ALTER_UNIQUE;
	/**
	 * 查询当前表的约束名
	 * */
	private static String SELECT_CONSTRAINT;
	/**
	 * 删除约束
	 * */
	private static String DROP_CONSTRAINT;
	
	
	/**
	 * 得到下一个主键sql
	 * */
	private static String PRIMARY_KEY_NEXT_VAL;
	
	/**
	 * on delete set null<br>或者<br>
	 * on delete cascade<br>
	 * */
	private static String ON_DELETE;
	
	
	/**
	 * 查询
	 * */
	private static String SELECT;
	/**
	 * 添加
	 * */
	private static String INSERT;
	/**
	 * 修改
	 * */
	private static String UPDATE;
	/**
	 * 删除
	 * */
	private static String DELETE;
	
	/**
	 * 日期函数
	 * */
	private static String DATE;
	
	/**
	 * 查询表总数sql
	 * */
	private static String COUNT;
	/**
	 * 数据类型
	 * */
	private static Map<String,String> jdbcTypeMap  = new HashMap<String,String>();
	
	/**
	 * 分页的sql
	 * */
	private static String PAGE;
	
	
	static{
		try {
			Properties properties = new Properties();
			String path = Create.class.getClassLoader().getResource("").getPath();
			File file = new File(path, "baseDao.properties");
			FileInputStream fis;
			fis = new FileInputStream(file);
			properties.load(fis);
			properties.clone();
			fis.close();
		
			//数据库小写
			String dbname = properties.getProperty("dbname").toLowerCase().trim();
			
			//onDeleteCascade
			Boolean onDeleteCascade = Boolean.parseBoolean(properties.getProperty("onDeleteCascade"));
			Boolean onDeleteSetNull = Boolean.parseBoolean(properties.getProperty("onDeleteSetNull"));
			
			
			if(onDeleteCascade && onDeleteSetNull){
				System.err.println("Base项目:建表模块...错误...baseDao.properties文件中onDeleteCascade 和 onDeleteSetNull 不能同时为true");
				System.exit(0);//jvm停止
			}else if(onDeleteCascade){
				ON_DELETE = " on delete cascade";
			}else if(onDeleteSetNull){
				ON_DELETE = " on delete set null";
			}else{
				ON_DELETE = "";
			}
			
			//onDeleteSetNull
			
			//调用初始化方法 对sql进行初始化
			if("oracle".equals(dbname)){
				ID_SEQUENCE = properties.getProperty("idsequence");
				oracle();
			}else if("mysql".equals(dbname)){
				mysql();
			}else{
				throw new Exception("当前BaseDao不支持该数据库:"+dbname);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void mysql() throws Exception{
		DROP_TABLE="DROP TABLE IF EXISTS `${tableName}`";
		CREATE_TABLE_BEFORE="CREATE TABLE `${tableName}`( ";
		CREATE_TABLE_CENTRE="`${columnName}` ${columnType},";
		CREATE_TABLE_AFTER=") ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8";
		CREATE_TABLE_AUTO_INCREMENT = " AUTO_INCREMENT";
		ALTER_TABLE_PRIMARY_KEY="alter table ${tableName} add constraint ${tableName}_pk primary key(${columnName})";
		
		ALTER_TABLE_FOREIGN_KEY="alter table ${tableName} add constraint ${tableName}_${referenceTable}_fk foreign key(${forekeyColumn}) references ${referenceTable}(${referenceTableColumn})"+ON_DELETE;
		SELECT_CONSTRAINT = "select CONSTRAINT_NAME from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where referenced_table_name is not null and  table_name='${tableName}'";
		
		DROP_CONSTRAINT ="alter table ${tableName} drop foreign key ${constraintName}";
		
		
		ALTER_UNIQUE = "alter table ${tableName} add constraint ${tableName}_${columnName}_unique unique(${columnName})";
		PRIMARY_KEY_NEXT_VAL="select auto_increment from information_schema.`TABLES` where table_name='${tableName}'";
		ALTER_TABLE_UNION_PRIMARY_KEY = "alter table ${tableName} add constraint ${tableName}_uni_on_pk primary key(${columnName1},${columnName2})";
		DATE = "date_format('${dateValue}','%Y-%m-%e %T')";
		COUNT = "select count(*) from ${tableName}";
		
		//测试   --5-3基本测试通过
		SELECT = "select distinct ${columnName} from ${tableName}";
		
		DELETE = "delete from ${tableName}";
		
		UPDATE = "update ${tableName}";// set ${columnName}='${columnValue}'
		
		INSERT = "insert into ${tableName}(${columnName}) values(${columnValue})";
		
		//分页 没有测试
		PAGE = "select ${columnName} from ${tableName} where id >= (select id from ${tableName} ${where1} limit ${minPage}, 1) ${where2}  LIMIT ${page} ";
		
		
		jdbcTypeMap.put("integer", "int");
		jdbcTypeMap.put("long", "bigint");
		jdbcTypeMap.put("double", "double");
		jdbcTypeMap.put("string", "varchar(255)");
		jdbcTypeMap.put("date", "datetime");
		jdbcTypeMap.put("boolean", "boolean");
		
	}
	
	private static void oracle() {
		DROP_TABLE="DROP TABLE ${tableName}";
		CREATE_TABLE_BEFORE="CREATE TABLE ${tableName}( ";
		CREATE_TABLE_CENTRE="${columnName} ${columnType},";
		CREATE_TABLE_AFTER=")";
		CREATE_TABLE_AUTO_INCREMENT = "";
		ALTER_TABLE_PRIMARY_KEY="alter table ${tableName} add constraint ${tableName}_pk primary key(${columnName})";
		ALTER_TABLE_FOREIGN_KEY="alter table ${tableName} add constraint ${tableName}_${forekeyColumn}_fk foreign key(${forekeyColumn}) references ${referenceTable}(${referenceTableColumn})"+ON_DELETE;
		
		SELECT_CONSTRAINT = "select CONSTRAINT_NAME from user_constraints e where CONSTRAINT_TYPE!='P' and upper(e.table_name) = '${tableName}'";
		//SELECT_CONSTRAINT = "select constraint_name from user_constraints e where upper(e.table_name) = '${tableName}'";
		
		
		DROP_CONSTRAINT ="alter table ${tableName}  drop constraint ${constraintName}";
		ALTER_UNIQUE = "alter table ${tableName} add constraint ${tableName}_${columnName}_unique unique(${columnName})";
		PRIMARY_KEY_NEXT_VAL="select "+ID_SEQUENCE+".nextval from dual";
		ALTER_TABLE_UNION_PRIMARY_KEY = "alter table ${tableName} add(primary key(${columnName1},${columnName2}))";
		DATE = "to_date('${dateValue}','yyyy-mm-dd hh24:mi:ss')";
		COUNT = "select count(*) from ${tableName}";
		//测试 --5-3基本测试通过
		SELECT = "select distinct ${columnName} from ${tableName}";
		
		
		DELETE = "delete ${tableName}";
		
		UPDATE = "update ${tableName}";//set ${columnName}='${columnValue}'
		
		INSERT = "insert into ${tableName}(${columnName}) values(${columnValue})";
		
		//分页 成功
		PAGE = "select ${columnName} from (select ${tableName}.*,rownum as myrownum from ${tableName} where rownum<=${minPage}) where myrownum >= ${maxPage} ${where}";
		
		jdbcTypeMap.put("integer", "number");
		jdbcTypeMap.put("long", "number");
		jdbcTypeMap.put("double", "number");
		jdbcTypeMap.put("string", "varchar2(255)");
		jdbcTypeMap.put("date", "date");
		jdbcTypeMap.put("boolean", "varchar2(10)");
	}
	
	/**
	 * 返回建表sql
	 * @param tableName 表名
	 * @param columnAndType 表中列和对应数据类型
	 * */
	public static String createTableSql(String tableName,Map<String,JavaType> columnAndType){
		StringBuffer createTable = new StringBuffer(CREATE_TABLE_BEFORE.replace("${tableName}", tableName.toLowerCase().trim()));
		//列s
		Set<String> columns = columnAndType.keySet();
		//列名
		for(String column:columns){
			String type = columnAndType.get(column).toString().toLowerCase().trim();
			//获得数据库类型
			String dbtype = jdbcTypeMap.get(type);
			String center = CREATE_TABLE_CENTRE;
			center = center.replace("${columnName}", column);
			center = center.replace("${columnType}", dbtype);
			createTable.append(center);
			if("id".equals(column.toLowerCase())){
				createTable.insert(createTable.length()-1, " PRIMARY KEY "+CREATE_TABLE_AUTO_INCREMENT);
			}
		}
		//去除多余的,号
		createTable.delete(createTable.length()-1,createTable.length());
		createTable.append(CREATE_TABLE_AFTER);
		//System.out.println("工具类:得到建表语句:"+createTable.toString());
		return createTable.toString();
	}
	
	/**
	 * 返回创建唯一约束
	 * */
	public static String createUnique(String tableName,String columnName){
		String unique = ALTER_UNIQUE;
		unique = unique.replace("${tableName}", tableName.toUpperCase().trim());
		unique = unique.replace("${columnName}", columnName);
		//System.out.println("工具类:得到唯一语句:"+unique);
		return unique;
	}
	/**
	 * 返回添加主键sql
	 * @param tableName 表名
	 * @param primaryKeyColumn 表中主键列名
	 * */
	public static String createAddPrimaryKeySql(String tableName,String primaryKeyColumn){
		String primaryKey = ALTER_TABLE_PRIMARY_KEY;
		primaryKey = primaryKey.replace("${tableName}", tableName.toUpperCase().trim());
		primaryKey = primaryKey.replace("${columnName}", primaryKeyColumn);
		//System.out.println("工具类:得到添加主键语句:"+primaryKey);
		return primaryKey;
	}
	
	/**
	 * 返回添加外键sql
	 * @param tableName 表名
	 * @param forekeyColumn 外键列名
	 * @param referenceTable 引用表名
	 * @param referenceTableColumn 引用表的列名
	 * */
	public static String createAddForeignKeySql(String tableName,String forekeyColumn,String referenceTable,String referenceTableColumn){
		String foreignKey = ALTER_TABLE_FOREIGN_KEY;
		foreignKey = foreignKey.replace("${tableName}", tableName.trim());
		foreignKey = foreignKey.replace("${forekeyColumn}", forekeyColumn);
		foreignKey = foreignKey.replace("${referenceTable}", referenceTable);
		foreignKey = foreignKey.replace("${referenceTableColumn}", referenceTableColumn);
		//System.out.println("工具类:得到添加外键语句:"+foreignKey);
		return foreignKey;
	}
	
	/**
	 * 返回删除表sql 
	 * @param tableName 被删除的表名
	 * */
	public static String createDropTableSql(String tableName){
		String dropTable = DROP_TABLE;
		dropTable = dropTable.replace("${tableName}", tableName.toUpperCase().trim());
		//System.out.println("工具类:得到删除表语句:"+dropTable);
		return dropTable;
	}
	
	/**
	 * 返回删除表中约束sql
	 * 
	 * */
	public static String createDropConstraintSql(String tableName,String constraintname){
		String dropConstraint = DROP_CONSTRAINT;
		dropConstraint = dropConstraint.replace("${tableName}", tableName.toLowerCase().trim());
		dropConstraint = dropConstraint.replace("${constraintName}", constraintname);
		//System.out.println("工具类:得到删除约束语句:"+dropConstraint);
		return dropConstraint;
	}
	
	/**
	 * 返回查询表中约束的sql
	 * @param tableName 表名
	 * */
	public static String createTableConstraintSql(String tableName){
		return SELECT_CONSTRAINT.replace("${tableName}", tableName.toUpperCase().trim());
	}
	
	/**
	 * 返回查询主键下一个值sql<br>
	 * 如果是oracle数据库 返回 查询序列的下一个值<br>
	 * 如果是mysql数据库返回null 这里需要处理 
	 * */
	public static String createPrimaryKeyValue(String tableName){
		String nextVal = PRIMARY_KEY_NEXT_VAL;
		nextVal = nextVal.replace("${tableName}", tableName);
		return nextVal;
	}
	/**
	 * 返回创建联合主键的sql<br>
	 * @param tableName 表名
	 * @param columnOne 第一个列
	 * @param columnTwo 第二个列
	 * */
	public static String createUnionPrimaryKey(String tableName,String columnOne,String columnTwo){
		String primaryKey = ALTER_TABLE_UNION_PRIMARY_KEY;
		primaryKey = primaryKey.replace("${tableName}", tableName);
		primaryKey = primaryKey.replace("${columnName1}", columnOne);
		primaryKey = primaryKey.replace("${columnName2}", columnTwo);
		return primaryKey;
		
	}
	
	/**
	 * 创建分页的sql语句
	 * @param entityClass 当前类对象，需要返回的对象类型
	 * @param minPage 页数的最小值
	 * @param maxPage 页数的最大致
	 * @param where where限制条件    
	 * @return 返回分页的sql语句
	 * */
	public static String createPage(Class<? extends Pojo> entityClass,Integer minPage,Integer maxPage,Where where){
		//oracle : PAGE = "select ${columnName} from (select ${tableName}.*,rownum as myrownum from ${tableName} where rownum<=${minPage}) where myrownum >= ${maxPage}";
		//mysql : PAGE = "select ${columnName} from ${tableName} where id >= (select id from ${tableName} ${where} limit ${minPage}, 1) ${where}  LIMIT ${maxPage}-${minPage} ";

		String page = PAGE;
		
		SqlUtil sqlUtil  = new SqlUtil();
		//主表名
		String entityTableName = sqlUtil.getTableName(entityClass);
		
		//主要 查询对象的 属性集合
		List<Pram> selectSqlParms = sqlUtil.getPramListOfSelect(entityClass);
		
		StringBuffer sb = new StringBuffer();
        for (int i = 0; i < selectSqlParms.size(); i++) {
            //sql += selectSqlParms.get(i).getField();
            sb.append(selectSqlParms.get(i).getField());
            if(i < selectSqlParms.size() -1){
                //sql += ",";
            	sb.append(",");
            }else{
                //sql += " ";
                sb.append(" ");
            }
        }
       
        
		page = page.replace("${columnName}", sb.toString());
		page = page.replace("${tableName}", entityTableName);
		if(page.indexOf("${page}")!=-1){//mysql
			
			if (where != null) {
				page = page.replace("${where1}", where.getWhere());
				//page = page.replace("${where1}", "");
				page = page.replace("${where2}", " and " + where.getWhere().trim().substring(5));
			} else {
				page = page.replace("${where1}", "");
				page = page.replace("${where2}", "");
			}
			
			page = page.replace("${page}", ""+(maxPage-minPage+1));
			page = page.replace("${minPage}", ""+(minPage-1));
		}else{//oracle
			
			if (where != null) {
				page = page.replace("${where}", " and " + where.getWhere().trim().substring(5));
			} else {
				page = page.replace("${where}", "");
			}
			
			page = page.replace("${minPage}", ""+minPage);
			page = page.replace("${maxPage}", ""+maxPage);
		}
		return page;
	}
	
	/**
	 * 返回 select 语句
	 * <br>
	 * 这个select 语句是没有where条件的
	 * @param entityClass 查询数据的主体类
	 * @param cascadeClass 主体类下的属性类型
	 *
	 * 
	 * 如果写了多个pojo类，那么就是多表连接查询，连接条件会自动编写。<br>
	 * <hr>
	 * 注：级联查询 得到需要级联查询的关联数据的sql语句<br>
	 * 注意是查关联属性的sql<br>
	 * 原则:发多次sql语句，第一次 查询主表数据，封装为类，然后再次发送sql语句查询级联数据，然后设置过去。
	 * @throws Exception 
	 * 
	 * */
	public static String createSelect(Class<? extends Pojo> entityClass,Class<? extends Pojo> cascadeClass,Where whereParam) throws Exception{
		String select = SELECT;//select distinct ${columnName} from ${tableName}
		
		SqlUtil sqlUtil  = new SqlUtil();
		//主表名
		String entityTableName = sqlUtil.getTableName(entityClass);
		
		//主要 查询对象的 属性集合
		List<Pram> selectSqlParms = sqlUtil.getPramListOfSelect(entityClass);
		//System.out.println(selectSqlParms);
		if(cascadeClass!=null){//需要级联查询某些属性
			
			//级联的 外表名
			String otherTableName = sqlUtil.getTableName(cascadeClass);
			
			//关联属性 需要查询的值
			List<Pram> otherpramListOfSelect = sqlUtil.getPramListOfSelect(cascadeClass);
			
			
			StringBuffer allSelectSql = new StringBuffer("select ");
			
	        for (int i = 0; i < otherpramListOfSelect.size(); i++) {
	            //sql += selectSqlParms.get(i).getField();
	            allSelectSql.append(otherTableName+"."+otherpramListOfSelect.get(i).getField());
	            if(i < otherpramListOfSelect.size() -1){
	                //sql += ",";
	            	allSelectSql.append(",");
	            }else{
	                //sql += " ";
	                allSelectSql.append(" ");
	            }
	        }
			
			//allSelectSql.append(otherTableName+".* ");
			
			//用于拼接 from 语句
			StringBuffer from = new StringBuffer(" from ");
		
			//用于拼接where语句
			StringBuffer where = new StringBuffer(" where ");
			
			PrimaryForeign pf = sqlUtil.toTableFKOne(entityClass, cascadeClass);//当前类中有多个需要级联查询的列
			BridgeTable bridgeTable = pf.getBridgeTable();
			if(bridgeTable!=null){//有桥表是多对多
				
				System.out.println("bbbb");
				
				//查询主表中数据
				//String sqlprimary = "select * from tbl_student stu where stu.id = 151";
				
				String bridgeName = bridgeTable.getBridgeName();
				//第一个列
				String oneforignKeyColumn = bridgeTable.getOneforignKeyColumn();
				//一个引用列
				String oneJoinColumn = bridgeTable.getOneJoinColumn();
				//第一个引用表
				String oneJoinTableName = bridgeTable.getOneJoinTableName();
				
				
				//第2个列
				String twoforignKeyColumn = bridgeTable.getTwoforignKeyColumn();
				//一个引用列
				String twoJoinColumn = bridgeTable.getTwoJoinColumn();
				//第一个引用表
				String twoJoinTableName = bridgeTable.getTwoJoinTableName();
				
				from.append(entityTableName+",");
				from.append(otherTableName+",");
				from.append(bridgeName);
				from.append(" ");
				
				where.append(oneJoinTableName);
				where.append(".");
				where.append(oneJoinColumn);
				where.append("=");
				where.append(bridgeName);
				where.append(".");
				where.append(oneforignKeyColumn);
				where.append(" and ");
				where.append(twoJoinTableName);
				where.append(".");
				where.append(twoJoinColumn);
				where.append("=");
				where.append(bridgeName);
				where.append(".");
				where.append(twoforignKeyColumn);
				if(whereParam!=null){
					System.out.println("where条件: "+whereParam);
					where.append(" and ");
					String cascadeWhere = sqlUtil.toCascadeWhere(whereParam, entityTableName);
					where.append(cascadeWhere);
				}
				
				allSelectSql.append(from);
				allSelectSql.append(where);
				
				//返回 查询 级联 得到属性的sql语句
				return allSelectSql.toString();
				
			}else{//桥表为null 说明是一对一 或者 一对多
				System.out.println("aaaa");
				
				//主表名
				String primaryTableName = pf.getPrimaryTableName();
				//System.out.println("主表名:"+primaryTableName);
				//主表中的外键列
				String primaryForeignKey = pf.getPrimaryForeignKey();
				//System.out.println("主表中的外键列:"+primaryForeignKey);
				//引用表名
				String foreignTableName = pf.getForeignTableName();
				//System.out.println("引用表名:"+foreignTableName);
				//引用表的主键
				String foreignTableprimaryKey = pf.getForeignTablePrimaryKey();
				//System.out.println("引用表的主键:"+foreignTableprimaryKey);
				//System.out.println("------from "+primaryTableName+","+foreignTableName+" where "+primaryTableName+"."+primaryForeignKey+" = "+foreignTableName+"."+foreignTableprimaryKey);
				
				
				from.append(entityTableName+",");
				from.append(otherTableName);
				from.append(" ");
				
				
				
			//	where tbl_book.book_id=tbl_school.id and tbl_school.id = '45'
				
				where.append(primaryTableName);
				where.append(".");
				where.append(primaryForeignKey);
				where.append("=");
				where.append(foreignTableName);
				where.append(".");
				where.append(foreignTableprimaryKey);
				
				if(whereParam!=null){
					where.append(" and ");
					String cascadeWhere = sqlUtil.toCascadeWhere(whereParam, entityTableName);//没有问题
					where.append(cascadeWhere);
				}
				
					
				allSelectSql.append(from);
				allSelectSql.append(where);
				
				
				//返回 查询 级联 得到属性的sql语句
				return allSelectSql.toString();
			}
		}else{//不需要级联查询某些属性
			StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < selectSqlParms.size(); i++) {
	            //sql += selectSqlParms.get(i).getField();
	            sb.append(selectSqlParms.get(i).getField());
	            if(i < selectSqlParms.size() -1){
	                //sql += ",";
	            	sb.append(",");
	            }else{
	                //sql += " ";
	                sb.append(" ");
	            }
	        }
			select = select.replace("${columnName}", sb.toString());
			String tableName = sqlUtil.getTableName(entityClass);
			select = select.replace("${tableName}", tableName);
			select = select.concat(whereParam.getWhere());
			return select;
		}
	}
	/**
	 * 创建select语句没有级联查询
	 * @throws Exception 
	 * */
	public static String createSelect(Class<? extends Pojo> entityClass,Where whereParam) throws Exception{
		return createSelect(entityClass, null, whereParam);
	}
	/**
	 * 创建删除语句<Br>
	 * @param entityClass 需要删除该类的数据
	 * @param where where 条件
	 * @return 得到对应的删除sql语句
	 * */
	public static String createDelete(Class<? extends Pojo> entityClass,Where where){
		SqlUtil sqlUtil = new SqlUtil();
		String delete = DELETE;
		delete = delete.replace("${tableName}",sqlUtil.getTableName(entityClass) );
		if(where!=null && where.getWhere()!=null && where.getWhere().length()>0){
			delete = delete.concat(where.getWhere());
		}
		//delete from tbl_book where name is null;
		//delete form tbl_book where name is null
		return delete;
	}

	
	/**
	 * 创建更新语句<br>
	 * @param pojo 需要更新的类对象，这个对象必须有id属性值，通过id去更新其他的列。
	 * @param where 更新的where条件，这个值必须要有。
	 * @param 返回update语句
	 * @throws Exception 
	 * */
	@SuppressWarnings("deprecation")
	public static String createUpdate(Pojo pojo,Where where) throws Exception{
		
		String update = UPDATE;
		
		SqlUtil sqlUtil = new SqlUtil();
		
		String tableName = sqlUtil.getTableName(pojo);
		
		List<Pram> prams = SqlUtil.getPramListofStatic(pojo);
		
		Serializable id = sqlUtil.getFileValue(pojo, "id");
		if(id==null){
			throw new Exception("update操作:对象的id属性为空");
		}
		update  = update.replace("${tableName}", tableName);
		
		
		 StringBuffer setSql =new StringBuffer(" set ");
	        for (int i = 0; i < prams.size(); i++) {
	        	Pram pram = prams.get(i);
	        	if(pram.getField().indexOf("mtm")!=-1){
	        		continue;
	        	}
	        	if("id".equals(pram.getField())){
	        		continue;
	        	}
	            if(null != pram.getValue()){
	            	setSql.append(pram.getField());
	            	setSql.append("=");
	                Object value = pram.getValue();
	                if (value instanceof byte[] ) {
	                	setSql.append("'").append(new String((byte[]) value)).append("'");
	                }else if(value instanceof String){
	                	setSql.append("'").append(value).append("'");
	                }else if(value instanceof Date){
	                	
	                	Date date = (Date)value;
	                	//setSql += "to_date('" + date.toLocaleString() + "','yyyy-mm-dd hh24:mi:ss')";
	                	setSql.append(DATE.replace("${dateValue}", date.toLocaleString()));
	                }else{
	                	setSql.append(value);
	                	//setSql += value ;
	                }
	                if (i < prams.size() -1) {
	                	setSql.append(",");
	                	//setSql += ",";
	                }
	            }
	        }
	     
	        if("set".equals(setSql.toString().trim())){
	        	System.err.println("没有修改任何列.");
	        	return null;
	        }
	        if((setSql.lastIndexOf(",")+1)==setSql.length()){
	        	setSql.delete(setSql.lastIndexOf(","), setSql.length());
	        	//setSql = setSql.substring(0, setSql.lastIndexOf(","));
	        }
	        
	        //sql += setSql + " where id= " + id ;
	        
	        if(where!=null && where.getWhere()!=null && where.getWhere().length()>0){
	        	setSql.append(where.getWhere());
	        }else{
	        	setSql.append(" where id = "+id);
	        }
		return update.concat(setSql.toString());
	}
	
	/**
	 * 创建insert 语句<br>
	 * 
	 * */
	@SuppressWarnings("deprecation")
	public static List<String> createInsert(Pojo pojo){
		SqlUtil sqlUtil = new SqlUtil();
		String tableName = sqlUtil.getTableName(pojo);
		String insert = INSERT;
		
		//用于存放sql
		//key 当前sql的描述
		//value 产生的sql语句
		//描述值 : id: 用于获取id值 如果是mysql 则没有 因为自动增长， 如果是oracle 则为查询伪列的值
		List<String> list = new ArrayList<String>();
		
		//list.add(PRIMARY_KEY_NEXT_VAL.replace("${tableName}", tableName));
		//是否是多对多的 标识位
    	boolean flag = false;
    	//多对多的 param
    	Pram pramMtm = null;
    	
    	insert = insert.replace("${tableName}", tableName);
    	
        //用于拼接key
        StringBuffer prams = new StringBuffer();
        //用于拼接value
        StringBuffer values = new StringBuffer();
        
        //通过传入的pojo类对象 返回该pojo对象的非id非@TempField属性的 属性名和属性值集合 
        List<Pram> pramList = SqlUtil.getPramListofStatic(pojo);
        //System.out.println("sqlStaticData.java:668___:"+pramList);
        int index = 0;
        for (int i = 0; i < pramList.size(); i++) {
        	Pram pram = pramList.get(i);
        	String field = pram.getField();
        	if(field.indexOf("mtm")!=-1){
        		flag = true;
        		pramMtm = pram;
        		continue;
        	}
           // if (pram.getValue() == null || (pram.getValue() + "") .equals("0")) {
            if (pram.getValue() == null) {
                continue;
            }else{
                if(index > 0){
                	prams.append(",");
                    //prams += ",";
                	values.append(",");
                    //values += ",";
                }
                prams.append(pramList.get(i).getField());
                //prams += pramList.get(i).getField();
                String value = pramList.get(i).getValue().toString();
                //日期类型 .class class java.util.Date
                String valueTypeStr = pramList.get(i).getValue().getClass().toString();
                if(valueTypeStr.indexOf("Date")!=-1){
                	Date date = (Date) pramList.get(i).getValue();
            		//values += "to_date('" +date.toLocaleString()+ "','yyyy-mm-dd hh24:mi:ss')";
                	values.append(DATE.replace("${dateValue}", date.toLocaleString()));
            		//values += DATE.replace("${dateValue}", date.toLocaleString());
            	}else{
            		//date:
            		values.append("'").append(value).append("'");
            		//values += "'" +value+ "'";
            	}
                index ++;
            }
        }
        insert = insert.replace("${columnName}", prams);
        insert = insert.replace("${columnValue}", values);
        list.add(insert.toString());
        //System.out.println("sql: "+sql);
       // int insert = getSqlSession().insert("save", sql);
        
        if(flag){
        	//insert into ${tableName}(${columnName}) values(${columnValue})
        	String otherInsert = INSERT;
        	//当前添加@ManyToMany的属性名 courses
        	String attribute = pramMtm.getField().split(",")[1];
        	
        	//joinTableName+"_"+foreignKeyColumn0+"_"+foreignKeyColumn1
        	String value = pramMtm.getValue().toString().trim();
        	String[] split = value.split(",");
        	String bridgeTables = split[0];
        	String bridgeTablesColumn_1 = split[1];//当前类的列
        	String bridgeTablesColumn_2 = split[2];//另一个类的列
        	
        	otherInsert = otherInsert.replace("${tableName}", bridgeTables);
        	otherInsert = otherInsert.replace("${columnName}", bridgeTablesColumn_1+","+bridgeTablesColumn_2);
        	
        	//保存到桥表 数据  的sql
        	//String bridgeSql = "insert into "+bridgeTables+"("+bridgeTablesColumn_1+","+bridgeTablesColumn_2+") values(${id},${value})";
        	//得到get方法
        	String getProp  = "get"+attribute.substring(0, 1).toUpperCase()+attribute.substring(1);
        	//System.out.println("得到的 get方法  ："+getProp);
        	Class<? extends Pojo> pojoClass = pojo.getClass();//student对象
        	//pojoClass.getMethod(getProp, );
        	try {
				@SuppressWarnings("unchecked")
				Set<? extends Pojo> invoke = (Set<? extends Pojo>) pojoClass.getMethod(getProp).invoke(pojo);
				if(invoke!=null){
					for(Pojo p:invoke){//course对象
						Class<? extends Pojo> pClass = p.getClass();
						//另一个对象的主键值
						Serializable bridgeTablesColumn_2_value = (Serializable) pClass.getMethod("getId").invoke(p);
						if(bridgeTablesColumn_2_value!=null){
							//中间变量 用来保存桥表数据sql
							String saveBridgeSql = otherInsert;
							//saveBridgeSql = saveBridgeSql.replace("${value}", bridgeTablesColumn_2_value.toString());
							saveBridgeSql = saveBridgeSql.replace("${columnValue}", "${id},"+bridgeTablesColumn_2_value.toString());
							//getSqlSession().insert("save", saveBridgeSql);
							list.add(saveBridgeSql);
						}else{
							throw new Exception(pojoClass.getSimpleName()+"对象，"+pClass.getSimpleName()+"属性的id没有值,不能保存.");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
        	//System.out.println("桥表名:"+bridgeTables);
        	//System.out.println("桥表 列1:"+bridgeTablesColumn_1);
        	//System.out.println("桥表 列2:"+bridgeTablesColumn_2);
        }
        return list;
	}
	/**
	 * 创建查询当前表总数的sql
	 * */
	public static String craeteCount(String tableName,Where where){
		if(where==null){
			return COUNT.replace("${tableName}", tableName);
		}else{
			return COUNT.replace("${tableName}", tableName).concat(where.getWhere());
		}
	}
	
		
}
