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
import com.boot.dao.api.SearchType;
import com.boot.dao.util.ApplicationContextUtil;
import com.boot.dao.util.BaseDAOLog;

/**
 * 数据访问封装类(适合需要DAO层的模型, 继承此类直接支持类泛型)<br>
 * 可指定一个带数据源的DAO来构造, 若未指定则默认为BaseDAO
 * @param <T>
 * @author 2020-12-01 create wang.jia.le
 * @version 1.1.0
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
	 * @return IBaseEntityDAO
	 */
	public IBaseEntityDAO DAO() {
		if(this.DAO == null) {
			if(this.daoClass != null) {
				this.DAO = ApplicationContextUtil.getBean(this.daoClass);
			}else if(this.daoName != null) {
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
					BaseDAOLog.printException(new Exception("未指定泛型类型!"));
				}
			}
		}
		return classT;
	}

	/**
	 * 保存对象(新增或更新)
	 * @param t
	 * @return <T>
	 * @throws Exception
	 */
	public T save(T t) throws Exception{
		return DAO().save(t);
	}

	/**
	 * 保存对象(新增或更新)(空字符更新)
	 * @param t
	 * @return <T>
	 * @throws Exception
	 */
	public T save_empty(T t) throws Exception{
		return DAO().save_empty(t);
	}
	
	/**
	 * 根据SQL查找对象集合
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return List<T>
	 */
	public List<T> list(String sql, Object... params){
		return DAO().getEntitys(sql, classT, params);
	}
	
	/**
	 * 获取entity集合《key, Entity》形式(导出数据请使用Map或数组, 否则可能影响性能)
	 * @param sql
	 * @param columnName 将指定的列作为key
	 * @param params SQL语句中对应的?号参数
	 * @return Map<A, T> A为[Integer|Long|String]中的一种基础类型(否则返回空集)
	 */
	public <A extends Serializable> Map<A, T> listMap(String sql, String columnName, Class<A> clzA, Object... params){
		return DAO().getEntitysMap(sql, columnName, clzA, classT, params);
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
	 * @return boolean
	 * @throws Exception
	 */
	public boolean delete(Serializable pk) throws Exception{
		return DAO().delete(pk, classT);
	}
	
	/**
	 * 根据SQL查找对象
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return <T>
	 */
	public T get(String sql, Object... params){
		return DAO().getEntity(sql, classT, params);
	}
	
	/**
	 * 根据主键查找对象
	 * @param pk
	 * @return <T>
	 */
	public T findByPK(Serializable pk){
		return DAO().findByPK(pk, classT);
	}
	
	/**
	 * 根据唯一属性查找对象(需要有对应的列名映射)
	 * @param fieldName
	 * @param value
	 * @return <T>
	 */
	public T findByUniqueField(String fieldName, Object value){
		return DAO().findByUniqueField(fieldName, value, classT);
	}
	
	/**
	 * 根据唯一列查找对象
	 * @param columnName
	 * @param value
	 * @return <T>
	 */
	public T findByUniqueColumn(String columnName, Object value) {
		return DAO().findByUniqueColumn(columnName, value, classT);
	}
	
	/**
	 * 根据单个条件查找对象集合(属性名, 需要有对应的列名映射)<br>
	 * 多个参数需要用String类型并以逗号进行分隔
	 * @param fieldName
	 * @param value
	 * @param searchType
	 * @return List<T>
	 */
	public List<T> findByWhereField(String fieldName, Object value, SearchType searchType){
		return DAO().findByWhereField(fieldName, value, searchType, classT);
	}
	
	/**
	 * 根据单个条件查找对象集合(列名)<br>
	 * 多个参数需要用String类型并以逗号进行分隔
	 * @param columnName
	 * @param value
	 * @param searchType
	 * @return List<T>
	 */
	public List<T> findByWhereColumn(String columnName, Object value, SearchType searchType){
		return DAO().findByWhereColumn(columnName, value, searchType, classT);
	}
	
	/**
	 * 根据主键更新单值(属性名)
	 * @param pk
	 * @param fieldName
	 * @param value
	 * @return int
	 * @throws Exception
	 */
	public int updateFieldByPK(Serializable pk, String fieldName, Object value) throws Exception{
		return DAO().updateFieldByPK(pk, classT, fieldName, value);
	}
	
	/**
	 * 根据主键更新单值(列名)
	 * @param pk
	 * @param columnName
	 * @param value
	 * @return int
	 * @throws Exception
	 */
	public int updateColumnByPK(Serializable pk, String columnName, Object value) throws Exception{
		return DAO().updateColumnByPK(pk, classT, columnName, value);
	}

	/**
	 * 分页包装, 单表且无子查询可省略SQL(目前仅支持LIMIT)
	 * @param search
	 * @return Page<T>
	 */
	public Page<T> page(PageSearch search){
		return DAO().page(search, classT);
	}
	
	/**
	 * 分页包装(目前仅支持LIMIT)
	 * @param search
	 * @return Page<Map<String, Object>>
	 */
	@SuppressWarnings("rawtypes")
	public Page<Map> pageMap(PageSearch search){
		return DAO().pageMap(search);
	}

}
