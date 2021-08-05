package com.boot.dao.api;

/**
 * 多条件动态查询方式枚举
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.7
 */
public enum SearchType {
	
	/**
	 * = 等于
	 */
	eq			(" = ?"),
	/**
	 * <> 不等于
	 */
	eq_not		(" <> ?"),
	/**
	 * < 小于
	 */
	small		(" < ?"),
	/**
	 * <= 小于等于
	 */
	small_eq	(" <= ?"),
	/**
	 * > 大于
	 */
	big			(" > ?"),
	/**
	 * >= 大于等于
	 */
	big_eq		(" >= ?"),
	/**
	 * like %xxx<br>
	 * 当同一个列需要多个模糊匹配时, 可建立多个查询属性, 同时配置相同的列名或列别名
	 */
	like_left	(" like ?"),
	/**
	 * like xxx%<br>
	 * 当同一个列需要多个模糊匹配时, 可建立多个查询属性, 同时配置相同的列名或列别名
	 */
	like_right	(" like ?"),
	/**
	 * like %xxx%<br>
	 * 当同一个列需要多个模糊匹配时, 可建立多个查询属性, 同时配置相同的列名或列别名
	 */
	like_all	(" like ?"),
	/**
	 * in 数组 | 集合 | 逗号分隔字符串(替代 or xxx = xxx)
	 */
	in			(" in"),
	/**
	 * not in 数组 | 集合 | 逗号分隔字符串(替代 or xxx <> xxx)
	 */
	in_not		(" not in"),
	/**
	 * between 数组 | 集合 | 逗号分隔字符串
	 */
	between		(" between ? and ?"),
	/**
	 * is empty 是空白
	 */
	empty_is	(" = ''"),
	/**
	 * is not empty 不是空白
	 */
	empty_not	(" <> ''"),
	/**
	 * is null 是空值
	 */
	null_is		(" is null"),
	/**
	 * is not null 不是空值
	 */
	null_not	(" is not null");
	
	public String code;
	SearchType(String code){
		this.code = code;
	}
	
	/**
	 * 获取枚举实例
	 * @param code
	 * @return SearchType
	 */
	public static SearchType getType(String code){
		for(SearchType type : SearchType.values()){
			if(type.code.equals(code)){
				return type;
			}
		}
		return null;
	}

}
