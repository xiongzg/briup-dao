package com.briup.base.jdbc.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.briup.base.jdbc.annocation.ColumnName;
import com.briup.base.jdbc.bean.Pojo;
import com.briup.base.jdbc.bean.Where;
import com.briup.base.jdbc.dao.IBaseDao;
import com.briup.base.jdbc.util.Condition;
import com.briup.base.jdbc.util.GenericsUtils;
import com.briup.base.jdbc.util.SqlUtil;
/**
 * base业务逻辑层实现类<br>
 * 需要 pojo类<br>
 * 需要pojo类主键对应的数据类型 <br>
 * */
public abstract class BaseServiceImpl<M extends Pojo, PK extends Serializable> implements IBaseService<M,PK>{
	
	private Class<M> entityClass;
	
	@SuppressWarnings("unchecked")
	public BaseServiceImpl() {
		 this.entityClass = (Class<M>) GenericsUtils.getSuperClassGenricType(this.getClass());
	}

	public abstract IBaseDao<M,PK> getDao();
	
	@Override
	public M findById(PK id)throws Exception {
		
		M m = getDao().get(id);
		if(m!=null){
			return m;
		}else{
			return null;
		}
	}
	
	@Override
	public List<M> findAll(String orders)throws Exception {
		IBaseDao<M, PK> dao = getDao();
		Where where = new Where(null, null, null);
		where.orderBy(orders);
		List<M> list = dao.get(where);
		if(list!=null && list.size()>0){
			return list;
		}else{
			return null;
		}
	}
	@Override
	 public List<? extends Pojo> findBySql(String sql,Class<? extends Pojo> resultClass) throws Exception{
		 List<? extends Pojo> list = getDao().getBySql(sql,entityClass);
		if(list!=null && list.size()>0){
			return list;
		}else{
			return null;
		}
	}
	@Override
	public List<M> findByPram(String field,Serializable value) throws Exception{
		List<M> list = getDao().get(new Where(getColumnNameByFieldName(field), Condition.EQ, value));
		if(list!=null && list.size()>0){
			return list;
		}else{
			return null;
		}
	}
	@Override
	public void save(M model)throws Exception {
		getDao().save(model);
	}
	@Override
	public void batchSave(List<M> models)throws Exception {
		for(M m: models){
			save(m);
		}
	}
	
	@Override
	public void saveOrUpdate(M model)throws Exception {
		PK id = getIdByPojo(model);
		if (id == null || id.equals(0)) {// 没有传入id 保存
			save(model);
		} else {// 更新
			update(model);
		}
	}
	@Override
	public void update(M model)throws Exception {
		Where where = new Where("id", Condition.EQ,getIdByPojo(model));
		getDao().update(model, where);
	}
	
	@Override
	public void delete(PK id)throws Exception {
		getDao().delete(id);
	}
	@Override
	public void batchDelete(List<PK> ids) throws Exception{
		for(PK id :ids){
			delete(id);
		}
	}
	/**
	 * 辅助方法: 获得pojo类id属性值
	 * */
	private PK getIdByPojo(M model)throws Exception{
		Class<? extends Pojo> clazz = model.getClass();
		Method method = null;
		try {
			method = clazz.getMethod("getId");
			@SuppressWarnings("unchecked")
			PK invoke = (PK) method.invoke(model);
			return invoke;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 辅助方法: 通过pojo类的属性名获得对应数据库表中列的名字<br>
	 * @param field pojo类中的属性名
	 * @return 该pojo类对应的列名
	 * */
	protected String getColumnNameByFieldName(String fieldName)throws Exception {
		SqlUtil sqlUtil = new SqlUtil();
		Field f = sqlUtil.getField(this.entityClass, fieldName);
		if (null == f) {
			System.err.println("查询字段失败(无法找到" + this.entityClass + "中的" + fieldName + "字段)");
			return null;
		}
		if (f.isAnnotationPresent(ColumnName.class)) {
			String columnName = f.getAnnotation(ColumnName.class).name();
			return columnName;
		}
		return fieldName;
	}
}
