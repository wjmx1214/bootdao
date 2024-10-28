package com.boot.dao.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.boot.dao.api.Hump;
import com.boot.dao.api.Search;
import com.boot.dao.api.SearchType;
import com.boot.dao.api.Sort;
import com.boot.dao.config.BaseDAOConfig;
import com.boot.dao.util.BaseDAOUtil;

/**
 * 多条件动态查询映射工具类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.1.7
 */
abstract class BaseSearchMappingUtil {

	//创建查询映射
	static List<BaseSearchMapping> createSearchMapping(Class<?> clz) {
		boolean isHumpClass = false;
		if(clz.isAnnotationPresent(Hump.class)) {
			isHumpClass = clz.getAnnotation(Hump.class).isHump();
		}
		
		List<BaseSearchMapping> list = new ArrayList<>();
		Field[] fields = BaseDAOUtil.getAllFields(clz); //获取该类型所有字段(自身公有，私有，父类公有, 直属父类私有)
		for (Field field : fields) {
			if(Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers()))
				continue;//当为final或static修饰时，则跳过

			boolean isHump = isHumpClass;
			if(field.isAnnotationPresent(Hump.class)) {
				isHump = field.getAnnotation(Hump.class).isHump();
			}
			BaseSearchMapping sm = findMapping(field, isHump);
			if(sm != null) {
				sm.fieldName = field.getName();
				sm.getMethod = BaseDAOUtil.findGetMethod(clz, sm.fieldName);
				if(sm.getMethod != null) {
					list.add(sm);
				}
			}
		}
		return list;
	}
	
	//查找注解
	private static BaseSearchMapping findMapping(Field field, boolean isHump) {
		BaseSearchMapping sm = null;
		if(field.isAnnotationPresent(Search.class)){
			Search search = field.getAnnotation(Search.class);
			if(!search.isMapping()) {
				return null;
			}
			sm = new BaseSearchMapping();
			sm.searchType = search.value() != SearchType.eq ? search.value() : search.type();
			sm.column = search.column();
			if(sm.column.length() == 0) {
				sm.column = isHump ? BaseDAOUtil.humpToUnderline(field.getName()) : field.getName();
			}
			sm.tableAs = search.tableAs();
			if(sm.tableAs.length() > 0) {
				sm.tableAs += ".";
			}
			sm.whereKey = search.whereKey();
			sm.sort = search.sort();
			sm.whereSQL = search.whereSQL().replace("\n", " ");
			if(search.dateFormat().length() > 0) {
				sm.datePattern = search.dateFormat();
				sm.isDate = true;
			}
		}else {
			sm = new BaseSearchMapping();
			sm.searchType = SearchType.eq;
			sm.column = BaseDAOUtil.humpToUnderline(field.getName());
			sm.tableAs = "";
			sm.whereKey = "";
			sm.sort = Sort.NOT;
			sm.whereSQL = "";
		}

		//日期格式化
		if(!sm.isDate && field.isAnnotationPresent(DateTimeFormat.class)) {
			DateTimeFormat format = field.getAnnotation(DateTimeFormat.class);
			if(format.pattern().trim().length() > 0) {
				sm.datePattern = format.pattern();
				sm.isDate = true;
			}else if(format.iso() != ISO.NONE) {
				if(format.iso() != ISO.DATE) {
					sm.datePattern = "yyyy-MM-dd";
				}else if(format.iso() != ISO.TIME) {
					sm.datePattern = "HH:mm:ss.SSSXXX";
				}else if(format.iso() != ISO.DATE_TIME) {
					sm.datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
				}
				sm.isDate = true;
			}
		}
		if(!sm.isDate) {
			if(field.getType() == Date.class || field.getType() == Date[].class 
					|| field.getGenericType() instanceof ParameterizedType && ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0] == Date.class) {
				sm.datePattern = field.getName().toLowerCase().equals("time") ? BaseDAOConfig.formatTime : BaseDAOConfig.formatDate;
				sm.isDate = true;
			}
		}
		return sm;
	}

}
