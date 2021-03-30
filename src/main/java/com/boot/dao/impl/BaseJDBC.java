package com.boot.dao.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.boot.dao.api.IBaseJDBC;
import com.boot.dao.config.BaseDAOConfig;
import com.boot.dao.mapping.BaseColumnMapping;
import com.boot.dao.mapping.BaseMappingCache;
import com.boot.dao.mapping.BaseTableMapping;
import com.boot.dao.util.BaseDAOLog;
import com.boot.dao.util.BaseDAOUtil;

/**
 * JDBC封装类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.3
 */
public abstract class BaseJDBC extends BaseSource implements IBaseJDBC{

	public BaseJDBC(String dataSourceName) {
		super(dataSourceName);
	}
	
	//----------------------------------------------------------增删改-----------------------------------------------------

	/**
	 * 批量处理
	 * @param sqls
	 * @return int
	 * @throws Exception 
	 */
	@Override
	public int updateBatchSQL(String[] sqls) throws Exception{
		int count = 0;//受影响行数
		PreparedStatement ps = null;
		try {
			for (int i = 0; i < sqls.length; i++) {
				BaseDAOLog.printSQLAndParam(BaseDAOConfig.showSQL, BaseDAOConfig.showParam, sqls[i]);
				if(i == 0) {
					ps = super.getConnection().prepareStatement(sqls[0]);
					ps.addBatch();
				}else{
					ps.addBatch(sqls[i]);
				}
			}
			int[] arrayCount = ps.executeBatch();
			for(int i=0; i<arrayCount.length; i++) {
				count+=arrayCount[i];
			}

		} catch (Exception e) {
			throw e;
		}finally{
			this.close(ps, null);
		}
		return count;
	}
	
	/**
	 * 批量处理
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return int
	 * @throws Exception 
	 */
	@Override
	public int updateBatchSQL(String sql, List<Object[]> params) throws Exception{
		int count = 0;//受影响行数
		PreparedStatement ps = null;
		try {
			if(params != null){
				for (int i = 0; i < params.size(); i++) {
					if(i == 0) {
						BaseDAOLog.printSQLAndParam(BaseDAOConfig.showSQL, false, sql, params);
						ps = super.getConnection().prepareStatement(sql);
						ps.addBatch();
					}else{
						ps.addBatch(sql);
					}
				}
				for (int i = 0; i < params.size(); i++) {
					BaseDAOLog.printSQLAndParam(false, BaseDAOConfig.showParam, sql, params);
					BaseDAOUtil.setParams(ps, params.get(i)); //设置参数
				}
			}
			int[] arrayCount = ps.executeBatch();
			for(int i=0; i<arrayCount.length; i++) {
				count+=arrayCount[i];
			}

		} catch (Exception e) {
			throw e;
		}finally{
			this.close(ps, null);
		}
		return count;
	}
	
	/**
	 * 插入一条记录并返回记录ID(自增)
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return long
	 * @throws Exception
	 */
	@Override
	public long insertAndGetId(String sql, Object... params) throws Exception{
		BaseDAOLog.printSQLAndParam(BaseDAOConfig.showSQL, BaseDAOConfig.showParam, sql, params);
		long id = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = super.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			BaseDAOUtil.setParams(ps, params); //设置参数
			int count = ps.executeUpdate();
			if(count == 1){//获取ID
				rs = ps.getGeneratedKeys();
				if(rs.next())	id = rs.getLong(1);
			}
		}catch(Exception e){
			throw e;
		}finally{
			this.close(ps, rs);
		}
		return id;
	}
	
	/**
	 * 增,删,改
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return int
	 * @throws Exception 
	 */
	@Override
	public int updateSQL(String sql, Object... params) throws Exception{
		BaseDAOLog.printSQLAndParam(BaseDAOConfig.showSQL, BaseDAOConfig.showParam, sql, params);
		int count = 0;//受影响行数
		PreparedStatement ps = null;
		try{
			ps = super.getConnection().prepareStatement(sql);
			BaseDAOUtil.setParams(ps, params); //设置参数
			count = ps.executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			this.close(ps, null);
		}
		return count;
	}
	
	/**
	 * 关闭资源(由于涉及事务问题，Connection资源交由spring处理)
	 * @param ps
	 * @param rs
	 */
	protected void close(PreparedStatement ps, ResultSet rs){
		try {
			if(rs != null) rs.close();
			if(ps != null) ps.close();
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		}
	}
	
	//----------------------------------------------------------查询-----------------------------------------------------
	
	/**
	 * 获取Object型单值
	 * @param sql
	 * @param clz java基础类型
	 * @param params SQL语句中对应的?号参数
	 * @return <A>
	 */
	protected <A> A getObject(String sql, Class<A> clz,  Object... params){
		BaseJDBCQuery jq = new BaseJDBCQuery();
		try{
			jq.query(super.getConnection(), sql, params);
			while (jq.rs.next()){
				return this.getValueByJavaType(jq.rs, 1, clz);
			}
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		} finally{
			jq.close();
		}
		return null;
	}
	
	/**
	 * 获取一列数据
	 * @param sql
	 * @param clz java基础类型
	 * @param params SQL语句中对应的?号参数
	 * @return List<A>
	 */
	@Override
	public <A> List<A> getColumnOne(String sql, Class<A> clz, Object... params){
		List<A> list = new ArrayList<>();
		BaseJDBCQuery jq = new BaseJDBCQuery();
		try{
			jq.query(super.getConnection(), sql, params);
			while (jq.rs.next()){
				list.add(this.getValueByJavaType(jq.rs, 1, clz));
			}
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		} finally{
			jq.close();
		}
		return list;
	}
	
	/**
	 * 获取两列数据，第一列作为key(第一列数据须唯一且不为null，否则将会造成获取到错误数据)<br>
	 * 此唯一指结果集中唯一，数据库中不必唯一，可使用where条件将其过滤成唯一属性
	 * @param sql
	 * @param clz java基础类型
	 * @param params SQL语句中对应的?号参数
	 * @return Map<String, A>
	 */
	@Override
	public <A> Map<String, A> getColumnTwo(String sql, Class<A> clz, Object... params){
		Map<String, A> map = new LinkedHashMap<>();
		BaseJDBCQuery jq = new BaseJDBCQuery();
		try{
			jq.query(super.getConnection(), sql, params);
			while (jq.rs.next()){
				String key = jq.rs.getString(1);
				if(!isBlank(key)) {
					map.put(key, this.getValueByJavaType(jq.rs, 2, clz));
				}
			}
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		} finally{
			jq.close();
		}
		return map;
	}
	
	/**
	 * 获取Array集合
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return List<Object[]>
	 */
	@Override
	public List<Object[]> getArrays(String sql, Object... params){
		List<Object[]> list = new ArrayList<>();
		BaseJDBCQuery jq = new BaseJDBCQuery();
		try{
			jq.query(super.getConnection(), sql, params);
			while (jq.rs.next()){
				Object[] array = new Object[jq.columnCount];
				for(int i=0; i<jq.columnCount; i++)
					array[i] = jq.rs.getObject(i+1);
				list.add(array);
			}
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		} finally{
			jq.close();
		}
		return list;
	}
	
	/**
	 * 获取Array集合
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return List<String[]>
	 */
	@Override
	public List<String[]> getArraysString(String sql, Object... params){
		List<String[]> list = new ArrayList<>();
		BaseJDBCQuery jq = new BaseJDBCQuery();
		try{
			jq.query(super.getConnection(), sql, params);
			while (jq.rs.next()){
				String[] array = new String[jq.columnCount];
				for(int i=0; i<jq.columnCount; i++)
					array[i] = jq.rs.getString(i+1);
				list.add(array);
			}
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		} finally{
			jq.close();
		}
		return list;
	}

	/**
	 * 获取Map集合
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return List<Map<String, Object>>
	 */
	@Override
	public List<Map<String, Object>> getMaps(String sql, Object... params){
		List<Map<String, Object>> list = new ArrayList<>();
		BaseJDBCQuery jq = new BaseJDBCQuery();
		try{
			jq.query(super.getConnection(), sql, params);
			while (jq.rs.next()){
				Map<String, Object> map = new LinkedHashMap<>();
				for(int i=0; i<jq.columnCount; i++)
					map.put(jq.rs.getMetaData().getColumnLabel(i+1), jq.rs.getObject(i+1));
				list.add(map);
			}
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		} finally{
			jq.close();
		}
		return list;
	}
	
	/**
	 * 获取Map集合
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return List<Map<String, String>>
	 */
	@Override
	public List<Map<String, String>> getMapsString(String sql, Object... params){
		List<Map<String, String>> list = new ArrayList<>();
		BaseJDBCQuery jq = new BaseJDBCQuery();
		try{
			jq.query(super.getConnection(), sql, params);
			while (jq.rs.next()){
				Map<String, String> map = new LinkedHashMap<>();
				for(int i=0; i<jq.columnCount; i++)
					map.put(jq.rs.getMetaData().getColumnLabel(i+1), jq.rs.getString(i+1));
				list.add(map);
			}
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		} finally{
			jq.close();
		}
		return list;
	}
	
	//-------------------------------------------------------对象查询-----------------------------------------------------
	
	/**
	 * 获取entity
	 * @param sql
	 * @param clz
	 * @param params SQL语句中对应的?号参数
	 * @return <T>
	 */
	@Override
	public <T> T getEntity(String sql, Class<T> clz, Object... params){
		return this.getInnerEntity(null, sql, clz, params);
	}
	
	/**
	 * 获取内部类entity
	 * @param outer	外部类实例
	 * @param sql
	 * @param clz
	 * @param params SQL语句中对应的?号参数
	 * @return <T>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getInnerEntity(Object outer, String sql, Class<T> clz, Object... params){
		T t = null;
		BaseJDBCQuery jq = new BaseJDBCQuery();
		try{
			BaseTableMapping tm = BaseMappingCache.getTableMapping(clz);
			jq.query(super.getConnection(), sql, params);
			while (jq.rs.next()){
				if(outer == null) {
					t = clz.getDeclaredConstructor().newInstance();//动态生成泛型的实例
				}else {
					t = (T)clz.getDeclaredConstructors()[0].newInstance(outer);//动态生成泛型的实例(内部类，通过外部类实例创建)
				}
				for(int i=0; i<jq.columnCount; i++) {
					BaseColumnMapping cm = tm.columnMappings.get(jq.rs.getMetaData().getColumnLabel(i+1).toLowerCase());
					if(cm != null) {
						Object value = this.getValueByJavaType(jq.rs, i+1, cm.field.getType(), cm.formatDate);
						if(value != null) {
							cm.field.set(t, value); //为字段赋值
						}
					}
				}
				break;
			}
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		} finally{
			jq.close();
		}
		return t;
	}

	/**
	 * 获取entity集合(导出数据请使用Map或数组, 否则可能影响性能)
	 * @param sql
	 * @param clz
	 * @param params SQL语句中对应的?号参数
	 * @return List<T>
	 */
	@Override
	public <T> List<T> getEntitys(String sql, Class<T> clz, Object... params){
		return this.getInnerEntitys(null, sql, clz, params);
	}
	
	/**
	 * 获取内部类entity集合(导出数据请使用Map或数组, 否则可能影响性能)
	 * @param outer	外部类实例
	 * @param sql
	 * @param clz
	 * @param params SQL语句中对应的?号参数
	 * @return List<T>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getInnerEntitys(Object outer, String sql, Class<T> clz, Object... params){
		List<T> list = new ArrayList<>();
		BaseJDBCQuery jq = new BaseJDBCQuery();
		try{
			jq.query(BaseMappingCache.getTableMapping(clz), super.getConnection(), sql, params);
			while (jq.rs.next()){
				T t = null;
				if(outer == null) {
					t = clz.getDeclaredConstructor().newInstance();//动态生成泛型的实例
				}else {
					t = (T)clz.getDeclaredConstructors()[0].newInstance(outer);//动态生成泛型的实例(内部类，通过外部类实例创建)
				}
				for(int i=0; i<jq.columnCount; i++) {
					BaseColumnMapping cm = jq.resultColumns.get(i);
					if(cm != null) {
						Object value = this.getValueByJavaType(jq.rs, i+1, cm.field.getType(), cm.formatDate);
						if(value != null) {
							cm.field.set(t, value); //为字段赋值
						}
					}
				}
				list.add(t);
			}
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		} finally{
			jq.close();
		}
		return list;
	}
	
	/**
	 * 获取entity集合《key, Entity》形式(导出数据请使用Map或数组, 否则可能影响性能)
	 * @param sql
	 * @param columnName 将指定的列作为key
	 * @param clz
	 * @param params SQL语句中对应的?号参数
	 * @return Map<String, T>
	 */
	@Override
	public <T> Map<String, T> getEntitysMap(String sql, String columnName, Class<T> clz, Object... params){
		return this.getInnerEntitysMap(null, sql, columnName, clz, params);
	}
	
	/**
	 * 获取内部类entity集合《key, Entity》形式(导出数据请使用Map或数组, 否则可能影响性能)
	 * @param outer	外部类实例
	 * @param sql
	 * @param columnName 将指定的列作为key
	 * @param clz
	 * @param params SQL语句中对应的?号参数
	 * @return Map<String, T>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Map<String, T> getInnerEntitysMap(Object outer, String sql, String columnName, Class<T> clz, Object... params){
		Map<String, T> map = new LinkedHashMap<>();
		BaseJDBCQuery jq = new BaseJDBCQuery();
		try{
			jq.query(BaseMappingCache.getTableMapping(clz), super.getConnection(), sql, params);
			while (jq.rs.next()){
				T t = null;
				if(outer == null) {
					t = clz.getDeclaredConstructor().newInstance();//动态生成泛型的实例
				}else {
					t = (T)clz.getDeclaredConstructors()[0].newInstance(outer);//动态生成泛型的实例(内部类，通过外部类实例创建)
				}
				String key = null;
				for(int i=0; i<jq.columnCount; i++) {
					BaseColumnMapping cm = jq.resultColumns.get(i);
					if(cm != null) {
						Object value = this.getValueByJavaType(jq.rs, i+1, cm.field.getType(), cm.formatDate);
						if(value != null) {
							cm.field.set(t, value); //为字段赋值
							if(cm.columnName.equals(columnName)) {
								key = value.toString();
							}
						}
					}
				}
				if(key != null) {
					map.put(key, t);
				}
			}
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		} finally{
			jq.close();
		}
		return map;
	}
	
	/**
	 * 根据java基础类型从ResultSet获取数据
	 * @param rs
	 * @param index
	 * @param clz java基础类型
	 * @return <A>
	 * @throws SQLException
	 */
	protected <A> A getValueByJavaType(ResultSet rs, int index, Class<A> clz) throws SQLException{
		return getValueByJavaType(rs, index, clz, null);
	}
	
	/**
	 * 根据java基础类型从ResultSet获取数据
	 * @param rs
	 * @param index
	 * @param clz java基础类型
	 * @return <A>
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	protected <A> A getValueByJavaType(ResultSet rs, int index, Class<A> clz, String formatTime) throws SQLException{
		Object a = null;

		if(clz == String.class){
			Object value = rs.getObject(index);
			if(value != null) {
				if(value instanceof Date){
					a = this.formatDate(((Date)value).getTime(), formatTime);
				}else {
					a = value.toString();
				}
			}
		}
		
		//数值类型
		else if(clz == Integer.class) {
			String value = rs.getString(index);
			a = (value == null) ? null : Integer.valueOf(value);
		}else if(clz == Long.class){
			String value = rs.getString(index);
			a = (value == null) ? null : Long.valueOf(value);
		}else if(clz == Float.class){
			String value = rs.getString(index);
			a = (value == null) ? null : Float.valueOf(value);
		}else if(clz == Double.class){
			String value = rs.getString(index);
			a = (value == null) ? null : Double.valueOf(value);
		}else if(clz == int.class) {
			a = rs.getInt(index);
		}else if(clz == long.class){
			a = rs.getLong(index);
		}else if(clz == float.class){
			a = rs.getFloat(index);
		}else if(clz == double.class){
			a = rs.getDouble(index);
		}

		//时间类型
		else if(clz == Date.class){
			a = rs.getTimestamp(index);
		}else if(clz == Timestamp.class){
			a = rs.getTimestamp(index);
		}else if(clz == java.sql.Date.class){
			a = rs.getDate(index);
		}else if(clz == Time.class){
			a = rs.getTime(index);
		}else if(clz == LocalDateTime.class){
			Timestamp value = rs.getTimestamp(index);
			a = (value == null) ? null : value.toLocalDateTime();
		}else if(clz == LocalDate.class){
			java.sql.Date value = rs.getDate(index);
			a = (value == null) ? null : value.toLocalDate();
		}else if(clz == LocalTime.class){
			Time value = rs.getTime(index);
			a = (value == null) ? null : value.toLocalTime();
		}
		
		//其它类型
		else if(clz == Boolean.class || clz == boolean.class){
			a = rs.getBoolean(index);
		}else if(clz == Byte[].class || clz == byte[].class){
			a = rs.getBytes(index);
		}else if(clz == BigDecimal.class){
			a = rs.getBigDecimal(index);
		}else if(clz == Short.class){
			String value = rs.getString(index);
			a = (value == null) ? null : Short.valueOf(value);
		}else if(clz == short.class){
			a = rs.getShort(index);
		}else{
			a = rs.getObject(index);
		}
		
		return (a == null) ? null : (A)a;
	}
	
	/**
	 * 将时间戳格式化为String
	 * @param time
	 * @param pattern
	 * @return String
	 */
	protected String formatDate(long time, String pattern) {
		pattern = (pattern == null || pattern.length() == 0) ? BaseDAOConfig.formatTime : pattern;
		return new java.text.SimpleDateFormat(pattern).format(time);
	}

	/**
	 * 判断一个字符串是否为空 (true为空)
	 */
	protected boolean isBlank(String str) {
        if (str == null)
            return true;
        int l = str.length();
        if (l > 0) {
            for (int i = 0; i < l; i++) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }
	
	/**
	 * 判断一个Object是否为空, 且toString()可转为字符串类型 (true为空)
	 */
	protected boolean isBlankObj(Object obj) {
		return obj == null ? true : this.isBlank(obj.toString());
	}

}
