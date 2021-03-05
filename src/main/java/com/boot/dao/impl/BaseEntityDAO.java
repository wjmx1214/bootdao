package com.boot.dao.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.mapping.MappingException;

import com.boot.dao.api.IBaseEntityDAO;
import com.boot.dao.api.Page;
import com.boot.dao.api.PageSearch;
import com.boot.dao.config.BaseDAOConfig;
import com.boot.dao.mapping.BaseColumnMapping;
import com.boot.dao.mapping.BaseMappingCache;
import com.boot.dao.mapping.BaseTableMapping;
import com.boot.dao.util.BaseDAOLog;
import com.boot.dao.util.BaseDAOUtil;

/**
 * 实体封装类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.1
 */
public abstract class BaseEntityDAO extends BaseJDBC implements IBaseEntityDAO{
	
	public BaseEntityDAO(String dataSourceName) {
		super(dataSourceName);
	}

	//----------------------------------------------------------- 基础API -------------------------------------------------------

	/**
	 * 获取boolean型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return boolean
	 */
	@Override
	public boolean getboolean(String sql, Object... params){
		return super.getObject(sql, boolean.class, params);
	}
	
	/**
	 * 获取int型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return int
	 */
	@Override
	public int getint(String sql, Object... params){
		return super.getObject(sql, int.class, params);
	}
	
	/**
	 * 获取Integer型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Integer
	 */
	@Override
	public Integer getInteger(String sql, Object... params){
		return super.getObject(sql, Integer.class, params);
	}
	
	/**
	 * 获取long型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return long
	 */
	@Override
	public long getlong(String sql, Object... params){
		return super.getObject(sql, long.class, params);
	}
	
	/**
	 * 获取Long型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Long
	 */
	@Override
	public Long getLong(String sql, Object... params){
		return super.getObject(sql, Long.class, params);
	}
	
	/**
	 * 获取float型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return float
	 */
	@Override
	public float getfloat(String sql, Object... params) {
		return super.getObject(sql, float.class, params);
	}
	
	/**
	 * 获取Float型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Float
	 */
	@Override
	public Float getFloat(String sql, Object... params) {
		return super.getObject(sql, Float.class, params);
	}
	
	/**
	 * 获取double型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return double
	 */
	@Override
	public double getdouble(String sql, Object... params){
		return super.getObject(sql, double.class, params);
	}
	
	/**
	 * 获取Double型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Double
	 */
	@Override
	public Double getDouble(String sql, Object... params){
		return super.getObject(sql, Double.class, params);
	}
	
	/**
	 * 获取String型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return String
	 */
	@Override
	public String getString(String sql, Object... params){
		return super.getObject(sql, String.class, params);
	}
	
	/**
	 * 获取java.util.Date型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Date
	 */
	@Override
	public Date getDate(String sql, Object... params){
		return super.getObject(sql, Date.class, params);
	}
	
	/**
	 * 获取byte[]型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return byte[]
	 */
	@Override
	public byte[] getbytes(String sql, Object... params){
		return super.getObject(sql, byte[].class, params);
	}
	
	/**
	 * 获取BigDecimal型单值
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return BigDecimal
	 */
	@Override
	public BigDecimal getBigDecimal(String sql, Object... params){
		return super.getObject(sql, BigDecimal.class, params);
	}
	
	/**
	 * 获取Object型单值(java基础类型)
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return <A>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <A> A getObject(String sql, Object... params){
		return (A)super.getObject(sql, Object.class, params);
	}
	
	/**
	 * 获取一行数据
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getRowOne(String sql, Object... params){
		List<Map<String, Object>> list = super.getMaps(sql, params);
		return (list.size() > 0) ? list.get(0) : null;
	}
	
	/**
	 * 获取一行数据
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Map<String, String>
	 */
	@Override
	public Map<String, String> getRowOneString(String sql, Object... params){
		List<Map<String, String>> list = super.getMapsString(sql, params);
		return (list.size() > 0) ? list.get(0) : null;
	}
	
	//---------------------------------------------------------- 对象增删改查 ------------------------------------------------------

	/**
	 * 新增或更新(空字符更新)
	 * @param t
	 * @return <T>
	 * @throws Exception
	 */
	@Override
	public <T> T save_empty(T t) throws Exception{
		return this.save(t, true);
	}
	
	/**
	 * 新增或更新
	 * @param t
	 * @return <T>
	 * @throws Exception
	 */
	@Override
	public <T> T save(T t) throws Exception{
		return this.save(t, false);
	}

	//新增或更新
	@SuppressWarnings("unchecked")
	private <T> T save(T t, boolean empty) throws Exception{
		BaseTableMapping tm = this.getTableMapping(t);
		Serializable id = tm.idFieldGet(t);
		T old = (id == null) ? null : (T)this.getByColumn(tm.idColumnName, id, t.getClass(), tm);
		List<Object> paramsList = new ArrayList<>();
		if(old == null){ //新增
			String sql = this.appendCreateSQL(t, tm, paramsList, empty, id);
			if(sql != null) {
				if(tm.idAuto){ //自增
					Long newId = super.insertAndGetId(sql, paramsList.toArray());
					id = (tm.idField.getType() == Integer.class || tm.idField.getType() == int.class) ? newId.intValue() : newId;
					tm.idFieldSet(t, id);
				}else{
					super.updateSQL(sql, paramsList.toArray());
				}
			}
		}else{ //更新
			String sql = this.appendUpdateSQL(t, tm, paramsList, empty, id, old);
			if(sql != null) {
				super.updateSQL(sql, paramsList.toArray());
				t = BaseDAOUtil.copy(t, old);
			}
		}
		return t;
	}

	//拼接SQL(新增)
	private <T> String appendCreateSQL(T t, BaseTableMapping tm, List<Object> paramsList, boolean empty, Serializable id) throws Exception{
		StringBuffer setSQL = new StringBuffer();
		for (String columnName : tm.columnMappings.keySet()) {
			BaseColumnMapping cm = tm.columnMappings.get(columnName);
			Object value = cm.field.get(t);
			if(value == null && BaseDAOConfig.autoCreateTime && tm.hasCreateTime){
				if("createTime".equals(cm.field.getName()) || "createDate".equals(cm.field.getName())) {
					value = super.formatDate(System.currentTimeMillis(), cm.formatDate); //当创建时间为空时，根据配置决定是否自动生成
				}
			}
			if(value == null || cm.field == tm.idField || "null".equals(value.toString().toLowerCase()))
				continue;
			if( empty || !empty && !isBlankObj(value) ){
				setSQL.append(columnName).append("=?,");
				paramsList.add(value);
			}
		}
		if(BaseDAOConfig.autoCreateTime && !tm.hasCreateTime && tm.createTime != null) {
			setSQL.append(tm.createTime.columnName).append("=?,"); //配置为自动生成，且实体类有创建时间字段，而当前类没有该字段
			paramsList.add(super.formatDate(System.currentTimeMillis(), tm.createTime.formatDate));
		}
		int length = setSQL.length();
		if(length == 0){
			return null;
		}
		setSQL.deleteCharAt(length-1);
		StringBuffer sql = new StringBuffer("INSERT INTO ").append(tm.tableName).append(" SET ").append(setSQL);
		if(!tm.idAuto){ //非自增
			sql.append(',').append(tm.idColumnName).append("=?");
			paramsList.add(id);
		}
		return sql.toString();
	}

	//拼接SQL(更新)
	private <T> String appendUpdateSQL(T t, BaseTableMapping tm, List<Object> paramsList, boolean empty, Serializable id, T old) throws Exception{
		StringBuffer setSQL = new StringBuffer();
		for (String columnName : tm.columnMappings.keySet()) {
			BaseColumnMapping cm = tm.columnMappings.get(columnName);
			Object value = cm.field.get(t);
			if(value == null || value.equals(cm.field.get(old)) || cm.field == tm.idField || "null".equals(value.toString().toLowerCase()))
				continue;
			if("createTime".equals(cm.field.getName()) || "createDate".equals(cm.field.getName()))
				continue; //更新时发现有创建时间字段则跳过
			if( empty || !empty && !isBlankObj(value) ){
				setSQL.append(columnName).append("=?,");
				paramsList.add(value);
			}
		}
		int length = setSQL.length();
		if(length == 0){
			return null;
		}
		setSQL.deleteCharAt(length-1);
		paramsList.add(id);
		return new StringBuffer("UPDATE ").append(tm.tableName).append(" SET ").append(setSQL).append(" WHERE ").append(tm.idColumnName).append("=?").toString();
	}

	/**
	 * 删除
	 * @param t
	 * @throws Exception
	 */
	@Override
	public <T> void delete(T t) throws Exception{
		BaseTableMapping tm = this.getTableMapping(t);
		this.delete(tm.idFieldGet(t), t.getClass(), tm);
	}
	
	/**
	 * 根据主键删除
	 * @param pk
	 * @param clz
	 * @throws Exception
	 */
	@Override
	public <T> void delete(Serializable pk, Class<T> clz) throws Exception{
		this.delete(pk, clz, this.getTableMapping(clz));
	}
	
	//根据主键删除
	private <T> void delete(Serializable pk, Class<T> clz, BaseTableMapping tm) throws Exception{
		if(pk == null)
			throw new MappingException("id is null, please check! className: " + clz.getName());
		String sql = new StringBuffer("DELETE FROM ").append(tm.tableName).append(" WHERE ").append(tm.idColumnName).append("=?").toString();
		super.updateSQL(sql, pk);
	}
	
	/**
	 * 根据主键查找对象
	 * @param pk
	 * @param clz
	 * @return <T>
	 */
	@Override
	public <T> T getByPK(Serializable pk, Class<T> clz){
		if(pk == null || isBlank(pk.toString()) || "0".equals(pk.toString()))
			return null;
		try {
			BaseTableMapping tm = this.getTableMapping(clz);
			return this.getByColumn(tm.idColumnName, pk, clz, tm);
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		}
		return null;
	}
	
	/**
	 * 根据唯一列查找对象
	 * @param columnName
	 * @param value
	 * @param clz
	 * @return <T>
	 */
	@Override
	public <T> T getByColumn(String columnName, Object value, Class<T> clz){
		if(isBlank(columnName) || isBlankObj(value))
			return null;
		return this.getByColumn(columnName, value, clz, BaseMappingCache.getTableMapping(clz));
	}
	
	//根据唯一列查找对象
	private <T> T getByColumn(String columnName, Object value, Class<T> clz, BaseTableMapping tm){
		String sql = new StringBuffer("SELECT * FROM ").append(tm.tableName).append(" WHERE ").append(columnName).append("=?").toString();
		return super.getEntity(sql, clz, value);
	}
	
	//根据对象查询映射(未找到主键映射时抛出未映射异常)
	private <T> BaseTableMapping getTableMapping(T t) throws Exception{
		if(t == null)
			throw new MappingException("t is null, please check!");
		return this.getTableMapping(t.getClass());
	}
	
	//根据模板查询映射(未找到主键映射时抛出未映射异常)
	private BaseTableMapping getTableMapping(Class<?> clz) throws Exception{
		BaseTableMapping tm = BaseMappingCache.getTableMapping(clz);
		if(tm.metaType == 0 || tm.idField == null)
			throw new MappingException("not found id mapping, please check! className: " + clz.getName());
		return tm;
	}
	
	//-------------------------------------------------------- 分页查询 -------------------------------------------------------
	
	/**
	 * 分页包装(目前仅支持MYSQL)
	 * @param search
	 * @param clz
	 * @return Page<T>
	 */
	public <T> Page<T> page(PageSearch search, Class<T> clz){
		return page(Map.class.isAssignableFrom(clz), search, clz);
	}
	
	/**
	 * 分页包装(目前仅支持MYSQL)
	 * @param search
	 * @return Page<Map<String, Object>>
	 */
	@SuppressWarnings("rawtypes")
	public Page<Map> pageMap(PageSearch search){
		return page(true, search, Map.class);
	}
	
	//分页包装(目前仅支持MYSQL)
	@SuppressWarnings("unchecked")
	private <T> Page<T> page(boolean isMap, PageSearch search, Class<T> clz){
		search.appendWhere();
		if(search.countSQL == null) {
			search.countSQL = "SELECT count(1) AS count FROM (#{SQL}) AS count_auto".replace("#{SQL}", search.SQL);
		}
		int count = this.getint(search.countSQL, search.params);
		String dataSql = search.SQL + BaseDAOUtil.appendPage(search.pageIndex, search.pageSize);
		List<T> list = isMap ? (List<T>) super.getMaps(dataSql, search.params) : super.getEntitys(dataSql, clz, search.params);
		boolean pageIndexChange = false;
		if(count > 0 && list.size() == 0 && search.pageSize > 0){
			//若指定页无数据，则改为获取最后一页数据
			int allPage = count / search.pageSize;
			if(count % search.pageSize != 0){
				allPage++;
			}
			search.pageIndex = allPage;
			pageIndexChange = true; //分页索引已被重置
			list = isMap ? (List<T>) super.getMaps(dataSql, search.params) : super.getEntitys(dataSql, clz, search.params);
		}
		Page<T> page = new Page<>(search.pageIndex, search.pageSize, count, list);
		page.setPageIndexChange(pageIndexChange);
		search.clear();
		page.setSearch(search);
		return page;
	}
	
	/**
	 * 分页包装(目前仅支持MYSQL)
	 * @param pageIndex
	 * @param pageSize
	 * @param sql
	 * @param clz
	 * @param params SQL语句中对应的?号参数
	 * @return Page<T>
	 */
	public <T> Page<T> page(int pageIndex, int pageSize, String sql, Class<T> clz, Object... params){
		return page(Map.class.isAssignableFrom(clz), pageIndex, pageSize, sql, clz, params);
	}
	
	/**
	 * 分页包装(目前仅支持MYSQL)
	 * @param pageIndex
	 * @param pageSize
	 * @param sql
	 * @param params SQL语句中对应的?号参数
	 * @return Page<Map<String, Object>>
	 */
	@SuppressWarnings("rawtypes")
	public Page<Map> pageMap(int pageIndex, int pageSize, String sql, Object... params){
		return page(true, pageIndex, pageSize, sql, Map.class, params);
	}
	
	//分页包装(目前仅支持MYSQL)
	@SuppressWarnings("unchecked")
	private <T> Page<T> page(boolean isMap, int pageIndex, int pageSize, String sql, Class<T> clz, Object... params){
		String countSql = "SELECT count(1) AS count FROM (#{SQL}) AS count_auto".replace("#{SQL}", sql);
		int count = this.getint(countSql, params);
		String dataSql = sql + BaseDAOUtil.appendPage(pageIndex, pageSize);
		List<T> list = isMap ? (List<T>) super.getMaps(dataSql, params) : super.getEntitys(dataSql, clz, params);
		boolean pageIndexChange = false;
		if(count > 0 && list.size() == 0 && pageSize > 0){
			//若指定页无数据，则改为获取最后一页数据
			int allPage = count / pageSize;
			if(count % pageSize != 0){
				allPage++;
			}
			pageIndex = allPage;
			pageIndexChange = true; //分页索引已被重置
			list = isMap ? (List<T>) super.getMaps(dataSql, params) : super.getEntitys(dataSql, clz, params);
		}
		Page<T> page = new Page<>(pageIndex, pageSize, count, list);
		page.setPageIndexChange(pageIndexChange);
		return page;
	}
	
}
