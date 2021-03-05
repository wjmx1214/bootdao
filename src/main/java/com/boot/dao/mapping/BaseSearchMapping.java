package com.boot.dao.mapping;

import java.lang.reflect.Field;

import com.boot.dao.api.SType;
import com.boot.dao.util.BaseDAOLog;

/**
 * 多条件动态查询映射
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.1
 */
public class BaseSearchMapping {

	public SType searchType;	//查询方式(默认=eq)
	public String tableLabel;	//表别名(默认="")
	public String columnName;	//列名或列别名(默认=Field名称)(除非检测到驼峰转换显式关闭, 否则自动进行驼峰转下划线)
	public int whereIndex;		//条件索引(多表或子查询时, 若出现多处where, 则利用此索引进行区分)(默认=1, 即默认只有一处where)
	Field searchField;			//对应的Field

	public Object searchFieldGet(Object search){
		try {
			return searchField.get(search);
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		}
		return null;
	}

}
