package com.boot.dao.mapping;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.boot.dao.api.SearchType;
import com.boot.dao.api.Sort;
import com.boot.dao.util.BaseDAOLog;

/**
 * 多条件动态查询映射
 * @author 2020-12-01 create wang.jia.le
 * @version 1.1.7
 */
public class BaseSearchMapping {

	public SearchType searchType;	//查询方式(默认=eq)
	public String tableAs;			//表别名(默认="")
	public String column;			//列名或列别名(默认=Field名称)
	public Sort sort;				//排序规则
	public String whereKey;			//多处不同where条件定位标识(多表或子查询时, 若出现多处where或having, 则利用此标识进行区分)(默认为空, 即默认只有一处where)
	public String whereSQL;			//自定义条件语句，用于复杂的条件判断
	Method getMethod;				//对应的get方法
	String fieldName;				//对应的Field名称

	boolean isDate;					//是否为日期格式
	String datePattern;				//当字段为日期类型时的格式化样式

	public Object searchFieldGet(Object search){
		try {
			Object value = getMethod.invoke(search);
			if(value != null) {
				if(isDate) {
					try {
						Object value1 = formatDateArray(value);
						return isBlankObj(value1) ? null : value1;
					} catch (ParseException e) {
						BaseDAOLog.printException("the Field(" + fieldName + ") value(" + value.toString() + ") can't parse Date!", e);
					} 
				}else if(value.getClass().isArray() || value instanceof List || value instanceof Set) {
					Object value1 =  arrayToString(value);
					return isBlankObj(value1) ? null : value1;
				}else if(isBlankObj(value)) {
					value = null;
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
			if(datePattern != null && datePattern.length() > 0) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
				if(item instanceof Date) {
					return dateFormat.format(item);
				}else if(item instanceof String) {
					String dateStr = item.toString();
					Date date = null;
					try {
						date = dateFormat.parse(dateStr);
					} catch (ParseException e) {
						dateFormat.applyPattern("yyyy-MM-dd");
						try {
							date = dateFormat.parse(dateStr);
						} catch (ParseException e1) {
							dateFormat.applyPattern("HH:mm:ss");
							date = dateFormat.parse(dateStr);
						}
					}
					return dateFormat.format(date);
				}
			}
			return item.toString();
		}
		return null;
	}
	
	// 判断一个Object是否为空
	private static boolean isBlankObj(Object obj) {
		if (obj == null)
			return true;
		if (obj.getClass().isArray()) {
			int length = Array.getLength(obj);
			for (int i = 0; i < length; i++) {
				Object item = Array.get(obj, i);
				if (item != null && item.toString().trim().length() > 0) {
					return false;
				}
			}
			return true;
		} else if (obj instanceof Map) {
			return ((Map<?, ?>) obj).size() == 0;
		} else if (obj instanceof Collection) {
			return ((Collection<?>) obj).size() == 0;
		} else {
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
