package com.boot.dao.api;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.boot.dao.mapping.BaseMappingCache;
import com.boot.dao.mapping.BaseSearchMapping;
import com.boot.dao.mapping.BaseTableMapping;
import com.boot.dao.util.BaseDAOUtil;

/**
 * 多条件动态查询父类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.1.0
 */
public abstract class BaseSearch{

	public String SQL;
	public Object[] params;	//参数数组
	boolean append = false; //是否已拼接
	
	public void clear(){
		this.SQL = null;
		this.params = null;
		this.append = false;
	}
	
	public String appendWhere() {
		return this.appendWhere(Integer.class); //由于函数多态问题，此处无法用null作为参数，后面使用时进行判断
	}
	
	public String appendWhere(Class<?> clz) {
		if(this.append)
			return this.SQL;
		List<Object> params = new ArrayList<>();
		List<String> qualifiers = this.findQualifier(SQL);
		for (int i = 0; i < qualifiers.size(); i++) {
			SQL = SQL.replace(qualifiers.get(i), this.appendWhere(i+1, params, clz));
		}
		this.params = params.toArray();
		this.append = true;
		return this.SQL;
	}
	
	public final String appendWhere(String sql) {
		return this.appendWhere(sql, Integer.class); //由于函数多态问题，此处无法用null作为参数，后面使用时进行判断
	}
	
	public final String appendWhere(String sql, Class<?> clz) {
		if(this.append)
			return this.SQL;
		this.SQL = sql;
		return this.appendWhere(clz);
	}
	
	String appendWhere(int index, List<Object> params, Class<?> clz) {
		StringBuffer sort = new StringBuffer();
		StringBuffer where = new StringBuffer();
		BaseTableMapping btm = clz == Integer.class ? null : BaseMappingCache.getTableMapping(clz);
		List<BaseSearchMapping> sms = BaseMappingCache.getSearchMapping(this.getClass());
		for (BaseSearchMapping sm : sms) {
			if(btm != null && btm.columnMappings.get(sm.column) == null) {
				continue; //当实体类映射存在，而该列不存在时跳过，用于多表共用Search类时，过滤非当前表的列
			}
			
			if(sm.index == index) {
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
					where.append(sm.whereSQL);
					int count = BaseDAOUtil.subStringCount(sm.whereSQL, "?");
					appendWhereSQL(value, params, count);
					continue;
				}

				where.append(" and ").append(sm.tableAs).append(sm.column).append(sm.searchType.code);
				if(sm.searchType == SearchType.in || sm.searchType == SearchType.in_not) {
					where.append(appendInOrNotIn(value, params));
				}else {
					if(sm.searchType == SearchType.like_left) {
						params.add("%" + value.toString());
					}else if(sm.searchType == SearchType.like_right) {
						params.add(value.toString() + "%");
					}else if(sm.searchType == SearchType.like_all) {
						params.add("%" + value.toString() + "%");
					}else if(sm.searchType == SearchType.between) {
						appendBetween(value, params);
					}else {
						params.add(value);
					}
				}
			}
		}
		return where.append(sort).toString();
	}
	
	//未做参数个数验证，可能导致SQL错误
	private void appendWhereSQL(Object value, List<Object> params, int count) {
		if(value instanceof String) {
			String[] values = value.toString().split(",");
			if(values.length == count) {
				for (int i = 0; i < count; i++) {
					params.add(values[i]);
				}
				return;
			}
		}
		for (int i = 0; i < count; i++) {
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
	
	List<String> findQualifier(String str) {
		List<String> list = new ArrayList<>();
		Matcher matcher = Pattern.compile("(#\\{.*?\\})").matcher(str); //prefix = "#"
		while (matcher.find()) {
			list.add(matcher.group(1));
		}
		return list;
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
