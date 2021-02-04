package com.boot.dao.api;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 实体封装接口
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.0
 */
public interface IBaseEntityDAO extends IBaseJDBC{

	/**
	 * 获取boolean型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	boolean getboolean(String sql, Object... params);
	
	/**
	 * 获取int型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	int getint(String sql, Object... params);
	
	/**
	 * 获取Integer型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	Integer getInteger(String sql, Object... params);
	
	/**
	 * 获取long型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	long getlong(String sql, Object... params);
	
	/**
	 * 获取Long型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	Long getLong(String sql, Object... params);
	
	/**
	 * 获取float型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	float getfloat(String sql, Object... params);
	
	/**
	 * 获取Float型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	Float getFloat(String sql, Object... params);
	
	/**
	 * 获取double型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	double getdouble(String sql, Object... params);
	
	/**
	 * 获取Double型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	Double getDouble(String sql, Object... params);
	
	/**
	 * 获取String型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	String getString(String sql, Object... params);
	
	/**
	 * 获取java.util.Date型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	Date getDate(String sql, Object... params);
	
	/**
	 * 获取byte[]型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	byte[] getbytes(String sql, Object... params);
	
	/**
	 * 获取BigDecimal型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	BigDecimal getBigDecimal(String sql, Object... params);
	
	/**
	 * 获取Object型单值(java基础类型)
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	<A> A getObject(String sql, Object... params);
	
	/**
	 * 获取一行数据
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	Map<String, Object> getRowOne(String sql, Object... params);
	
	/**
	 * 获取一行数据
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return 
	 */
	Map<String, String> getRowOneString(String sql, Object... params);

	/**
	 * 新增或更新(空字符更新)
	 * @param t
	 * @return 
	 * @throws Exception
	 */
	<T> T save_empty(T t) throws Exception;
	
	/**
	 * 新增或更新
	 * @param t
	 * @return 
	 * @throws Exception
	 */
	<T> T save(T t) throws Exception;
	
	/**
	 * 删除
	 * @param t
	 * @throws Exception
	 */
	<T> void delete(T t) throws Exception;
	
	/**
	 * 根据主键删除
	 * @param pk
	 * @param clz
	 * @throws Exception
	 */
	<T> void delete(Serializable pk, Class<T> clz) throws Exception;
	
	/**
	 * 根据主键查找对象
	 * @param pk
	 * @param clz
	 * @return 
	 */
	<T> T getByPK(Serializable pk, Class<T> clz);
	
	/**
	 * 根据唯一列查找对象
	 * @param columnName
	 * @param value
	 * @param clz
	 * @return 
	 */
	<T> T getByColumn(String columnName, Object value, Class<T> clz);
	
	/**
	 * 分页包装(目前仅支持MYSQL)
	 * @param search
	 * @param clz
	 * @return
	 */
	<T> Page<T> page(PageSearch search, Class<T> clz);
	
	/**
	 * 分页包装(目前仅支持MYSQL)
	 * @param search
	 * @return Map《String, Object》
	 */
	@SuppressWarnings("rawtypes")
	Page<Map> pageMap(PageSearch search);

	/**
	 * 分页包装(目前仅支持MYSQL)
	 * @param pageIndex
	 * @param pageSize
	 * @param sql
	 * @param clz
	 * @param params SQL语句中对应的?号参数
	 * @return
	 */
	<T> Page<T> page(int pageIndex, int pageSize, String sql, Class<T> clz, Object... params);
	
	/**
	 * 分页包装(目前仅支持MYSQL)
	 * @param pageIndex
	 * @param pageSize
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Map《String, Object》
	 */
	@SuppressWarnings("rawtypes")
	Page<Map> pageMap(int pageIndex, int pageSize, String sql, Object... params);
	
}