package com.boot.dao.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.boot.dao.api.Search;
import com.boot.dao.api.SearchType;
import com.boot.dao.api.Sort;
import com.boot.dao.util.BaseDAOUtil;

/**
 * 多条件动态查询映射工具类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.1.0
 */
abstract class BaseSearchMappingUtil {

	//创建查询映射
	static List<BaseSearchMapping> createSearchMapping(Class<?> clz) {
		List<BaseSearchMapping> list = new ArrayList<>();
		Field[] fields = clz.getDeclaredFields(); //获取该类型所有字段，包括私有字段，但不包括继承字段
		for (Field field : fields) {
			if(Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers()))
				continue;//当为final或static修饰时，则跳过

			BaseSearchMapping sm = findMapping(field);
			if(sm != null) {
				field.setAccessible(true); //将字段设置为可强制访问
				sm.searchField = field;
				list.add(sm);
			}
		}
		return list;
	}
	
	//查找注解
	private static BaseSearchMapping findMapping(Field field) {
		BaseSearchMapping sm = null;
		if(field.isAnnotationPresent(Search.class)){
			Search search = field.getAnnotation(Search.class);
			if(!search.isMapping()) {
				return null;
			}
			sm = new BaseSearchMapping();
			sm.searchType = search.value() != SearchType.eq ? search.value() : search.type();
			sm.column = search.column();
			sm.tableAs = search.tableAs();
			sm.index = search.index();
			if(sm.column.length() == 0) {
				sm.column = search.isHump() ? BaseDAOUtil.humpToUnderline(field.getName()) : field.getName();
			}
			if(sm.tableAs.length() > 0) {
				sm.tableAs += ".";
			}
			sm.sort = search.sort();
			sm.whereSQL = search.whereSQL();
			if(search.dateFormat().length() > 0) {
				sm.formatTime = search.dateFormat();
				sm.isDate = true;
			}
		}else {
			sm = new BaseSearchMapping();
			sm.searchType = SearchType.eq;
			sm.column = BaseDAOUtil.humpToUnderline(field.getName());
			sm.tableAs = "";
			sm.index = 1;
			sm.sort = Sort.NOT;
			sm.whereSQL = "";
		}

		//日期格式化
		if(!sm.isDate && field.isAnnotationPresent(DateTimeFormat.class)) {
			DateTimeFormat format = field.getAnnotation(DateTimeFormat.class);
			if(format.pattern().trim().length() > 0) {
				sm.formatTime = format.pattern();
				sm.isDate = true;
			}else if(format.iso() != ISO.NONE) {
				if(format.iso() != ISO.DATE) {
					sm.formatTime = "yyyy-MM-dd";
				}else if(format.iso() != ISO.TIME) {
					sm.formatTime = "HH:mm:ss.SSSXXX";
				}else if(format.iso() != ISO.DATE_TIME) {
					sm.formatTime = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
				}
				sm.isDate = true;
			}
		}
		if(!sm.isDate) {
			if(field.getType() == Date.class || field.getType() == Date[].class 
					|| field.getGenericType() instanceof ParameterizedType && ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0] == Date.class) {
				sm.isDate = true;
			}
		}
		return sm;
	}

}
