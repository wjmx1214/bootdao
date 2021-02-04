package com.boot.dao.api;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.boot.dao.mapping.BaseMappingCache;
import com.boot.dao.mapping.BaseSearchMapping;

/**
 * 多条件动态查询父类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.0
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
		if(this.append)
			return this.SQL;
		List<Object> params = new ArrayList<>();
		List<String> qualifiers = this.findQualifier(SQL);
		for (int i = 0; i < qualifiers.size(); i++) {
			SQL = SQL.replace(qualifiers.get(i), this.appendWhere(i+1, params));
		}
		this.params = params.toArray();
		this.append = true;
		return this.SQL;
	}
	
	public final String appendWhere(String sql) {
		if(this.append)
			return this.SQL;
		this.SQL = sql;
		return this.appendWhere();
	}
	
	String appendWhere(int index, List<Object> params) {
		StringBuffer where = new StringBuffer();
		List<BaseSearchMapping> sms = BaseMappingCache.getSearchMapping(this.getClass());
		for (BaseSearchMapping sm : sms) {
			if(sm.whereIndex == index) {
				if(sm.searchType == SType.nu_is || sm.searchType == SType.nu_not) {
					where.append(" and ").append(sm.tableLabel).append(sm.columnName).append(sm.searchType.code);
					continue;
				}
				Object value = sm.searchFieldGet(this);
				if(isBlankObj(value)) {
					continue;
				}

				where.append(" and ").append(sm.tableLabel).append(sm.columnName).append(sm.searchType.code);
				if(sm.searchType == SType.in || sm.searchType == SType.in_not) {
					StringBuffer question = new StringBuffer("(");
					String[] array = value.toString().split(",");
					for (Object o : array) {
						question.append("?,");
						params.add(o);
					}
					question.deleteCharAt(question.length()-1).append(')');
					where.append(question);
				}else {
					if(sm.searchType == SType.like_l) {
						params.add("%" + value.toString());
					}else if(sm.searchType == SType.like_r) {
						params.add(value.toString() + "%");
					}else if(sm.searchType == SType.like_a) {
						params.add("%" + value.toString() + "%");
					}else if(sm.searchType == SType.bet) {
						String[] array = value.toString().split(",");
						params.add(array[0]);
						params.add(array[1]);
					}else {
						params.add(value);
					}
				}
			}
		}
		return where.toString();
	}
	
	List<String> findQualifier(String str) {
		List<String> list = new ArrayList<>();
		Matcher matcher = Pattern.compile("(#\\{.*?\\})").matcher(str); //prefix = "#"
		while (matcher.find()) {
			list.add(matcher.group(1));
		}
		return list;
	}
	
	//判断一个Object是否为空, 且toString()可转为字符串类型 (true为空)
	private static boolean isBlankObj(Object obj) {
        if (obj == null)
            return true;
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
