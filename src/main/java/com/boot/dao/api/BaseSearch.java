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
 * 注意：若该Search类用于多个查询业务共用时，请设置业务类型<br>
 * 正常情况下无需指定，主要用于区分字段属于哪个业务<br>
 * @author 2020-12-01 create wang.jia.le
 * @version 1.1.1
 */
public abstract class BaseSearch{

	public String SQL;
	public Object[] params;	//参数数组
	boolean append = false; //是否已拼接
	
	/**
	 * 业务类型，用于多个业务共用同一个Search时，区分字段属于哪个业务
	 */
	public String businessName;
	
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
		this.SQL = this.SQL.replace("\n", " ");
		if(countSQL != null) {
			countSQL = countSQL.replace("\n", " ");
		}
		
		List<Object> paramsList = new ArrayList<>();
		Map<String, List<Object>> paramsMap = new LinkedHashMap<>();
		Map<String, String> whereKeys = findWhereKey();
		List<String> qualifiers = this.findQualifier(SQL);
		for (String qualifier : qualifiers) {
			String whereKey = qualifier.trim();
			whereKey = whereKeys.get(whereKey.substring(2, whereKey.length() - 1));
			whereKey = whereKey == null ? "default_search" : whereKey;
			String where = this.appendWhereAndParam(whereKey, paramsList, paramsMap);
			if(where != null) {
				SQL = SQL.replace(qualifier, where);
				if(countSQL != null) {
					countSQL = countSQL.replace(qualifier, where);
				}
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
	
	private List<String> findQualifier(String str) {
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
			return null;
		}
		
		StringBuffer sort = new StringBuffer();
		StringBuffer where = new StringBuffer();
		List<BaseSearchMapping> sms = BaseMappingCache.getSearchMapping(this.getClass());
		for (BaseSearchMapping sm : sms) {
			if(sm.businessName.length() > 0 && !sm.businessName.equals(this.businessName)) {
				continue; //该字段不属于本次业务查询；用于多个业务共用XxxSearch类时，过滤非当前业务的列
			}
			
			if("default_search".equals(whereKey) && sm.whereKey.length() == 0 || sm.whereKey.equals(whereKey)) {
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
			return whereStr;
		}
		return null;
	}

	//未做参数个数验证，可能导致SQL错误
	private void appendWhereSQLParam(Object value, List<Object> params, String whereSQL) {
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
	private List<String> getWhereSQLSymbol(String whereSQL) {
		String m = "([\\s|=]\\?[\\s|\\)])|(\\s%\\?[\\s|\\)])|(\\s\\?%[\\s|\\)])|(\\s%\\?%[\\s|\\)])";
		Matcher matcher = Pattern.compile(m).matcher(whereSQL);
		List<String> symbol = new ArrayList<>();
		while (matcher.find()) {
			symbol.add(matcher.group().replace(" ", "").replace("=", "").replace(")", ""));
		}
		return symbol;
	}
	private void appendWhereSQLSymbol(Object value, List<Object> params, String symbol) {
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
	
	//未做参数个数验证，可能导致SQL错误
	private void appendBetween(Object value, List<Object> params) {
		String[] array = value.toString().split(",");
		params.add(array[0]);
		params.add(array[1]);
	}

	private StringBuffer appendInOrNotIn(Object value, List<Object> params) {
		StringBuffer question = new StringBuffer("(");
		String[] array = value.toString().split(",");
		for (String item : array) {
			question.append("?,");
			params.add(item);
		}
		question.deleteCharAt(question.length()-1).append(')');
		return question;
	}
	
	private void appendWhereLike(String value, List<Object> params, SearchType searchType) {
		if(searchType == SearchType.like_left) {
			params.add("%" + value);
		}else if(searchType == SearchType.like_right) {
			params.add(value + "%");
		}else if(searchType == SearchType.like_all) {
			params.add("%" + value + "%");
		}
	}
	
	//判断一个Object是否为空
	private static boolean isBlankObj(Object obj) {
        if (obj == null)
            return true;
        if(obj.getClass().isArray()) {
        	int length = Array.getLength(obj);
        	for (int i = 0; i < length; i++) {
        		Object item = Array.get(obj, i);
				if(item != null && item.toString().trim().length() > 0) {
					return false;
				}
			}
        	return true;
        }else if(obj instanceof Map) {
        	return ((Map<?,?>)obj).size() == 0;
        }else if(obj instanceof Collection) {
        	return ((Collection<?>)obj).size() == 0;
        }else {
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
