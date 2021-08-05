package com.boot.dao.api;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 实体封装接口
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.7
 */
public interface IBaseEntityDAO extends IBaseJDBC{

	/**
	 * 获取boolean型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return boolean
	 */
	boolean getboolean(String sql, Object... params);
	
	/**
	 * 获取int型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return int
	 */
	int getint(String sql, Object... params);
	
	/**
	 * 获取Integer型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Integer
	 */
	Integer getInteger(String sql, Object... params);
	
	/**
	 * 获取long型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return long
	 */
	long getlong(String sql, Object... params);
	
	/**
	 * 获取Long型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Long
	 */
	Long getLong(String sql, Object... params);
	
	/**
	 * 获取float型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return float
	 */
	float getfloat(String sql, Object... params);
	
	/**
	 * 获取Float型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Float
	 */
	Float getFloat(String sql, Object... params);
	
	/**
	 * 获取double型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return double
	 */
	double getdouble(String sql, Object... params);
	
	/**
	 * 获取Double型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Double
	 */
	Double getDouble(String sql, Object... params);
	
	/**
	 * 获取String型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return String
	 */
	String getString(String sql, Object... params);
	
	/**
	 * 获取java.util.Date型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Date
	 */
	Date getDate(String sql, Object... params);
	
	/**
	 * 获取byte[]型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return byte[]
	 */
	byte[] getbytes(String sql, Object... params);
	
	/**
	 * 获取BigDecimal型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return BigDecimal
	 */
	BigDecimal getBigDecimal(String sql, Object... params);
	
	/**
	 * 获取Object型单值(java基础类型)
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return <A>
	 */
	<A> A getObject(String sql, Object... params);
	
	/**
	 * 获取一行数据
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Map<String, Object>
	 */
	Map<String, Object> getRowOne(String sql, Object... params);
	
	/**
	 * 获取一行数据
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Map<String, String>
	 */
	Map<String, String> getRowOneString(String sql, Object... params);
	
	/**
	 * 新增或更新
	 * @param t
	 * @return <T>
	 * @throws Exception
	 */
	<T> T save(T t) throws Exception;
	
	/**
	 * 新增或更新(空字符更新)
	 * @param t
	 * @return <T>
	 * @throws Exception
	 */
	<T> T save_empty(T t) throws Exception;
	
	/**
	 * 删除
	 * @param t
	 * @return boolean
	 * @throws Exception
	 */
	<T> boolean delete(T t) throws Exception;
	
	/**
	 * 根据主键删除
	 * @param pk
	 * @param clz
	 * @return boolean
	 * @throws Exception
	 */
	<T> boolean delete(Serializable pk, Class<T> clz) throws Exception;
	
	/**
	 * 根据主键查找对象
	 * @param pk
	 * @param clz
	 * @return <T>
	 */
	<T> T findByPK(Serializable pk, Class<T> clz);
	
	/**
	 * 根据唯一属性查找对象(需要有对应的列名映射)
	 * @param fieldName
	 * @param value
	 * @param clz
	 * @return <T>
	 */
	<T> T findByUniqueField(String fieldName, Object value, Class<T> clz);
	
	/**
	 * 根据唯一列查找对象
	 * @param columnName
	 * @param value
	 * @param clz
	 * @return <T>
	 */
	<T> T findByUniqueColumn(String columnName, Object value, Class<T> clz);
	
	/**
	 * 根据单个条件查找对象集合(属性名, 需要有对应的列名映射)
	 * @param fieldName
	 * @param value
	 * @param searchType
	 * @param clz
	 * @return List<T>
	 */
	<T> List<T> findByWhereField(String fieldName, Object value, SearchType searchType, Class<T> clz);
	
	/**
	 * 根据单个条件查找对象集合(列名)
	 * @param columnName
	 * @param value
	 * @param searchType
	 * @param clz
	 * @return List<T>
	 */
	<T> List<T> findByWhereColumn(String columnName, Object value, SearchType searchType, Class<T> clz);
	
	/**
	 * 根据主键更新单值(属性名)
	 * @param pk
	 * @param clz
	 * @param fieldName
	 * @param value
	 * @return int
	 * @throws Exception
	 */
	<T> int updateFieldByPK(Serializable pk, Class<T> clz, String fieldName, Object value) throws Exception;
	
	/**
	 * 根据主键更新单值(列名)
	 * @param pk
	 * @param clz
	 * @param columnName
	 * @param value
	 * @return int
	 * @throws Exception
	 */
	<T> int updateColumnByPK(Serializable pk, Class<T> clz, String columnName, Object value) throws Exception;
	
	/**
	 * 分页包装, 单表且无子查询可省略SQL(目前仅支持MYSQL)
	 * @param search
	 * @param clz
	 * @return Page<T>
	 */
	<T> Page<T> page(PageSearch search, Class<T> clz);
	
	/**
	 * 分页包装(目前仅支持MYSQL)
	 * @param search
	 * @return Page<Map<String, Object>>
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
	 * @return Page<T>
	 */
	<T> Page<T> page(int pageIndex, int pageSize, String sql, Class<T> clz, Object... params);
	
	/**
	 * 分页包装(目前仅支持MYSQL)
	 * @param pageIndex
	 * @param pageSize
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Page<Map<String, Object>>
	 */
	@SuppressWarnings("rawtypes")
	Page<Map> pageMap(int pageIndex, int pageSize, String sql, Object... params);
	
}
