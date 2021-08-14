package com.boot.dao.api;

import java.util.ArrayList;
import java.util.List;

/**
 * 多条件动态查询父类(分页)<br>
 * 注意：若该Search类用于多个查询业务共用时，请设置业务类型<br>
 * 正常情况下无需指定，主要用于区分字段属于哪个业务<br>
 * @author 2020-12-01 create wang.jia.le
 * @version 1.1.0
 */
public abstract class PageSearch extends BaseSearch{

	/**
	 * 总记录SQL, 可以不用编写<br>
	 * 但考虑到复杂SQL的性能问题, 此处保留自定义功能, 但查询条件须与SQL相同
	 */
	public String countSQL;
	
	public int pageIndex; 	//分页索引
	public int pageSize; 	//分页大小
	
	public PageSearch() {
		this(1, 10);
	}

	public PageSearch(int pageIndex, int pageSize) {
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
	}
	
	@Override
	public void clear(){
		super.clear();
		this.countSQL = null;
	}

	@Override
	public final String appendWhere() {
		if(super.append)
			return super.SQL;
		List<Object> params = new ArrayList<>();
		List<String> qualifiers = super.findQualifier(SQL);
		for (int i = 0; i < qualifiers.size(); i++) {
			String qualifier = qualifiers.get(i);
			String where = super.appendWhere(i+1, params);
			SQL = SQL.replace(qualifier, where);
			if(countSQL != null) {
				countSQL = countSQL.replace(qualifier, where);
			}
		}
		super.params = params.toArray();
		super.append = true;
		return super.SQL;
	}

	/**
	 * @param sql
	 * @param countSQL 总记录SQL, 可以不用编写<br>
	 * 但考虑到复杂SQL的性能问题, 此处保留自定义功能, 但查询条件须与SQL相同
	 * @return String
	 */
	public final String appendWhere(String sql, String countSQL) {
		if(super.append)
			return super.SQL;
		super.SQL = sql;
		this.countSQL = countSQL;
		return this.appendWhere();
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
