package com.briup.base.jdbc.service;

import java.io.Serializable;
import java.util.List;

import com.briup.base.jdbc.bean.Pojo;

public interface IBaseService<M extends Pojo, PK extends Serializable> {

	/**
	 * 根据id查询唯一对象
	 * @param id
	 * @return 对象
	 */
	M findById(PK id) throws Exception;
	
	/**
	 * 查询所有对象
	 * @param orders 排序规则 
	 * 	<li> eg: findAll("id") 
	 * 	<li> eg: findAll("id desc") 
	 * 	<li> eg: findAll("id ,name asc") 
	 * @return 对象集合
	 */
	List<M> findAll(String orders)throws Exception;
	/**
	 * 通过sql查询数据<br>
	 * @param sql 能正常执行的sql语句
	 * @param resultClass 该sql语句返回的结果集封装为什么类型，这个类型必须是Pojo的子类型
	 * @return resultClass类型的集合
	 * 
	 * */
    public List<? extends Pojo> findBySql(String sql,Class<? extends Pojo> resultClass) throws Exception;
	/**
	 * 通过某一个属性名和属性值 查询对象
	 * */
	List<M> findByPram(String field,Serializable value)throws Exception;
	
	/**
	 * 保存
	 * @param model 对象
	 */
	void save(M model)throws Exception;
	
	/**
	 * 批量保存
	 * @param models 对象集合
	 */
	void batchSave(List<M> models)throws Exception;
	
	/**
	 * 保存或者更新<br>
	 * <li>id!=null 更新 
	 * <li>id==null 保存
	 * @param model 对象
	 */
	void saveOrUpdate(M model)throws Exception;
	
	/**
	 * 更新<br>
	 * 通过id更新对象 
	 * @param model 对象
	 */
	void update(M model)throws Exception;
	
	/**
	 * 根据id删除
	 * @param id
	 */
	void delete(PK id)throws Exception;
	
	/**
	 * 批量删除
	 * @param ids
	 */
	void batchDelete(List<PK> ids)throws Exception;
	
	
}
