package com.boot.dao.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.boot.dao.api.Search;
import com.boot.dao.api.SearchType;
import com.boot.dao.util.BaseDAOUtil;

/**
 * 多条件动态查询映射工具类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.5
 */
abstract class BaseSearchMappingUtil {

	//创建查询映射
	static List<BaseSearchMapping> createSearchMapping(Class<?> clz) {
		List<BaseSearchMapping> list = new ArrayList<>();
		Field[] fields = clz.getDeclaredFields(); //获取该类型所有字段，包括私有字段，但不包括继承字段
		for (Field field : fields) {
			if(Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers()))
				continue;//当为final或static修饰时，则跳过
			
			BaseSearchMapping sm = new BaseSearchMapping();
			field.setAccessible(true); //将字段设置为可强制访问
			sm.searchField = field;
			findMapping(field, sm);
			list.add(sm);
		}
		return list;
	}
	
	//查找注解
	private static void findMapping(Field field, BaseSearchMapping sm) {
		if(field.isAnnotationPresent(Search.class)){
			Search search = field.getAnnotation(Search.class);
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
		}else {
			sm.searchType = SearchType.eq;
			sm.column = BaseDAOUtil.humpToUnderline(field.getName());
			sm.tableAs = "";
			sm.index = 1;
		}
	}

}
