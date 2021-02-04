package com.boot.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Import;

import com.boot.dao.api.IBaseEntityDAO;
import com.boot.dao.api.Page;
import com.boot.dao.api.PageSearch;
import com.boot.dao.util.ApplicationContextUtil;

/**
 * 数据访问封装类(适合需要DAO层的模型, 继承此类直接支持类泛型)<br>
 * 可指定一个带数据源的DAO来构造, 若未指定则默认为BaseDAO
 * @param <T>
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.0
 */
@Import(BaseDAO.class)
public abstract class BaseTDAO<T>{
	
	private Class<T> classT; //泛型的实际类型

	private IBaseEntityDAO DAO;
	
	private Class<?> daoClass;
	
	private String daoName;
	
	public BaseTDAO(){
		getClassType();
	}
	
	public BaseTDAO(String daoName){
		this();
		this.daoName = daoName;
	}
	
	public <D extends BaseEntityDAO> BaseTDAO(Class<D> daoClass){
		this();
		this.daoClass = daoClass;
	}
	
	/**
	 * 更多数据获取方式
	 * @return
	 */
	public IBaseEntityDAO DAO() {
		if(this.DAO == null) {
			if(this.daoClass != null) {
				this.DAO = ApplicationContextUtil.getBean(this.daoClass);
			}
			if(this.daoName != null) {
				this.DAO = ApplicationContextUtil.getBean(this.daoName);
			}
			if(this.DAO == null) {
				this.DAO = ApplicationContextUtil.getBean(BaseDAO.class); //未指定时加载默认DAO
			}
		}
		return this.DAO;
	}
	
	//获取继承的泛型的实际类型
	@SuppressWarnings("unchecked")
	private Class<T> getClassType(){
		if(classT == null){
			Type type = this.getClass().getGenericSuperclass();
			if(type instanceof ParameterizedType){
				Type ptype = ((ParameterizedType)type).getActualTypeArguments()[0];
				if(!"T".equals(ptype.toString())) {
					classT = (Class<T>)ptype;
				}else {
					new Exception("未指定泛型类型!").printStackTrace();
				}
			}
		}
		return classT;
	}
	
	/**
	 * 保存对象(新增或更新)(空字符更新)
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public T save_empty(T t) throws Exception{
		return DAO().save_empty(t);
	}
	
	/**
	 * 保存对象(新增或更新)
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public T save(T t) throws Exception{
		return DAO().save(t);
	}
	
	/**
	 * 根据SQL查找对象集合
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return
	 */
	public List<T> list(String sql, Object... params){
		return DAO().getEntitys(sql, classT, params);
	}
	
	/**
	 * 获取entity集合《key, Entity》形式(导出数据请使用Map或数组, 否则可能影响性能)
	 * @param sql
	 * @param columnNameKey 将指定的列名作为key
	 * @param params SQL语句中对应的?号参数
	 * @return
	 */
	public Map<String, T> listMap(String sql, String columnNameKey, Object... params){
		return DAO().getEntitysMap(sql, columnNameKey, classT, params);
	}
	
	/**
	 * 删除
	 * @param t
	 * @throws Exception
	 */
	public void delete(T t) throws Exception{
		DAO().delete(t);
	}
	
	/**
	 * 根据主键删除
	 * @param pk
	 * @throws Exception
	 */
	public void delete(Serializable pk) throws Exception{
		DAO().delete(pk, classT);
	}
	
	/**
	 * 根据SQL查找对象
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return
	 */
	public T get(String sql, Object... params){
		return DAO().getEntity(sql, classT, params);
	}
	
	/**
	 * 根据主键查找对象
	 * @param pk
	 * @return
	 */
	public T get(Serializable pk){
		return DAO().getByPK(pk, classT);
	}
	
	/**
	 * 根据唯一列查找对象
	 * @param columnName
	 * @param value
	 * @return
	 */
	public T getByColumn(String columnName, Object value){
		return DAO().getByColumn(columnName, value, classT);
	}

	/**
	 * 分页包装(目前仅支持MYSQL)
	 * @param search
	 * @return
	 */
	public Page<T> page(PageSearch search){
		return DAO().page(search, classT);
	}
	
	/**
	 * 分页包装(目前仅支持MYSQL)
	 * @param search
	 * @return Map《String, Object》
	 */
	@SuppressWarnings("rawtypes")
	public Page<Map> pageMap(PageSearch search){
		return DAO().pageMap(search);
	}

}