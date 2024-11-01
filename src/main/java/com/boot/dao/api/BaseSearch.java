package com.boot.dao.api;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.boot.dao.mapping.BaseMappingCache;
import com.boot.dao.mapping.BaseSearchMapping;

/**
 * 多条件动态查询父类<br>
 * @author 2020-12-01 create wang.jia.le
 * @version 1.1.7
 */
public abstract class BaseSearch{

	@Search(isMapping = false)
	public String SQL;
	@Search(isMapping = false)
	public Object[] params;	//参数数组
	@Search(isMapping = false)
	boolean append = false; //是否已拼接
	
	public void clear(){
		this.SQL = null;
		this.params = null;
		this.append = false;
	}

	public String appendWhere() {
		appendWhereAndParam(this.SQL, null);
		return this.SQL;
	}

	public final String appendWhere(String sql) {
		if(this.append)
			return this.SQL;
		this.SQL = sql;
		return this.appendWhere();
	}
	
	String appendWhereAndParam(String sql, String countSQL) {
		if(this.append)
			return countSQL;
		List<Object> paramsList = new ArrayList<>();
		Map<String, List<Object>> paramsMap = new LinkedHashMap<>();
		Map<String, String> whereKeys = findWhereKey();
		List<String> qualifiers = findQualifier(SQL);
		for (String qualifier : qualifiers) {
			String whereKey = qualifier.trim();
			whereKey = whereKeys.get(whereKey.substring(2, whereKey.length() - 1));
			whereKey = whereKey == null ? "default_search" : whereKey;
			String where = this.appendWhereAndParam(whereKey, paramsList, paramsMap);
			if (!isAppendAnd(SQL, qualifier)){
				if(where.length() == 0 || where.startsWith(" order")) {
					where = " 1=1 " + where;
				} else {
					where = where.substring(4, where.length());
				}
			}
			SQL = SQL.replace(qualifier, where);
			if(countSQL != null) {
				countSQL = countSQL.replace(qualifier, where);
			}
		}
		this.params = paramsList.toArray();
		this.append = true;
		return countSQL;
	}
	
	private Map<String, String> findWhereKey() {
		Map<String, String> map = new HashMap<>();
		List<BaseSearchMapping> sms = BaseMappingCache.getSearchMapping(this.getClass());
		for (BaseSearchMapping sm : sms) {
			if(sm.whereKey.length() > 0) {
				map.put(sm.whereKey, sm.whereKey);
			}
		}
		return map;
	}
	
	//第一个条件是否拼接 and 关键字
	private static boolean isAppendAnd(String sql, String qualifier) {
		boolean appendAnd = true;
		String[] strs = sql.substring(0, sql.indexOf(qualifier)).replace("\r\n", "").replace("\n", "").replace("\t", "").toLowerCase().split(" ");
		for (int i = strs.length-1; i >= 0; i--) {
			if(strs[i].length() > 0) {
				if("where".equals(strs[i]) || "having".equals(strs[i]) || "and".equals(strs[i]) || "or".equals(strs[i])) {
					appendAnd = false;
				}
				break;
			}
		}
		return appendAnd;
	}
	
	private static List<String> findQualifier(String str) {
		List<String> list = new ArrayList<>();
		Matcher matcher = Pattern.compile("(\\s*#\\{.*?\\})").matcher(str); //prefix = "#"
		while (matcher.find()) {
			list.add(matcher.group());
		}
		return list;
	}
	
	private String appendWhereAndParam(String whereKey, List<Object> paramsList, Map<String, List<Object>> paramsMap) {
		List<Object> params = paramsMap.get(whereKey);
		if(params == null) {
			params = new ArrayList<>();
		}else {
			for (Object param : params) {
				paramsList.add(param);
			}
			return "";
		}
		
		StringBuffer sort = new StringBuffer();
		StringBuffer where = new StringBuffer();
		List<BaseSearchMapping> sms = BaseMappingCache.getSearchMapping(this.getClass());
		for (BaseSearchMapping sm : sms) {
			if(sm.whereKey.length() == 0 || sm.whereKey.equals(whereKey)) {
				if(sm.sort != Sort.NOT) {
					sort.append((sort.length() == 0) ? " order by " : ", ");
					sort.append(sm.tableAs).append(sm.column).append(" ").append(sm.sort.sort);
				}
				
				if(sm.searchType == SearchType.null_is || sm.searchType == SearchType.null_not || sm.searchType == SearchType.empty_is || sm.searchType == SearchType.empty_not) {
					where.append(" and ").append(sm.tableAs).append(sm.column).append(sm.searchType.code);
					continue;
				}
				
				Object value = sm.searchFieldGet(this);
				if(isBlankObj(value)) {
					continue;
				}
				
				if(sm.whereSQL.length() > 0) {
					if(sm.whereSQL.charAt(0) != ' ') {
						where.append(' ');
					}
					appendWhereSQLParam(value, params, sm.whereSQL);
					where.append(sm.whereSQL.replace("%", ""));
					continue;
				}

				where.append(" and ").append(sm.tableAs).append(sm.column).append(sm.searchType.code);
				if(sm.searchType == SearchType.in || sm.searchType == SearchType.in_not) {
					where.append(appendInOrNotIn(value, params));
				}else {
					if(sm.searchType == SearchType.like_left || sm.searchType == SearchType.like_right || sm.searchType == SearchType.like_all) {
						appendWhereLike(value.toString(), params, sm.searchType);
					}else if(sm.searchType == SearchType.between) {
						appendBetween(value, params);
					}else {
						params.add(value);
					}
				}
			}
		}
		String whereStr = where.append(sort).toString();
		if(whereStr.length() > 0) {
			for (Object param : params) {
				paramsList.add(param);
			}
			paramsMap.put(whereKey, params);
		}
		return whereStr;
	}

	//未做参数个数验证，可能导致SQL错误
	private static void appendWhereSQLParam(Object value, List<Object> params, String whereSQL) {
		List<String> symbol = getWhereSQLSymbol(whereSQL);
		if(value instanceof String) {
			String[] values = value.toString().split(",");
			if(values.length == symbol.size()) {
				for (int i = 0; i < symbol.size(); i++) {
					appendWhereSQLSymbol(values[i], params, symbol.get(i));
				}
				return;
			}
		}
		for (int i = 0; i < symbol.size(); i++) {
			appendWhereSQLSymbol(value, params, symbol.get(i));
		}
	}
	private static List<String> getWhereSQLSymbol(String whereSQL) {
		String m = "([\\s|=]\\?[\\s|\\)])|(\\s%\\?[\\s|\\)])|(\\s\\?%[\\s|\\)])|(\\s%\\?%[\\s|\\)])";
		Matcher matcher = Pattern.compile(m).matcher(whereSQL);
		List<String> symbol = new ArrayList<>();
		while (matcher.find()) {
			symbol.add(matcher.group().replace(" ", "").replace("=", "").replace(")", ""));
		}
		return symbol;
	}
	private static void appendWhereSQLSymbol(Object value, List<Object> params, String symbol) {
		if(symbol.indexOf("%?%") != -1) {
			params.add("%" + value + "%");
		}else if(symbol.indexOf("%?") != -1) {
			params.add("%" + value);
		}else if(symbol.indexOf("?%") != -1) {
			params.add(value + "%");
		}else {
			params.add(value);
		}
	}

	private static void appendBetween(Object value, List<Object> params) {
		String[] array = value.toString().split(",");
		params.add(array[0]);
		params.add(array.length > 1 ? array[1] : array[0]);
	}

	private static StringBuffer appendInOrNotIn(Object value, List<Object> params) {
		StringBuffer question = new StringBuffer("(");
		String[] array = value.toString().split(",");
		for (String item : array) {
			question.append("?,");
			params.add(item);
		}
		question.deleteCharAt(question.length()-1).append(')');
		return question;
	}
	
	private static void appendWhereLike(String value, List<Object> params, SearchType searchType) {
		if(searchType == SearchType.like_left) {
			params.add("%" + value);
		}else if(searchType == SearchType.like_right) {
			params.add(value + "%");
		}else if(searchType == SearchType.like_all) {
			params.add("%" + value + "%");
		}
	}
	
	// 判断一个Object是否为空
	private static boolean isBlankObj(Object obj) {
		if (obj == null)
			return true;
		if (obj.getClass().isArray()) {
			int length = Array.getLength(obj);
			for (int i = 0; i < length; i++) {
				Object item = Array.get(obj, i);
				if (item != null && item.toString().trim().length() > 0) {
					return false;
				}
			}
			return true;
		} else if (obj instanceof Map) {
			return ((Map<?, ?>) obj).size() == 0;
		} else if (obj instanceof Collection) {
			return ((Collection<?>) obj).size() == 0;
		} else {
			String str = obj.toString();
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
	}
	
}
