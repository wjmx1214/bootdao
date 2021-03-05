package com.boot.dao.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.boot.dao.api.SType;
import com.boot.dao.api.Search;
import com.boot.dao.api.SearchMeta;
import com.boot.dao.util.BaseDAOUtil;

/**
 * 多条件动态查询映射工具类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.1
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
			findMeta(field, sm);
			list.add(sm);
		}
		return list;
	}
	
	//查找注解
	private static void findMeta(Field field, BaseSearchMapping sm) {
		if(field.isAnnotationPresent(Search.class)){ //简写版
			Search search = field.getAnnotation(Search.class);
			sm.searchType = search.type();
			sm.columnName = search.column();
			sm.tableLabel = search.label();
			sm.whereIndex = search.index();
			if(sm.columnName.length() == 0) {
				sm.columnName = search.hump() ? BaseDAOUtil.humpToUnderline(field.getName()) : field.getName();
			}
			if(sm.tableLabel.length() > 0) {
				sm.tableLabel += ".";
			}
		}else if(field.isAnnotationPresent(SearchMeta.class)){ //全名版
			SearchMeta searchMeta = field.getAnnotation(SearchMeta.class);
			sm.searchType = SType.getType(searchMeta.searchType().code); //将查询类型,从全名版转化为简写版
			sm.columnName = searchMeta.columnName();
			sm.tableLabel = searchMeta.tableLabel();
			sm.whereIndex = searchMeta.whereIndex();
			if(sm.columnName.length() == 0) {
				sm.columnName = searchMeta.isHump() ? BaseDAOUtil.humpToUnderline(field.getName()) : field.getName();
			}
			if(sm.tableLabel.length() > 0) {
				sm.tableLabel += ".";
			}
		}else {
			sm.searchType = SType.eq;
			sm.columnName = BaseDAOUtil.humpToUnderline(field.getName());
			sm.tableLabel = "";
			sm.whereIndex = 1;
		}
	}

}
