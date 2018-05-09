package com.briup.base.jdbc.dao;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;

import com.briup.base.jdbc.annocation.ColumnName;
import com.briup.base.jdbc.bean.Pojo;
import com.briup.base.jdbc.bean.Pram;
import com.briup.base.jdbc.bean.SimplePage;
import com.briup.base.jdbc.bean.WhereParams;
import com.briup.base.jdbc.sqldata.SqlStaticData;
import com.briup.base.jdbc.util.GenericsUtils;
import com.briup.base.jdbc.util.Query;
import com.briup.base.jdbc.util.SqlUtil;
import com.briup.test.bean.Book;
@SuppressWarnings("all")
public abstract class  BaseDaoImpl<T extends Pojo, PK extends Serializable> implements IBaseDao<T, PK> {

	private SqlSession session;

    private Class<T> entityClass;

    private String tableName;

    //private List<Pram> sqlParms;


    private List<Pram> selectSqlParms;

    private SqlUtil sqlUtil;

    
    private  Query query;
    /**
     * 读取当前文件中需要的配置文件
     * */
    private Properties properties = new Properties();
    {
    	String path = this.getClass().getClassLoader().getResource("").getPath();
		//System.out.println(this.getClass().getClassLoader().getResource(""));
		//String path = "src";
		//System.out.println("连接对象的 路径path: "+path);
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
    
    public abstract SqlSession getSqlSession();
   
	public BaseDaoImpl(){
        super();

        this.sqlUtil = new SqlUtil();

        this.entityClass = (Class<T>) GenericsUtils.getSuperClassGenricType(this.getClass());

        //this.sqlParms = this.sqlUtil.getPramList(this.entityClass);
        //System.out.println(" baseDaoImpl.java_构造器_79 得到的查询sqlParms  : "+sqlParms);
        this.selectSqlParms = this.sqlUtil.getPramListOfSelect(this.entityClass);
       // System.out.println(" baseDaoImpl.java_构造器_81 得到的查询Param  : "+selectSqlParms);
             

        this.tableName = this.sqlUtil.getTableName(this.entityClass);

        
        this.query = new Query();
    }
    
	// 保存属性不为空的数据<br>
    // 完成 支持Date
    @Override
    public int save(T pojo) {
    	//是否是多对多的 标识位
    	boolean flag = false;
    	//多对多的 param
    	Pram pramMtm = null;
    	
    	//产生的主键值
    	long nextId = nextId();
    	
    	//把id的值设置到id属性中
    	SqlUtil.setFileValue(pojo, "id",nextId );
        String sql = "insert into " + tableName + "(";
        //用于拼接key
        String prams = "";
        //用于拼接value
        String values = "";
        
        //通过传入的pojo类对象 返回该pojo对象的非id非@TempField属性的 属性名和属性值集合 
        List<Pram> pramList = SqlUtil.getPramListofStatic(pojo);
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
                    prams += ",";
                    values += ",";
                }
                prams += pramList.get(i).getField();
                String value = pramList.get(i).getValue().toString();
                //日期类型 .class class java.util.Date
                String valueTypeStr = pramList.get(i).getValue().getClass().toString();
                if(valueTypeStr.indexOf("Date")!=-1){
                	Date date = (Date) pramList.get(i).getValue();
            		values += "to_date('" +date.toLocaleString()+ "','yyyy-mm-dd hh24:mi:ss')";
            	}else{
            		//date:
            		values += "'" +value+ "'";
            	}
                index ++;
            }
        }
        sql += prams + ") values(" + values +")";
        //System.out.println("sql: "+sql);
       // int insert = getSqlSession().insert("save", sql);
        int insert = query.save(sql);
        
        
        if(flag){
        	//当前添加@ManyToMany的属性名 courses
        	String attribute = pramMtm.getField().split(",")[1];
        	
        	//joinTableName+"_"+foreignKeyColumn0+"_"+foreignKeyColumn1
        	String value = pramMtm.getValue().toString().trim();
        	String[] split = value.split(",");
        	String bridgeTables = split[0];
        	String bridgeTablesColumn_1 = split[1];//当前类的列
        	String bridgeTablesColumn_2 = split[2];//另一个类的列
        	
        	//保存到桥表 数据  的sql
        	String bridgeSql = "insert into "+bridgeTables+"("+bridgeTablesColumn_1+","+bridgeTablesColumn_2+") values("+nextId+",${value})";
        	//得到get方法
        	String getProp  = "get"+attribute.substring(0, 1).toUpperCase()+attribute.substring(1);
        	//System.out.println("得到的 get方法  ："+getProp);
        	Class<? extends Pojo> pojoClass = pojo.getClass();//student对象
        	//pojoClass.getMethod(getProp, );
        	try {
				Set<? extends Pojo> invoke = (Set<? extends Pojo>) pojoClass.getMethod(getProp).invoke(pojo);
				for(Pojo p:invoke){//course对象
					Class<? extends Pojo> pClass = p.getClass();
					//另一个对象的主键值
					Serializable bridgeTablesColumn_2_value = (Serializable) pClass.getMethod("getId").invoke(p);
					//中间变量 用来保存桥表数据sql
					String saveBridgeSql = bridgeSql;
					saveBridgeSql = saveBridgeSql.replace("${value}", bridgeTablesColumn_2_value.toString());
					//getSqlSession().insert("save", saveBridgeSql);
					query.save(saveBridgeSql);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
        	//System.out.println("桥表名:"+bridgeTables);
        	//System.out.println("桥表 列1:"+bridgeTablesColumn_1);
        	//System.out.println("桥表 列2:"+bridgeTablesColumn_2);
        }
        return insert;

    }

   
    /**
     * 通过id查询数据<br>
     * */
    @Override
    public T get(PK id) {
        String sql = "select ";
        for (int i = 0; i < selectSqlParms.size(); i++) {
            sql += selectSqlParms.get(i).getField();
            if(i < selectSqlParms.size() -1){
                sql += ",";
            }else{
                sql += " ";
            }
        }
        sql += " from " + tableName + " where id= '" + id+"'";
        
        
        //Map<String, Object> resultMap = getSqlSession().selectOne("get", sql);
        List<T> selectList = query.selectList(sql, entityClass);
        if(selectList!=null && selectList.size()>0){
        	return selectList.get(0);
        }else{
        	return null;
        }
    }
    

    //----------------------↑ 测试 2018_3_6---------------------
    /**
     * 更新pojo需要传入id
     * */
    @Override
    public int update(T pojo) {
    	
        Serializable id = sqlUtil.getFileValue(pojo, "id");
        if(null == id){
            return 0;
        }
        String sql = "update " + tableName;
     
        // List<Pram> prams = sqlUtil.getPramList(po);
      
        List<Pram> prams = sqlUtil.getPramListofStatic(pojo);
        //System.out.println("getPramListofStatic : BaseDaoImpl.java___327:"+prams);
     
        /* 		
        		BookABC
        		getPramListofStatic : BaseDaoImpl.java___327:
        		[Pram [field=id, value=22]
        		, Pram [field=name, value=hansdfsdfsdfsddom]
        		, Pram [field=price, value=190.2]
        		, Pram [field=publish_address, value=昆山sdfsdf出版社]
        		, Pram [field=dob, value=Sat Apr 28 08:58:02 CST 2018]
        		, Pram [field=num, value=1029]*/
        
        String setSql =" set ";
        for (int i = 0; i < prams.size(); i++) {
        	Pram pram = prams.get(i);
        	if(pram.getField().indexOf("mtm")!=-1){
        		continue;
        	}
            if(null != pram.getValue()){
            	setSql += pram.getField() + "=";
                Object value = pram.getValue();
                if (value instanceof byte[] ) {
                	setSql += "'" + new String((byte[]) value) + "'";
                }else if(value instanceof String){
                	setSql += "'" + value + "'";
                }else if(value instanceof Date){
                	Date date = (Date)value;
                	setSql += "to_date('" + date.toLocaleString() + "','yyyy-mm-dd hh24:mi:ss')";
                }else{
                	setSql += value ;
                }
                if (i < prams.size() -1) {
                	setSql += ",";
                }
            }
        }
     
        if("set".equals(setSql.trim())){
        	try {
				throw new Exception("没有修改任何列");
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
        }
        if((setSql.lastIndexOf(",")+1)==setSql.length()){
        	setSql = setSql.substring(0, setSql.lastIndexOf(","));
        }
        sql += setSql + " where id= " + id ;
        
       // return getSqlSession().update("update", sql);
        return query.update(sql);
    }
   
    
    @Override
    public int delete(PK id) {
        String sql = "delete from " + tableName + " where id =" + id;

       // return getSqlSession().delete("delete", sql);
        return query.delete(sql);
    }

    @Override
    public List<T> getBySql(String sql) {
    	return query.selectList(sql, entityClass);
    	
    }
    
   
   
    @Override
    public int excuse(String sql) {
       // return getSqlSession().update("update", sql);
    	return query.update(sql);
    }
    @Override
    public long count() {
        String sql = "select count(*) from " + tableName;
        //long count = getSqlSession().selectOne("getLong", sql);
        return  query.selectLong(sql);
    }

   
 
   
    @Override
    public List<T> get(WhereParams where) {
        String sql = "select ";
        for (int i = 0; i < selectSqlParms.size(); i++) {
            sql += selectSqlParms.get(i).getField();
            if(i < selectSqlParms.size() -1){
                sql += ",";
            }else{
                sql += " ";
            }
        }
        sql += "from " + tableName + where.getWherePrams();
        
        return query.selectList(sql, entityClass);
      
    }



    //支持 Date
    @Override
    public int update(T po, WhereParams where) {

        String sql = "update " + tableName;
       // List<Pram> prams = sqlUtil.getPramList(po);
        List<Pram> prams = sqlUtil.getPramListofStatic(po);
       // System.out.println("BaseDaoImpl_558_"+prams);
        /*[Pram [file=name, value=新小说]
          Pram [file=price, value=null]
          Pram [file=publish, value=null]*/
        //传入的pojo类没有修改任何的属性值 时候会报错  ：update book set   where id  is null 错误的sql
        String setSql =" set ";
        for (int i = 0; i < prams.size(); i++) {
        	
        	Pram pram = prams.get(i);
        	if(pram.getField().indexOf("mtm")!=-1){
        		continue;
        	}
        	
            if(null != prams.get(i).getValue()){
            	setSql += pram.getField() + " = ";
                Object value = pram.getValue();
                if (value instanceof byte[] ) {
                	setSql += "'" + new String((byte[]) value) + "'";
                }else if(value instanceof String){
                	setSql += "'" + value + "'";
                }else if(value instanceof Date){
                	Date date = (Date)value;
                	setSql += "to_date('" + date.toLocaleString() + "','yyyy-mm-dd hh24:mi:ss')";
                }else{
                	setSql += value ;
                }
//              sql += prams.get(i).getFile() + "='" + prams.get(i).getValue() + "'";
                if (i < prams.size() -1) {
                	setSql += ",";
                }
            }
        }
        if("set".equals(setSql.trim())){
        	try {
				throw new Exception("没有修改任何列");
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
        }
     
        if((setSql.lastIndexOf(",")+1)==setSql.length()){
        	setSql = setSql.substring(0, setSql.lastIndexOf(","));
        }
        sql += setSql + where.getWherePrams();
       // System.out.println("sql:"+sql);
        //sql += where.getWherePrams();
       // return getSqlSession().update("update", sql);
        return  query.update(sql);
    }
  
  
    
    @Override
    public int deleteByWherePrams(WhereParams where) {

        String sql = "delete from " + tableName + where.getWherePrams();
       // return getSqlSession().delete("delete", sql);
        return query.delete(sql);
    }
   
 
    @Override
    public long count(WhereParams where) {
        String sql = "select count(*) from ";
        sql += tableName + where.getWherePrams();
      //  Long count = getSqlSession().selectOne("getLong", sql);
        Long count = query.selectLong(sql);
        return count;
    }
   
    @Override
    public boolean isExist(WhereParams where) {
        return count(where) > 0;
    }
  
    
    
 
 
    
    /**
     * 获取某表的下一个Id
     */
    public long nextId(){
    	
    	//String sql = "select "+property+".nextval from dual";
    	String sql = SqlStaticData.createPrimaryKeyValue();
    	if(!"".equals(sql)){
    		Long idVal = query.selectLong(sql);
    		if (null == idVal) {
    			return 0;
    		}
    		return idVal;
    	}else{
    		return 0;
    	}
    }
    
    /**
     * 分页查询<br>
     * @param everyPageCount 每页显示数据量
     * @param currentPage 当前第几页
     * @param where 限制条件
     * */
    public SimplePage<T> page(Integer everyPageCount ,Integer currentPage,WhereParams where){
    	
    	
    	
    	if(currentPage==null || currentPage<=0){
    		currentPage = 1;
    	}
    	if(everyPageCount==null || everyPageCount<=0){
    		everyPageCount = 10;
    	}
    	 //List<Pram> pramList = sqlUtil.getPramList(entityClass);
    	SimplePage<T> simplePage = new SimplePage<T>();
    	simplePage.setPage(everyPageCount);//设置每页显示数据量
    	simplePage.setPageDataNum((int)count());//设置总数量
    	simplePage.setPageNo(currentPage);
    	
    	int pageStart = simplePage.getPageStart();
    	int pageEnd = simplePage.getPageEnd();
    	String select = "select ";
        for (int i = 0; i < selectSqlParms.size(); i++) {
            select += selectSqlParms.get(i).getField();
            if(i < selectSqlParms.size() -1){
                select += ",";
            }else{
                select += " ";
            }
        }
        pageStart++;
        pageEnd++;
        //System.out.println("起始页: "+pageStart+"---结束页: "+pageEnd);
        //select id as id,name as name,price as price,publish_address as publishAddress,dob as dob,num as num ,rownum as row from BookABC
    	//System.out.println(select);
        String sql = null;
        if(where!=null){
         	String wherePrams = where.getWherePrams().trim().substring(5)+" and ";
        	sql = select+" from (select "+tableName+".* ,rownum as myrownum from "+tableName+" where "+wherePrams+" rownum <= "+pageEnd+") where myrownum >= "+pageStart;
        	
        }else{
        	sql = select+" from (select "+tableName+".* ,rownum as myrownum from "+tableName+" where rownum <= "+pageEnd+") where myrownum >= "+pageStart;
        }
        
       	List<T> list = query.selectList(sql, entityClass);
       	simplePage.setList(list);
        return simplePage;
    }

    
   
    /* 
	获取map中的泛型
 
Type t = Test.class.getDeclaredField("map").getGenericType();  
if (ParameterizedType.class.isAssignableFrom(t.getClass())) {  
    for (Type t1 : ((ParameterizedType) t).getActualTypeArguments()) {  
        System.out.print(t1 + ",");  
    }  
    System.out.println();  
}  */
  
    
    //完成--------------------------------------------------------------------
    
    
    

    /*
     * 通过id 查询 某一个属性值<br>
     * 目前觉得没有用
     * */
   /* @Override
    public Serializable getField(PK id, String fieldName) {
        String tabField = "";
        Field f = sqlUtil.getField(this.entityClass, fieldName);
        if (null == f) {
            try {
				throw new Exception("查询字段失败(无法找到" + this.entityClass + "中的" + fieldName + "字段)");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
        }
        ColumnName annotation = f.getAnnotation(ColumnName.class);
        if (null == annotation) {
            tabField = sqlUtil.toTableString(fieldName) + " as " + fieldName;
        }else{
            tabField = annotation.name() + " as " + fieldName;
        }

        String sql = "select ";
        sql += tabField + " from " + tableName + " where id=" + id ;
        
        Map<String, Object> resultMap = getSqlSession().selectOne("get", sql);
        if(resultMap!=null){
        	return (Serializable) resultMap.get(fieldName.toUpperCase());
        }else{
        	return null;
        }
    }*/
    
    /*
     * 添加 对象<br>
     * 完成
     * */
   /* @Override
    public int save(T po) {
    	
    	SqlUtil.setFileValue(po, "id", nextId());
    	
        String sql = "insert into " + tableName + " (";
        String prams = "";
        String values = "";

        List<Pram> pramList = SqlUtil.getPramListofStatic(po);
        
	 [Pram [file=id, value=125]
	    , Pram [file=name, value=小书]
	    , Pram [file=school_id, value=School [id=124, name=你好, address=南昌, phone=18928, userGender=null]]
        
        
        
        for (int i = 0; i < pramList.size(); i++) {
            prams += pramList.get(i).getField();
            if (pramList.get(i).getValue() == null) {
                values += "null";
            }else{
            	String value = pramList.get(i).getValue().toString();
            	if(value.indexOf("date")!=-1){
            		values += "to_date('" +value.substring(5)+ "','yyyy-mm-dd hh24:mi:ss')";
            	}else{
            		//date:
            		values += "'" +value+ "'";
            	}
            }

            if(i < pramList.size() -1){
                prams += ",";
                values += ",";
            }
        }
        sql += prams + ")values (" + values +")";
        //System.out.println("BaseDaoImpl_保存时候的sql : "+sql);
        return getSqlSession().insert("add", sql);
    }*/
    
    /*
  	更新

  @Override
public int update(T po) {

    Serializable id = sqlUtil.getFileValue(po, "id");

    if(null == id){
        return 0;
    }
    String sql = "update " + tableName + " set ";

    List<Pram> prams = sqlUtil.getPramList(po);

    for (int i = 0; i < prams.size(); i++) {
        if(null != prams.get(i).getValue()){
            sql += prams.get(i).getField() + "=";
            Object value = prams.get(i).getValue();
            if (value instanceof byte[] ) {
                sql += "'" + new String((byte[]) value) + "'";
            }else if(value instanceof String){
                sql += "'" + value + "'";
            }else if(value instanceof Date){
            	Date date = (Date)value;
            	sql += "to_date('" + date.toLocaleString() + "','yyyy-mm-dd hh24:mi:ss')";
            }else{
                sql += value ;
            }
//          sql += prams.get(i).getFile() + "='" + prams.get(i).getValue() + "'";
            if (i < prams.size() -1) {
                sql += ",";
            }
        }else{
            sql += prams.get(i).getField() + "=null";
            if (i < prams.size() -1) {
                sql += ",";
            }
        }
    }
    sql += " where id = " + id ;

    return getSqlSession().update("update", sql);
}*/
    /*
     * 辅助方法:<br>
     *  将结果集返回的map 封装为对象
     * */
   /* private T handleResult(Map<String, Object> resultMap, Class<T> tClazz) {
        if (null == resultMap) {
            return null;
        }
        T t = null;
        try {
            t = tClazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
            String key = entry.getKey().toLowerCase();
            
            Serializable val = (Serializable) entry.getValue();
            try {
                SqlUtil.setFileValue(t, key, val);
            } catch (Exception e) {
                System.err.println("/t实例化Bean失败(" + this.entityClass + ")不能赋值到字段(" + key + "):"+ e.getMessage());
                e.printStackTrace();
            }
        }
        return t;
    }*/
    //支持 Date
    /* @Override
     public int update(T po, WherePrams where) {


         String sql = "update " + tableName + " set ";
         List<Pram> prams = sqlUtil.getPramList(po);
         for (int i = 0; i < prams.size(); i++) {
             sql += prams.get(i).getField() + "=";
             Object value = prams.get(i).getValue();
             if (value instanceof byte[] ) {
                 sql += "'" + new String((byte[]) value) + "'";
             }else if(value instanceof String){
                 sql += "'" + value + "'";
             }else if(value instanceof Date){
             	Date date = (Date)value;
             	sql += "to_date('" + date.toLocaleString() + "','yyyy-mm-dd hh24:mi:ss')";
             }else{
                 sql += value ;
             }
//               sql += prams.get(i).getFile() + "='" + prams.get(i).getValue() + "'";
             if (i < prams.size() -1) {
                 sql += ",";
             }
         }
         sql += where.getWherePrams();
         return getSqlSession().update("updateByPram", sql);
     }*/
    /*   @Override
    public Serializable getFile(WhereParams where, String fieldName) {
        String tabField = "";
        Field f = sqlUtil.getField(this.entityClass, fieldName);
        if (null == f) {
            try {
				throw new Exception("查询字段失败(无法找到" + this.entityClass + "中的" + fieldName + "字段)");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
        }
        ColumnName annotation = f.getAnnotation(ColumnName.class);
        if (null == annotation) {
            tabField = sqlUtil.toTableString(fieldName) + " as " + fieldName;
        }else{
            tabField = annotation.name() + " as " + fieldName;
        }

        String sql = "select ";
        sql += tabField + " from " + tableName +" " + where.getWherePrams();
        Map<String, Object> resultMap = getSqlSession().selectOne("getFieldByParm", sql);
        return (Serializable) resultMap.get(fieldName.toUpperCase());
    }*/
    /*
    好像在wherePram中封装了
    这里先去除
@Override
public List<T> in(String fieldName, Serializable... values) {
	Class<?> type = null;
	try {
		Field field = entityClass.getDeclaredField(fieldName);
		type = field.getType();
	}catch (Exception e) {
		e.printStackTrace();
		return null;
	}
	//拼接查询的列
  StringBuffer sql = new StringBuffer("select ");
  
  for(int i = 0;i<selectSqlParms.size();i++){
  	sql.append(selectSqlParms.get(i).getField());
  	if(i<selectSqlParms.size()-1){
  		sql.append(",");
  	}else{
  		sql.append(" ");
  	}
  }
  
  //拼接in中的数据
  sql.append("from " + tableName + " where " + sqlUtil.toTableString(fieldName) + " in");
  String value = "(";
  for(int i = 0; i < values.length; i++){
      if(i < values.length-1){
      	if(String.class == type){
      		value += ("'"+values[i] + "',"); 
      	}else if(Date.class==type){
      		Date date = (Date) values[i];
      		value += "to_date('"+date.toLocaleString()+"','yyyy-mm-dd hh24:mi:ss')";
      	}else{
      		value += values[i] + ","; 
      	}
      }else{
      	if(String.class == type){
      		value += ("'"+values[i] + "')"); 
      	}else if(Date.class==type){
      		Date date = (Date) values[i];
      		value += "to_date('"+date.toLocaleString()+"','yyyy-mm-dd hh24:mi:ss'))";
      	}else{
      		value += values[i] + ")"; 
      	}
      }
  }
  sql.append(value);
  //System.out.println("BaseDaoImpl__408__"+sql.toString());
  List<Map<String, Object>> selectList = getSqlSession().selectList("get", sql.toString());

  List<T> list = new ArrayList<>();
  for (Map<String, Object> map : selectList) {
      T t = handleResult(map, this.entityClass);
      list.add(t);
  }
  return list;
  if(selectList!=null && selectList.size()>0){
  	
  	return toListPojoByMap(selectList);
  }else{
  	return null;
  }
}*/
    
    /* 查询某个属性 没有必要 删除@Override
    public Serializable[] getFidleValues(WhereParams where, String fieldName) {
        String tabField = "";
        Field f = sqlUtil.getField(this.entityClass, fieldName);
        if (null == f) {
            try {
				throw new Exception("查询指定字段集失败(无法序列化" + this.entityClass + "中的" + fieldName + "字段)");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
        }
        ColumnName annotation = f.getAnnotation(ColumnName.class);
        if (null == annotation) {
            tabField = sqlUtil.toTableString(fieldName) + " as " + fieldName;
        }else{
            tabField = annotation.name() + " as " + fieldName;
        }

        String sql = "select ";
        sql += tabField + " from " + tableName + where.getWherePrams();
        List<Map<String, Object>> resultMap = getSqlSession().selectList("get", sql);

        Serializable[] fields = new Serializable[resultMap.size()];

        for (int i = 0; i < resultMap.size(); i++) {
            if (null != resultMap.get(i)) {
                fields[i] =(Serializable) resultMap.get(i).get(fieldName.toUpperCase());
            }
        }
        return fields;
    }*/
    
    /* @Override
    public List<T> getFieldsByWherePrams(WhereParams where, String... fields) {
        String tabField = "";
        int index = 1;
        for (String field : fields) {
            Field f = sqlUtil.getField(this.entityClass, field);
            if (null == f) {
                try {
    				throw new Exception("查询指定字段集失败(无法序列化" + this.entityClass + "中的" + field + "字段)");
    			} catch (Exception e) {
    				e.printStackTrace();
    				return null;
    			}
            }
            ColumnName annotation = f.getAnnotation(ColumnName.class);
            if (null == annotation) {
                tabField += sqlUtil.toTableString(field) + " as " + field;
            }else{
                tabField += annotation.name() + " as " + field;
            }
            if (index < fields.length) {
                tabField += ",";
            }
            index ++;
        }

        String sql = "select ";
        sql += tabField + " from " + tableName + where.getWherePrams();
        List<Map<String, Object>> resultMap = getSqlSession().selectList("get", sql);
        
        List<T> list = new ArrayList<>();
        for (Map<String, Object> map : resultMap) {
            T t = handleResult(map, this.entityClass);
            list.add(t);
        }
        return list;
        if(resultMap!=null && resultMap.size()>0){
        	return toListPojoByMap(resultMap);
        }else{
        	return null;
        }
    }*/
    /**
     * 辅助方法----------------
     * 把map转换为对象返回
     * <br>
     * date [{ADDRESS=新山, PHONE=a111, ID=59, NAME=新2数据, USER_GENDER=新女}]
     * */
/*   private List<T> toListPojoByMap(List<Map<String, Object>> date){
	   if(date==null){
		   return null;
	   }
    	List<T> list = new ArrayList<T>();
		try {
			Field[] fields = this.entityClass.getDeclaredFields();
			//System.out.println(date);
			for (Map<String, Object> map : date) {
				T t = this.entityClass.newInstance();
				for(Field field :fields){
					//属性名
					String fName = field.getName();
					
					String set = "set";
					//属性类型名
					String fieldType = field.getGenericType().toString();
					
					if (fieldType.indexOf("boolean") != -1 ) {
	    				set = "is";
					}
	    			if (field.isAnnotationPresent(ColumnName.class)) {
	    				String fieldName = field.getAnnotation(ColumnName.class).name();
	    				//通过属性名从返回的map中获取了属性值
	    				Object value = map.get(fieldName.toUpperCase());
	    				String setProp = set + fName.substring(0, 1).toUpperCase() + fName.substring(1);
	    				this.entityClass.getMethod(setProp,field.getType()).invoke(t,value);
	    			}else{
	    				String fieldName =fName;
	    				
	    				Object value = map.get(fieldName.toUpperCase());
	    				if(value==null){
	    					continue;
	    				}
	    				String setProp = set + fName.substring(0, 1).toUpperCase() + fName.substring(1);
	    				java.lang.reflect.Method method = this.entityClass.getMethod(setProp,field.getType());
	    				//System.out.println("BaseDaoImpl.java_743_"+fieldType+"\t"+value);
	    				
	    				if(fieldType.indexOf("String")!=-1){
	    					method.invoke(t, ""+value);
	    				}else if(fieldType.indexOf("Integer")!=-1){
	    					java.math.BigDecimal bd = (BigDecimal) value;
	    					method.invoke(t, bd.intValue());
	    				}else if(fieldType.indexOf("Long")!=-1){
	    					java.math.BigDecimal bd = (BigDecimal) value;
	    					method.invoke(t, bd.longValue());
	    					//--------------测试了long
	    				}else if(fieldType.indexOf("Short")!=-1){
	    					java.math.BigDecimal bd = (BigDecimal) value;
	    					method.invoke(t, bd.shortValue());
	    				}else if(fieldType.indexOf("Boolean")!=-1){
	    					method.invoke(t, Boolean.parseBoolean(value.toString()));
	    				}else if(fieldType.indexOf("Date")!=-1){
	    					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss");
	    					value = sdf.parse(value.toString());
	    					method.invoke(t, value);
	    				}else if(fieldType.indexOf("Double")!=-1){
	    					java.math.BigDecimal bd = (BigDecimal) value;
	    					method.invoke(t, bd.doubleValue());
	    				}else if(fieldType.indexOf("Float")!=-1){
	    					java.math.BigDecimal bd = (BigDecimal) value;
	    					method.invoke(t, bd.floatValue());
	    				}else if(fieldType.indexOf("Character")!=-1){
	    					
	    				}else if(fieldType.indexOf("Byte")!=-1){
	    					method.invoke(t, (""+value).getBytes());
	    				}else{
	    					
	    				}
					}
				}
				list.add(t);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }*/
    
}