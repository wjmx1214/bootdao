package com.boot.dao.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * JDBC封装接口
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.7
 */
public interface IBaseJDBC {
	
	/**
	 * 批量处理
	 * @param sqls
	 * @return int
	 * @throws Exception 
	 */
	int updateBatchSQL(String[] sqls) throws Exception;
	
	/**
	 * 批量处理
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return int
	 * @throws Exception 
	 */
	int updateBatchSQL(String sql, List<Object[]> params) throws Exception;
	
	/**
	 * 插入一条记录并返回记录ID(自增)
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return long
	 * @throws Exception
	 */
	long insertAndGetId(String sql, Object... params) throws Exception;
	
	/**
	 * 增,删,改
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return int
	 * @throws Exception 
	 */
	int updateSQL(String sql, Object... params) throws Exception;
	
	/**
	 * 获取一列数据
	 * @param sql
	 * @param clz java基础类型
	 * @param params SQL语句中对应的?号参数
	 * @return List<A>
	 */
	<A> List<A> getColumnOne(String sql, Class<A> clz, Object... params);
	
	/**
	 * 获取两列数据，第一列作为key(第一列数据须唯一且不为null，否则将会造成获取到错误数据)<br>
	 * 此唯一指结果集中唯一，数据库中不必唯一，可使用where条件将其过滤成唯一属性
	 * @param sql
	 * @param clz java基础类型
	 * @param params SQL语句中对应的?号参数
	 * @return Map<String, A>
	 */
	<A> Map<String, A> getColumnTwo(String sql, Class<A> clz, Object... params);
	
	/**
	 * 获取Array集合
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return List<Object[]>
	 */
	List<Object[]> getArrays(String sql, Object... params);
	
	/**
	 * 获取Array集合
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return List<String[]>
	 */
	List<String[]> getArraysString(String sql, Object... params);

	/**
	 * 获取Map集合
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> getMaps(String sql, Object... params);
	
	/**
	 * 获取Map集合
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return List<Map<String, String>>
	 */
	List<Map<String, String>> getMapsString(String sql, Object... params);

	/**
	 * 获取entity
	 * @param sql
	 * @param clz
	 * @param params SQL语句中对应的?号参数
	 * @return <T>
	 */
	<T> T getEntity(String sql, Class<T> clz, Object... params);
	
	/**
	 * 获取内部类entity
	 * @param outer	外部类实例
	 * @param sql
	 * @param clz
	 * @param params SQL语句中对应的?号参数
	 * @return <T>
	 */
	<T> T getInnerEntity(Object outer, String sql, Class<T> clz, Object... params);

	/**
	 * 获取entity集合(导出数据请使用Map或数组, 否则可能影响性能)
	 * @param sql
	 * @param clz
	 * @param params SQL语句中对应的?号参数
	 * @return List<T>
	 */
	<T> List<T> getEntitys(String sql, Class<T> clz, Object... params);
	
	/**
	 * 获取内部类entity集合(导出数据请使用Map或数组, 否则可能影响性能)
	 * @param outer	外部类实例
	 * @param sql
	 * @param clz
	 * @param params SQL语句中对应的?号参数
	 * @return List<T>
	 */
	<T> List<T> getInnerEntitys(Object outer, String sql, Class<T> clz, Object... params);
	
	/**
	 * 获取entity集合《key, Entity》形式(导出数据请使用Map或数组, 否则可能影响性能)
	 * @param sql
	 * @param columnName 将指定的列作为key
	 * @param keyClz
	 * @param clz
	 * @param params SQL语句中对应的?号参数
	 * @return Map<A, T> A为[Integer|Long|String]中的一种基础类型(否则返回空集)，T为实体类型
	 */
	<A extends Serializable, T> Map<A, T> getEntitysMap(String sql, String columnName, Class<A> keyClz, Class<T> clz, Object... params);
	
	/**
	 * 获取内部类entity集合《key, Entity》形式(导出数据请使用Map或数组, 否则可能影响性能)
	 * @param outer	外部类实例
	 * @param sql
	 * @param columnName 将指定的列作为key
	 * @param keyClz
	 * @param clz
	 * @param params SQL语句中对应的?号参数
	 * @return Map<A, T> A为[Integer|Long|String]中的一种基础类型(否则返回空集)，T为实体类型
	 */
	<A extends Serializable, T> Map<A, T> getInnerEntitysMap(Object outer, String sql, String columnName, Class<A> keyClz, Class<T> clz, Object... params);

}
