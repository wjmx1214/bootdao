package com.boot.dao.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 多条件动态查询父类(分页)<br>
 * 注意：若该Search类用于多个查询业务共用时，请设置业务类型<br>
 * 正常情况下无需指定，主要用于区分字段属于哪个业务<br>
 * @author 2020-12-01 create wang.jia.le
 * @version 1.1.1
 */
public abstract class PageSearch extends BaseSearch{

	/**
	 * 总记录SQL, 可以不用编写<br>
	 * 但考虑到复杂SQL的性能问题，以及自动生成可能产生错误, 此处保留自定义功能, 但查询条件须与SQL相同
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
		this.countSQL = appendWhereAndParam(super.SQL, this.countSQL);
		return super.SQL;
	}

	/**
	 * @param sql
	 * @param countSQL 总记录SQL, 可以不用编写<br>
	 * 但考虑到复杂SQL的性能问题，以及自动生成可能产生错误, 此处保留自定义功能, 但查询条件须与SQL相同
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
	
	public static String getCountSQL(String sql) {
		String countSQL = getBaseCountSQL(sql);
		return getUnionCountSQL(countSQL);
	}
	
	private static String getUnionCountSQL(String countSQL) {
		Map<String, String> sqls = new LinkedHashMap<>();
		String unionAll = findKeyword(countSQL, " union all ");
		if(unionAll != null) {
			String[] unionallSqls = countSQL.split(unionAll);
			for (String unionallSql : unionallSqls) {
				String union = findUnionCountSQL(sqls, unionallSql, unionAll);
				if(union == null) {
					sqls.put(unionallSql, unionAll);
				}
			}
		}else {
			findUnionCountSQL(sqls, countSQL, "0");
		}

		if(sqls.size() > 0) {
			StringBuffer sbCountSQL = new StringBuffer();
			int i = 0;
			for (String key : sqls.keySet()) {
				if(findKeywordCount(key, "\\(") != findKeywordCount(key, "\\)")) {
					return countSQL; //当分割后的查询语句()数量不对等时，说明是包含关系，则不进行下一步处理了
				}
				sbCountSQL.append(getGroupCountSQL(key));
				if(i++ < sqls.size()-1) {
					sbCountSQL.append(sqls.get(key));
				}
			}
			return "select sum(count1) from (#{countSQL}) count_auto".replace("#{countSQL}", sbCountSQL.toString());
		}
		return getGroupCountSQL(countSQL);
	}
	
	private static String findUnionCountSQL(Map<String, String> sqls, String unionSql, String unionAll) {
		String union = findKeyword(unionSql, " union ");
		if(union != null) {
			String[] unionSqls = unionSql.split(union);
			int i = 0;
			for (String sql : unionSqls) {
				if(i++ < unionSqls.length-1) {
					sqls.put(sql, union);
				}else {
					sqls.put(sql, unionAll);
				}
			}
		}
		return union;
	}
	
	private static String getGroupCountSQL(String countSQL) {
		String groupBy = findKeyword(countSQL, " group by ");
		if(groupBy != null) {
			String[] sqls = countSQL.split(groupBy);
			if(findKeyword(sqls[sqls.length-1], " from") == null) {
				return "select count(1) count1 from (#{countSQL}) count_auto".replace("#{countSQL}", countSQL);
			}
		}
		return countSQL;
	}

	private static String getBaseCountSQL(String sql) {
		String countSQL = sql;
		String selectFromSQL = findSelectFromSQLGreedy(countSQL);
		String selectFrom = findKeyword(selectFromSQL, "select | from");
		if(selectFrom != null && selectFrom.length() == 5) {
			List<String> list = findSelectFromSQL(countSQL);
			for (String str : list) {
				countSQL = countSQL.replace(str, " count(1) count1 ");
			}
		}else {
			countSQL = countSQL.replace(selectFromSQL, " count(1) count1 ");
		}
		List<String> orderBySQLs = findOrderBySQL(countSQL);
		for (String orderBySQL : orderBySQLs) {
			countSQL = countSQL.replace(orderBySQL, "");
		}
		return countSQL;
	}
	
	private static String findKeyword(String sql, String keyword) {
		String m = "(?i)("+keyword+")";
		Matcher matcher = Pattern.compile(m).matcher(sql);
		while (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
	
	//关键字出现次数
	private static int findKeywordCount(String sql, String keyword) {
		String m = "(?i)("+keyword+")";
		Matcher matcher = Pattern.compile(m).matcher(sql);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;
	}
	
	//贪婪模式
	private static String findSelectFromSQLGreedy(String sql) {
		String m = "(?i)(select )+(.*)(?i)( from)";
		Matcher matcher = Pattern.compile(m).matcher(sql);
		while (matcher.find()) {
			return matcher.group().substring(6, matcher.group().length() - 4);
		}
		return null;
	}
	
	//最小模式
	private static List<String> findSelectFromSQL(String sql) {
		String m = "(?i)(select )+(.*?)(?i)( from)";
		Matcher matcher = Pattern.compile(m).matcher(sql);
		List<String> list = new ArrayList<>();
		while (matcher.find()) {
			list.add(matcher.group().substring(6, matcher.group().length() - 4));
		}
		return list;
	}
	
	private static List<String> findOrderBySQL(String sql) {
		List<String> list = new ArrayList<>();
		String m = "\\s?(?i)(order by)\\s+(.*?)(?i)( group by )";
		
		Matcher matcher = Pattern.compile(m).matcher(sql);
		while (matcher.find()) {
			list.add(matcher.group().substring(0, matcher.group().length() - 10));
		}
		m = "\\s?(?i)(order by )+(.*?)\\)";
		matcher = Pattern.compile(m).matcher(sql);
		while (matcher.find()) {
			list.add(matcher.group().substring(0, matcher.group().length() - 1));
		}
		m = "\\s?(?i)(order by )+(.*?)";
		matcher = Pattern.compile(m).matcher(sql);
		String temp = null;
		while (matcher.find()) {
			temp = matcher.group();
		}
		if(temp != null) {
			int index = sql.lastIndexOf(temp);
			temp = sql.substring(index, sql.length());
			for (String str : list) {
				if(temp.indexOf(str) == 0) {
					return list;
				}
			}
			list.add(temp);
		}
		return list;
	}

}
