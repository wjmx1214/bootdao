package com.boot.dao.mapping;

import java.lang.reflect.Field;

import com.boot.dao.api.SearchType;
import com.boot.dao.api.Sort;
import com.boot.dao.config.BaseDAOConfig;
import com.boot.dao.util.BaseDAOLog;

/**
 * 多条件动态查询映射
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.9
 */
public class BaseSearchMapping {

	public SearchType searchType;	//查询方式(默认=eq)
	public String tableAs;			//表别名(默认="")
	public String column;			//列名或列别名(默认=Field名称)(除非检测到驼峰转换显式关闭, 否则自动进行驼峰转下划线)
	public int index;				//条件索引(多表或子查询时, 若出现多处where, 则利用此索引进行区分)(默认=1, 即默认只有一处where)
	public Sort sort;				//排序规则
	public String whereSQL;			//自定义条件语句，用于复杂的条件判断
	Field searchField;				//对应的Field
	boolean isDate;					//是否为日期类型
	String formatTime = BaseDAOConfig.formatTime;	//当字段为日期类型时的格式化

	public Object searchFieldGet(Object search){
		try {
			Object value = searchField.get(search);
			if(isDate && value != null) {
				return new java.text.SimpleDateFormat(formatTime).format(value);
			}
			return value;
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		}
		return null;
	}

}
