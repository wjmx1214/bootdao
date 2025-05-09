package com.boot.dao.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.mapping.MappingException;

import com.boot.dao.api.BaseSearch;
import com.boot.dao.api.IBaseEntityDAO;
import com.boot.dao.api.Page;
import com.boot.dao.api.PageSearch;
import com.boot.dao.api.SearchType;
import com.boot.dao.config.BaseDAOConfig;
import com.boot.dao.mapping.BaseColumnMapping;
import com.boot.dao.mapping.BaseMappingCache;
import com.boot.dao.mapping.BaseTableMapping;
import com.boot.dao.util.BaseDAOLog;
import com.boot.dao.util.BaseDAOUtil;

/**
 * 实体封装类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.2.0
 */
public abstract class BaseEntityDAO extends BaseJDBC implements IBaseEntityDAO{

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
	 * 新增
	 * @param t
	 * @return <T>
	 * @throws Exception
	 */
	@Override
	public <T> T insert(T t) throws Exception{
		return save(t, 0);
	}
	
	/**
	 * 更新
	 * @param t
	 * @return <T>
	 * @throws Exception
	 */
	@Override
	public <T> T update(T t) throws Exception{
		return save(t, 1);
	}
	
	/**
	 * 新增或更新(组合主键暂不支持，请自行查询是否存在，然后选择新增或者更新)
	 * @param t
	 * @return <T>
	 * @throws Exception
	 */
	@Override
	public <T> T save(T t) throws Exception{
		return save(t, -1);
	}
	
	//新增或更新
	@SuppressWarnings("unchecked")
	public <T> T save(T t, int save) throws Exception{
		BaseTableMapping tm = this.getTableMapping(t);
		Serializable id = tm.idFieldGet(t);
		T old = null;
		if(save == 1 || save == -1) {
			old = (id == null) ? null : (T)this.findByUniqueColumn(tm.idColumnName, id, t.getClass(), tm.tableName);
		}
		List<Object> paramsList = new ArrayList<>();
		boolean isBlank = isBlankObj(id);
		if(old == null){ //新增
			if(super.sourceType == BaseSourceType.clickhouse) {
				if(tm.idAuto && isBlank){ //自动生成
					id = BaseDAOConfig.snowflakeIdWorker.nextId();
				}
				String sql = this.appendCreateSQLValues(t, tm, paramsList, id);
				super.updateSQL(sql, paramsList.toArray());
				tm.idFieldSet(t, id);
			}else {
				if(tm.idAuto && isBlank){ //自动生成
					if(tm.idField.getType() == String.class) {
						id = UUID.randomUUID().toString();//.replaceAll("-","");
					}
				}
				String sql = this.appendCreateSQLValues(t, tm, paramsList, id);
				if(sql != null) {
					if(tm.idAuto && isBlank){ //自增
						if(tm.idField.getType() != String.class) {
							Long newId = super.insertAndGetId(sql, paramsList.toArray());
							if(tm.idField.getType() == Integer.class || tm.idField.getType() == int.class) {
								id = newId.intValue();
							}else {
								id = newId;
							}
							tm.idFieldSet(t, id);
						}
					}else{
						super.updateSQL(sql, paramsList.toArray());
					}
				}
			}
		}else{ //更新
			String sql = this.appendUpdateSQL(t, tm, paramsList, id, old);
			if(sql != null) {
				super.updateSQL(sql, paramsList.toArray());
				t = BaseDAOUtil.copy(t, old);
			}
		}
		return t;
	}

	//拼接SQL(新增)(values语法)
	private <T> String appendCreateSQLValues(T t, BaseTableMapping tm, List<Object> paramsList, Serializable id) throws Exception{
		StringBuffer nameSQL = new StringBuffer();
		StringBuffer paramSQL = new StringBuffer();
		for (String columnName : tm.columnMappings.keySet()) {
			BaseColumnMapping cm = tm.columnMappings.get(columnName);
			if(cm.field == tm.idField)
				continue;
			if(cm.saveMapping && cm.createMapping) {
				Object value = cm.field.get(t);
				if(value != null && "null".equals(value.toString().toLowerCase()))
					value = null;
				if(value == null && BaseDAOConfig.autoCreateTime && tm.hasCreateTime){
					if("createTime".equals(cm.field.getName()) || "createDate".equals(cm.field.getName())) {
						value = super.formatDate(System.currentTimeMillis(), cm.datePattern); //当创建时间为空时，根据配置决定是否自动生成
					}
				}
				if(!isBlankObj(value) || cm.saveNull || cm.saveEmpty && value != null) {
					nameSQL.append(columnName).append(",");
					paramSQL.append("?,");
					paramsList.add(value);
				}
			}
		}
		if(BaseDAOConfig.autoCreateTime && !tm.hasCreateTime && tm.createTime != null) {
			if(tm.createTime.saveMapping && tm.createTime.createMapping) {
				nameSQL.append(tm.createTime.columnName).append(","); //配置为自动生成，且实体类有创建时间字段，而当前类没有该字段
				paramSQL.append("?,");
				paramsList.add(super.formatDate(System.currentTimeMillis(), tm.createTime.datePattern));
			}
		}
		int length = nameSQL.length();
		if(length == 0){
			return null;
		}
		nameSQL.deleteCharAt(length-1);
		paramSQL.deleteCharAt(paramSQL.length()-1);
		if(!tm.idAuto || id != null){ //非自增
			nameSQL.append(',').append(tm.idColumnName);
			paramSQL.append(",?");
			paramsList.add(id);
		}
		StringBuffer sql = new StringBuffer("INSERT INTO ").append(tm.tableName).append("(").append(nameSQL).append(") VALUES(").append(paramSQL).append(")");
		return sql.toString();
	}

	//拼接SQL(新增)
	@SuppressWarnings("unused")
	@Deprecated
	private <T> String appendCreateSQL(T t, BaseTableMapping tm, List<Object> paramsList, Serializable id) throws Exception{
		StringBuffer setSQL = new StringBuffer();
		for (String columnName : tm.columnMappings.keySet()) {
			BaseColumnMapping cm = tm.columnMappings.get(columnName);
			if(cm.field == tm.idField)
				continue;
			if(cm.saveMapping && cm.createMapping) {
				Object value = cm.field.get(t);
				if(value != null && "null".equals(value.toString().toLowerCase()))
					value = null;
				if(value == null && BaseDAOConfig.autoCreateTime && tm.hasCreateTime){
					if("createTime".equals(cm.field.getName()) || "createDate".equals(cm.field.getName())) {
						value = super.formatDate(System.currentTimeMillis(), cm.datePattern); //当创建时间为空时，根据配置决定是否自动生成
					}
				}
				if(!isBlankObj(value) || cm.saveNull || cm.saveEmpty && value != null) {
					setSQL.append(columnName).append("=?,");
					paramsList.add(value);
				}
			}
		}
		if(BaseDAOConfig.autoCreateTime && !tm.hasCreateTime && tm.createTime != null) {
			if(tm.createTime.saveMapping && tm.createTime.createMapping) {
				setSQL.append(tm.createTime.columnName).append("=?,"); //配置为自动生成，且实体类有创建时间字段，而当前类没有该字段
				paramsList.add(super.formatDate(System.currentTimeMillis(), tm.createTime.datePattern));
			}
		}
		int length = setSQL.length();
		if(length == 0){
			return null;
		}
		setSQL.deleteCharAt(length-1);
		StringBuffer sql = new StringBuffer("INSERT INTO ").append(tm.tableName).append(" SET ").append(setSQL);
		if(!tm.idAuto || id != null){ //非自增
			sql.append(',').append(tm.idColumnName).append("=?");
			paramsList.add(id);
		}
		return sql.toString();
	}

	//拼接SQL(更新)
	private <T> String appendUpdateSQL(T t, BaseTableMapping tm, List<Object> paramsList, Serializable id, T old) throws Exception{
		StringBuffer setSQL = new StringBuffer();
		for (String columnName : tm.columnMappings.keySet()) {
			BaseColumnMapping cm = tm.columnMappings.get(columnName);
			if(cm.field == tm.idField)
				continue;
			if(cm.saveMapping && cm.updateMapping) {
				Object value = cm.field.get(t);
				if(value != null && "null".equals(value.toString().toLowerCase()))
					value = null;
				if(value != null && value.equals(cm.field.get(old)))
					continue;
				if("createTime".equals(cm.field.getName()) || "createDate".equals(cm.field.getName()))
					continue; //更新时发现有创建时间字段则跳过
				if(!isBlankObj(value) || cm.saveNull || cm.saveEmpty && value != null) {
					setSQL.append(columnName).append("=?,");
					paramsList.add(value);
				}
			}
		}
		int length = setSQL.length();
		if(length == 0){
			return null;
		}
		setSQL.deleteCharAt(length-1);
		paramsList.add(id);
		StringBuffer sql = null;
		if(super.sourceType == BaseSourceType.clickhouse) {
			sql = new StringBuffer("ALTER TABLE ").append(tm.tableName).append(" UPDATE ").append(setSQL).append(" WHERE ").append(tm.idColumnName).append("=?");
		}else {
			sql = new StringBuffer("UPDATE ").append(tm.tableName).append(" SET ").append(setSQL).append(" WHERE ").append(tm.idColumnName).append("=?");
		}
		return sql.toString();
	}

	/**
	 * 删除
	 * @param t
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public <T> boolean delete(T t) throws Exception{
		BaseTableMapping tm = this.getTableMapping(t);
		return this.delete(tm.idFieldGet(t), t.getClass(), tm);
	}
	
	/**
	 * 根据主键删除
	 * @param pk
	 * @param clz
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public <T> boolean delete(Serializable pk, Class<T> clz) throws Exception{
		return this.delete(pk, clz, this.getTableMapping(clz));
	}
	
	//根据主键删除
	private <T> boolean delete(Serializable pk, Class<T> clz, BaseTableMapping tm) throws Exception{
		checkPK(pk, clz);
		String sql = null;
		if(super.sourceType == BaseSourceType.clickhouse) {
			sql = new StringBuffer("ALTER TABLE ").append(tm.tableName).append(" DELETE ").append(" WHERE ").append(tm.idColumnName).append("=?").toString();
		}else {
			sql = new StringBuffer("DELETE FROM ").append(tm.tableName).append(" WHERE ").append(tm.idColumnName).append("=?").toString();
		}
		return super.updateSQL(sql, pk) > 0;
	}
	
	/**
	 * 根据主键查找对象
	 * @param pk
	 * @param clz
	 * @return <T>
	 */
	@Override
	public <T> T findByPK(Serializable pk, Class<T> clz){
		checkPK(pk, clz);
		try {
			BaseTableMapping tm = this.getTableMapping(clz);
			return this.findByUniqueColumn(tm.idColumnName, pk, clz, tm.tableName);
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		}
		return null;
	}
	
	/**
	 * 根据唯一属性查找对象(需要有对应的列名映射)
	 * @param fieldName
	 * @param value
	 * @param clz
	 * @return <T>
	 */
	@Override
	public <T> T findByUniqueField(String fieldName, Object value, Class<T> clz){
		BaseTableMapping tm = BaseMappingCache.getTableMapping(clz);
		return this.findByUniqueColumn(tm.fieldMappings.get(fieldName).columnName, value, clz, tm.tableName);
	}
	
	/**
	 * 根据唯一列查找对象
	 * @param columnName
	 * @param value
	 * @param clz
	 * @return <T>
	 */
	@Override
	public <T> T findByUniqueColumn(String columnName, Object value, Class<T> clz){
		return this.findByUniqueColumn(columnName, value, clz, BaseMappingCache.getTableMapping(clz).tableName);
	}
	
	//根据唯一列查找对象
	private <T> T findByUniqueColumn(String columnName, Object value, Class<T> clz, String tableName){
		String sql = new StringBuffer("SELECT * FROM ").append(tableName).append(" WHERE ").append(columnName).append("=?").toString();
		return super.getEntity(sql, clz, value);
	}
	
	//检查主键是否有效
	private <T> void checkPK(Serializable pk, Class<T> clz) {
		if(pk == null || isBlank(pk.toString()) || "0".equals(pk.toString())) {
			throw new MappingException("id is null or empty or equals 0, please check! className: " + clz.getName());
		}
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
		if(tm.mappingType == 0 || tm.idField == null)
			throw new MappingException("not found id mapping, please check! className: " + clz.getName());
		return tm;
	}
	
	/**
	 * 根据单个条件查找对象集合(属性名, 需要有对应的列名映射)<br>
	 * 多个参数需要用String类型并以逗号进行分隔
	 * @param fieldName
	 * @param value
	 * @param searchType
	 * @param clz
	 * @return List<T>
	 */
	@Override
	public <T> List<T> findByWhereField(String fieldName, Object value, SearchType searchType, Class<T> clz){
		BaseTableMapping tm = BaseMappingCache.getTableMapping(clz);
		return this.findByWhereColumn(tm.fieldMappings.get(fieldName).columnName, value, searchType, clz);
	}
	
	/**
	 * 根据单个条件查找对象集合(列名)<br>
	 * 多个参数需要用String类型并以逗号进行分隔
	 * @param columnName
	 * @param value
	 * @param searchType
	 * @param clz
	 * @return List<T>
	 */
	@Override
	public <T> List<T> findByWhereColumn(String columnName, Object value, SearchType searchType, Class<T> clz){
		BaseTableMapping tm = BaseMappingCache.getTableMapping(clz);
		StringBuffer sql = new StringBuffer("SELECT * FROM ").append(tm.tableName).append(" WHERE ").append(columnName).append(searchType.code);
		List<Object> params = this.convertParam(value, searchType, sql);
		if(params == null) {
			return null;
		}
		return super.getEntitys(sql.toString(), clz, params.toArray());
	}
	
	//转换参数
	private List<Object> convertParam(Object value, SearchType type, StringBuffer sql) {
		List<Object> params = new ArrayList<>();
		if(type == SearchType.null_is || type == SearchType.null_not || type == SearchType.empty_is || type == SearchType.empty_not) {
			return params;
		}
		if(isBlankObj(value)) {
			return null;
		}
		if(type == SearchType.in || type == SearchType.in_not) {
			StringBuffer question = new StringBuffer("(");
			String[] array = value.toString().split(",");
			for (Object o : array) {
				question.append("?,");
				params.add(o);
			}
			question.deleteCharAt(question.length()-1).append(')');
			sql.append(question);
		}else {
			if(type == SearchType.like_left) {
				params.add("%" + value.toString());
			}else if(type == SearchType.like_right) {
				params.add(value.toString() + "%");
			}else if(type == SearchType.like_all) {
				params.add("%" + value.toString() + "%");
			}else if(type == SearchType.between) {
				String[] array = value.toString().split(",");
				params.add(array[0]);
				params.add(array[1]);
			}else {
				params.add(value);
			}
		}
		return params;
	}
	
	/**
	 * 根据主键更新单值(属性名)
	 * @param pk
	 * @param clz
	 * @param fieldName
	 * @param value
	 * @return int
	 * @throws Exception
	 */
	public <T> int updateFieldByPK(Serializable pk, Class<T> clz, String fieldName, Object value) throws Exception{
		BaseTableMapping tm = this.getTableMapping(clz);
		return this.updateColumnByPK(pk, clz, tm.fieldMappings.get(fieldName).columnName, value);
	}
	
	/**
	 * 根据主键更新单值(列名)
	 * @param pk
	 * @param clz
	 * @param columnName
	 * @param value
	 * @return int
	 * @throws Exception
	 */
	public <T> int updateColumnByPK(Serializable pk, Class<T> clz, String columnName, Object value) throws Exception{
		checkPK(pk, clz);
		BaseTableMapping tm = this.getTableMapping(clz);
		String sql = null;
		if(super.sourceType == BaseSourceType.clickhouse) {
			sql = new StringBuffer("ALTER TABLE ").append(tm.tableName).append(" UPDATE ").append(columnName).append("=? WHERE ").append(tm.idColumnName).append("=?").toString();
		}else {
			sql = new StringBuffer("UPDATE ").append(tm.tableName).append(" SET ").append(columnName).append("=? WHERE ").append(tm.idColumnName).append("=?").toString();
		}
		return super.updateSQL(sql, value, pk);
	}
	
	//-------------------------------------------------------- 分页查询 -------------------------------------------------------
	
	/**
	 * 分页包装, 单表且无子查询可省略SQL<br>
	 * 目前仅支持LIMIT, 若pageSize为0, 则代表不做分页, 且pageSize默认=总记录数count
	 * @param search
	 * @param clz
	 * @return Page<T>
	 */
	public <T> Page<T> page(PageSearch search, Class<T> clz){
		return page(Map.class.isAssignableFrom(clz), search, clz);
	}
	
	/**
	 * 分页包装<br>
	 * 目前仅支持LIMIT, 若pageSize为0, 则代表不做分页, 且pageSize默认=总记录数count
	 * @param search
	 * @return Page<Map<String, Object>>
	 */
	@SuppressWarnings("rawtypes")
	public Page<Map> pageMap(PageSearch search){
		return page(true, search, Map.class);
	}
	
	//分页包装(目前仅支持LIMIT, 若pageSize为0, 则代表不做分页, 且pageSize默认=总记录数count)
	@SuppressWarnings("unchecked")
	private <T> Page<T> page(boolean isMap, PageSearch search, Class<T> clz){
		if(!isMap && search.SQL == null) { //单表省略了SQL时
			BaseTableMapping tm = BaseMappingCache.getTableMapping(clz);
			search.SQL = "select * from " + tm.tableName + " where 1=1#{search}";
		}
		search.appendWhere();
		if(search.countSQL == null) {
			search.countSQL = PageSearch.getCountSQL(search.SQL);
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
			dataSql = search.SQL + BaseDAOUtil.appendPage(search.pageIndex, search.pageSize);
			list = isMap ? (List<T>) super.getMaps(dataSql, search.params) : super.getEntitys(dataSql, clz, search.params);
		}
		if(search.pageSize < 1) {
			search.pageSize = list.size();
		}
		Page<T> page = new Page<>(search.pageIndex, search.pageSize, count, list);
		page.setPageIndexChange(pageIndexChange);
		search.clear();
		page.setSearch(search);
		return page;
	}
	
	/**
	 * 分页包装<br>
	 * 目前仅支持LIMIT, 若pageSize为0, 则代表不做分页, 且pageSize默认=总记录数count
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
	 * 分页包装<br>
	 * 目前仅支持LIMIT, 若pageSize为0, 则代表不做分页, 且pageSize默认=总记录数count
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
	
	//分页包装(目前仅支持LIMIT, 若pageSize为0, 则代表不做分页, 且pageSize默认=总记录数count)
	@SuppressWarnings("unchecked")
	private <T> Page<T> page(boolean isMap, int pageIndex, int pageSize, String sql, Class<T> clz, Object... params){
		int count = this.getint(PageSearch.getCountSQL(sql), params);
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
			dataSql = sql + BaseDAOUtil.appendPage(pageIndex, pageSize);
			list = isMap ? (List<T>) super.getMaps(dataSql, params) : super.getEntitys(dataSql, clz, params);
		}
		if(pageSize < 1) {
			pageSize = list.size();
		}
		Page<T> page = new Page<>(pageIndex, pageSize, count, list);
		page.setPageIndexChange(pageIndexChange);
		return page;
	}
	
	//-------------------------------------------------------- 动态查询 -------------------------------------------------------
	
	/**
	 * 动态条件查询, 单表且无子查询可省略SQL<br>
	 * @param search
	 * @param clz
	 * @return T<T>
	 */
	public <T> T searchOne(BaseSearch search, Class<T> clz){
		List<T> list = search(Map.class.isAssignableFrom(clz), search, clz);
		if(list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 动态条件查询, 单表且无子查询可省略SQL<br>
	 * @param search
	 * @param clz
	 * @return List<T>
	 */
	public <T> List<T> search(BaseSearch search, Class<T> clz){
		return search(Map.class.isAssignableFrom(clz), search, clz);
	}
	
	/**
	 * 动态条件查询<br>
	 * @param search
	 * @return List<Map<String, Object>>
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> searchMap(BaseSearch search){
		return search(true, search, Map.class);
	}
	
	//动态条件查询
	@SuppressWarnings("unchecked")
	private <T> List<T> search(boolean isMap, BaseSearch search, Class<T> clz){
		if(!isMap && search.SQL == null) { //单表省略了SQL时
			BaseTableMapping tm = BaseMappingCache.getTableMapping(clz);
			search.SQL = "select * from " + tm.tableName + " where 1=1#{search}";
		}
		search.appendWhere();
		List<T> list = isMap ? (List<T>) super.getMapsString(search.SQL, search.params) : super.getEntitys(search.SQL, clz, search.params);
		search.clear();
		return list;
	}

}
