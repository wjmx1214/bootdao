package com.boot.dao.mapping;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.boot.dao.api.SearchType;
import com.boot.dao.api.Sort;
import com.boot.dao.config.BaseDAOConfig;
import com.boot.dao.util.BaseDAOLog;

/**
 * 多条件动态查询映射
 * @author 2020-12-01 create wang.jia.le
 * @version 1.1.0
 */
public class BaseSearchMapping {

	public SearchType searchType;	//查询方式(默认=eq)
	public String tableAs;			//表别名(默认="")
	public String column;			//列名或列别名(默认=Field名称)(除非检测到驼峰转换显式关闭, 否则自动进行驼峰转下划线)
	public int index;				//条件索引(多表或子查询时, 若出现多处where, 则利用此索引进行区分)(默认=1, 即默认只有一处where)
	public Sort sort;				//排序规则
	public String whereSQL;			//自定义条件语句，用于复杂的条件判断
	Field searchField;				//对应的Field

	boolean isDate;					//是否为日期格式
	String formatTime = BaseDAOConfig.formatTime;	//当字段为日期类型时的格式化样式
	private SimpleDateFormat formatUtil;
	private SimpleDateFormat parseUtil = new SimpleDateFormat(BaseDAOConfig.formatTime);

	private SimpleDateFormat getFormatUtil() {
		if(this.formatUtil == null) {
			this.formatUtil = new SimpleDateFormat(formatTime);
		}
		return this.formatUtil;
	}

	public Object searchFieldGet(Object search){
		try {
			Object value = searchField.get(search);
			if(value != null) {
				if(isDate) {
					try {
						return formatDateArray(value);
					} catch (ParseException e) {
						BaseDAOLog.printException("the Field(" + searchField.getName() + ") value(" + value.toString() + ") can't parse Date!", e);
					} 
				}else if(value.getClass().isArray() || value instanceof List || value instanceof Set) {
					return arrayToString(value);
				}
			}
			return value;
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		}
		return null;
	}
	
	private Object arrayToString(Object value) {
		String str = "";
		if(value.getClass().isArray()) {
        	for (int i = 0; i < Array.getLength(value); i++) {
        		str += Array.get(value, i) + ",";
			}
		}else if(value instanceof List) {
			List<?> list = ((List<?>)value);
			for (Object item : list) {
				str += item + ",";
			}
		}else if(value instanceof Set) {
			Set<?> set = ((Set<?>)value);
			for (Object item : set) {
				str += item + ",";
			}
		}
		if(str.length() > 0) {
			return str.substring(0, str.length() - 1);
		}
		return value;
	}
	
	private Object formatDateArray(Object value) throws Exception {
		String str = "";
		if(value instanceof Date) {
			return formatDate(value);
		}else if(value.getClass().isArray()) {
        	for (int i = 0; i < Array.getLength(value); i++) {
        		str += formatDate(Array.get(value, i)) + ",";
			}
		}else if(value instanceof List) {
			List<?> list = ((List<?>)value);
			for (Object item : list) {
				str += formatDate(item) + ",";
			}
		}else if(value instanceof Set) {
			Set<?> set = ((Set<?>)value);
			for (Object item : set) {
				str += formatDate(item) + ",";
			}
		}else if(value instanceof String) {
			String[] values = value.toString().split(",");
			for (String item : values) {
				str += formatDate(item) + ",";
			}
		}
		if(str.length() > 0) {
			return str.substring(0, str.length() - 1);
		}
		return value;
	}
	
	private String formatDate(Object item) throws ParseException {
		if(item != null) {
			if(item instanceof Date) {
				return getFormatUtil().format(item);
			}else if(item instanceof String) {
				String dateStr = item.toString();
				Date date = null;
				try {
					date = parseUtil.parse(dateStr);
				} catch (ParseException e) {
					parseUtil.applyPattern("yyyy-MM-dd");
					try {
						date = parseUtil.parse(dateStr);
					} catch (ParseException e1) {
						parseUtil.applyPattern("HH:mm:ss");
						date = parseUtil.parse(dateStr);
					}
				}
				return getFormatUtil().format(date);
			}
			return item.toString();
		}
		return null;
	}

}
