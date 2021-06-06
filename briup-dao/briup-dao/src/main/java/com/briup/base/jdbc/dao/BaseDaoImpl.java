package com.briup.base.jdbc.dao;
import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import com.briup.base.jdbc.bean.Pojo;
import com.briup.base.jdbc.bean.SimplePage;
import com.briup.base.jdbc.bean.Where;
import com.briup.base.jdbc.sqldata.SqlStaticData;
import com.briup.base.jdbc.util.Condition;
import com.briup.base.jdbc.util.GenericsUtils;
import com.briup.base.jdbc.util.Query;
import com.briup.base.jdbc.util.SqlUtil;

public abstract class  BaseDaoImpl<T extends Pojo, PK extends Serializable> implements IBaseDao<T, PK> {

	/**
	 * 当前类 泛型的Class对象
	 * */
    private Class<T> entityClass;
    /**
     * 当前类 泛型对象的表名
     * */
    private String tableName;

    //private List<Pram> sqlParms;


   // private List<Pram> selectSqlParms;
    /**
     * sql工具类
     * */
    private SqlUtil sqlUtil;
    /**
     * 用于查询的对象
     * */
    private  Query query;
    
  
    
    //获得连接对象
    public abstract Connection getConnection();
    
 
   
	@SuppressWarnings("unchecked")
	public BaseDaoImpl(){
        super();

        this.sqlUtil = new SqlUtil();

        this.entityClass = (Class<T>) GenericsUtils.getSuperClassGenricType(this.getClass());

        //this.sqlParms = this.sqlUtil.getPramList(this.entityClass);
        //System.out.println(" baseDaoImpl.java_构造器_79 得到的查询sqlParms  : "+sqlParms);
      //  this.selectSqlParms = this.sqlUtil.getPramListOfSelect(this.entityClass);
       // System.out.println(" baseDaoImpl.java_构造器_81 得到的查询Param  : "+selectSqlParms);
             
        this.tableName = this.sqlUtil.getTableName(this.entityClass);

        this.query = new Query();
       
        
    }
    
	// 保存属性不为空的数据<br>
    // 完成 支持Date
    @Override
    public int save(T pojo) {
    	long nextId = nextId();
    	int save = 0;
    	SqlUtil.setFileValue(pojo, "id", nextId);
    	List<String> insert = SqlStaticData.createInsert(pojo);
    	for(String sql :insert){
    		if(sql.indexOf("${id}")!=-1){
    			sql = sql.replace("${id}", ""+nextId);
    		}
    		save +=  query.save(sql,getConnection());
    	}
        return save;
    }
   
    /**
     * 通过id查询数据<br>
     * @throws Exception 
     * */
    @Override
    public T get(PK id) throws Exception {
    	Where whereParam = new Where("id", Condition.EQ, id);
        String sql = SqlStaticData.createSelect(entityClass, whereParam);
        List<T> list = query.selectList(sql, entityClass,getConnection());
        return list!=null?list.get(0):null;
    }
    @Override
    public T get(PK id,Class<? extends Pojo> cascadeClass) throws Exception {
    	Where whereParam = new Where("id", Condition.EQ, id);
    	//主要对象的sql
    	String sql = SqlStaticData.createSelect(entityClass, whereParam);
    	List<T> list = query.selectList(sql, entityClass,getConnection());
    	//需要被返回的数据
    	T t = list!=null?list.get(0):null;
    	
    	//级联对象的sql
		String cascadeSelect = SqlStaticData.createSelect(entityClass, cascadeClass, whereParam);
		
		System.out.println("级联查询对象的sql : "+cascadeSelect);
		//级联查询出来的数据
		List<? extends Pojo> selectList = query.selectList(cascadeSelect, cascadeClass, getConnection());

		t = sqlUtil.setCascadeValue(t, cascadeClass, selectList);
    	return t;
    }

    
    
    @Override
    public List<T> get(Where where) throws Exception {
    	String createSelect = SqlStaticData.createSelect(entityClass, where);
        return query.selectList(createSelect, entityClass,getConnection());
    }
    
   @Override
	public Serializable getField(PK id, String fieldName) throws Exception {
	   Where where = new Where("id", Condition.EQ, id);
	   String createSelect = SqlStaticData.createSelect(entityClass, where);
	   List<T> selectList = query.selectList(createSelect, entityClass,getConnection());
	   if(selectList!=null){
		   T t = selectList.get(0);
		   Serializable fileValue = sqlUtil.getFileValue(t, fieldName);
		   return fileValue;
	   }
	   return null;
	}
    @Override
    public List<? extends Pojo> getBySql(String sql,Class<? extends Pojo> resultClass) {
    	return query.selectList(sql, resultClass,getConnection());
    }
    
    /**
     * 更新pojo需要有id的值
     * @throws Exception 
     * */
    @Override
    public int update(T pojo) throws Exception {
    	String createUpdate = SqlStaticData.createUpdate(pojo, null);
        return query.update(createUpdate,getConnection());
    }
   
    
    @Override
    public int delete(PK id) {
    	Where where = new Where("id", Condition.EQ, id);
    	String createDelete = SqlStaticData.createDelete(entityClass,where);
        return query.delete(createDelete,getConnection());
    }

   
    
    
    
    
    
    
    
    
    
   
   
    @Override
    public int excuse(String sql) {
    	return query.update(sql,getConnection());
    }
    @Override
    public long count() {
        return  query.selectLong(SqlStaticData.craeteCount(tableName,null),getConnection());
    }

   
 
   
  


    //支持 Date
    @Override
    public int update(T pojo, Where where) throws Exception {
       String createUpdate = SqlStaticData.createUpdate(pojo, where);
       return  query.update(createUpdate,getConnection());
    }
  
  
    
    @Override
    public int deleteByWherePrams(Where where) {
    	String createDelete = SqlStaticData.createDelete(entityClass, where);
        return query.delete(createDelete,getConnection());
    }
   
 
    @Override
    public long count(Where where) {
        Long count = query.selectLong(SqlStaticData.craeteCount(tableName,where),getConnection());
        return count;
    }
   
    @Override
    public boolean isExist(Where where) {
        return count(where) > 0;
    }
    
    /**
     * 获取某表的下一个Id
     */
    public long nextId(){
    	String sql = SqlStaticData.createPrimaryKeyValue(tableName);
    	if(!"".equals(sql)){
    		Long idVal = query.selectLong(sql,getConnection());
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
     * @throws Exception 
     * */
    public SimplePage<T> page(Integer everyPageCount ,Integer currentPage,Where where) throws Exception{
    	
    	
    	if(currentPage==null || currentPage<=0){
    		currentPage = 1;
    	}
    	if(everyPageCount==null || everyPageCount<=0){
    		everyPageCount = 10;
    	}
    	 //List<Pram> pramList = sqlUtil.getPramList(entityClass);
    	SimplePage<T> simplePage = new SimplePage<T>();
    	simplePage.setPage(everyPageCount);//设置每页显示数据量
    	simplePage.setPageDataNum((int)count(where));//设置总数量
    	simplePage.setPageNo(currentPage);
    	
    	
    	
    	int pageStart = simplePage.getPageStart();
    	int pageEnd = simplePage.getPageEnd();
    	//System.out.println("开始:"+pageStart+"   结束 : "+pageEnd);
        pageStart++;
        pageEnd++;
        
        if(pageStart>pageEnd){
        	throw new Exception("输入的数据有误，超过最大页码。");
        }
        
        String sql = SqlStaticData.createPage(entityClass, pageStart, pageEnd, where);
       // System.out.println("分页sql :"+sql);
        
       	List<T> list = query.selectList(sql, entityClass,getConnection());
       	simplePage.setList(list);
        return simplePage;
    }
   /* public SimplePage<T> page(Integer everyPageCount ,Integer currentPage,WhereParams where){
    	
    	
    	
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
    	
    	System.out.println("分页sql :"+sql);
    	
    	List<T> list = query.selectList(sql, entityClass,getConnection());
    	simplePage.setList(list);
    	return simplePage;
    }*/

    
   
 
    
}