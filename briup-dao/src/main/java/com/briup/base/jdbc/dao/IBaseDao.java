package com.briup.base.jdbc.dao;



import java.io.Serializable;
import java.util.List;

import com.briup.base.jdbc.bean.Pojo;
import com.briup.base.jdbc.bean.SimplePage;
import com.briup.base.jdbc.bean.Where;

/**
 * 公共数据库操作层
 * <br>
 * 泛型 T  pojo 基础pojo类需要继承的类<br>
 * 泛型 PK pojo类的主键数据类型
 */
public interface IBaseDao<T extends Pojo, PK extends Serializable> {

    /**
     * 添加
     * @param pojo
     * @return 受改变的记录数
     */
    public int save(T pojo) throws Exception;
    
 

    /**
     * 通过主键获取某个记录
     * @param id 主键
     * @return pojo
     */
    public T get(PK id) throws Exception;

    /**
     * 通过id查询，并且可以级联查询当前类对象下的cascadeClass类型的属性
     * @param id 主键
     * @param cascadeClass 需要级联查询属性的类型
     * @return 返回对象
     * */
    public T get(PK id,Class<? extends Pojo> cascadeClass) throws Exception;
    
    /**
     * 通过主键获取某个字段的值
     * @param id	主键值
     * @param fieldName 属性名,注意 这个属性名不能是 pojo类型
     * @return
     */
    public Serializable getField(PK id, String fieldName) throws Exception;

    /**
     * 通过条件查询pojo集合
     * @param  where 条件表达式
     * @return Pojo集合
     */
    public List<T> get(Where where) throws Exception;
    
    

    /**
     * 更新不为null的PO字段
     * @param po 
     * @return 受影响的行数<br>
     * <b>注: </b>如果当前pojo类中所有的属性都是null则会抛异常  Exception("没有修改任何列");
     */
    public int update(T po) throws Exception;


    /**
     * 条件更新不为null的字段
     * @param po
     * @param 条件表达式
     * @return 受影响的行数
     * <b>注: </b>如果当前pojo类中所有的属性都是null则会抛异常  Exception("没有修改任何列");
     */
    public int update(T po, Where where) throws Exception;


    /**
     * 删除某个记录
     * @param id 主键
     * @return 受影响的行数
     */
    public int delete(PK id) throws Exception;

    /**
     * 条件删除某个记录
     * @param where 条件表达式
     * @return 受影响的行数
     */
    public int deleteByWherePrams(Where where) throws Exception;

    /**
     * 自定义sql查询
     * @param resultClass 用于封装返回结果的Pojo类
     * @param sql 用于执行查询的Sql
     * @return 结果集合
     */
    public List<? extends Pojo> getBySql(String sql,Class<? extends Pojo> resultClass) throws Exception;

    /**
     * 执行自定义sql
     * <li>更新sql
     * <li>删除sql
     * <li>插入sql
     * @param sql 用于执行的Sql
     * @param args Sql占位付对应的参数
     * @return 受影响的行数
     */
    public int excuse(String sql);

    /**
     * 获取指定条件的记录数
     * @param where 条件表达式
     * @return 查询到的记录数
     */
    public long count(Where where);

    /**
     * 获取对应表中的记录数
     * @return 表中的条数
     */
    public long count();



    /**
     * 是否存在指定条件的记录
     * @param where 条件表达式
     * @return
     */
    public boolean isExist(Where where);


    /**
     * 获得下一个序列的值
     * @return
     */
   public  long nextId();
    
    
    /**
     * 分页<br>
     * @param everyPageCount 每页显示多少数据
     * 	<li>默认显示10条数据
     * @param currentPage 当前第几页
     * 	<li>默认第一页
     * @param where 限制条件
     * */
   public SimplePage<T> page(Integer everyPageCount ,Integer currentPage,Where where) throws Exception;

}