package com.boot.dao.api;

/**
 * 多条件动态查询方式枚举(简写版)
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.1
 */
public enum SType {
	
	/**
	 * = 等于
	 */
	eq		(" = ?"),
	/**
	 * <> 不等于
	 */
	eq_not	(" <> ?"),
	/**
	 * < 小于
	 */
	sma		(" < ?"),
	/**
	 * <= 小于等于
	 */
	sma_eq	(" <= ?"),
	/**
	 * > 大于
	 */
	big		(" > ?"),
	/**
	 * >= 大于等于
	 */
	big_eq	(" >= ?"),
	/**
	 * like %xxx<br>
	 * 当同一个列需要多个模糊匹配时, 可建立多个查询属性, 同时配置相同的列名或列别名
	 */
	like_l	(" like ?"),
	/**
	 * like xxx%<br>
	 * 当同一个列需要多个模糊匹配时, 可建立多个查询属性, 同时配置相同的列名或列别名
	 */
	like_r	(" like ?"),
	/**
	 * like %xxx%<br>
	 * 当同一个列需要多个模糊匹配时, 可建立多个查询属性, 同时配置相同的列名或列别名
	 */
	like_a	(" like ?"),
	/**
	 * in 逗号分隔字符串(替代 or xxx = xxx)
	 */
	in		(" in"),
	/**
	 * not in 逗号分隔字符串(替代 or xxx <> xxx)
	 */
	in_not	(" not in"),
	/**
	 * between 逗号分隔字符串
	 */
	bet		(" between ? and ?"),
	/**
	 * is empty 是空白
	 */
	em_is	(" = ''"),
	/**
	 * is not empty 不是空白
	 */
	em_not	(" <> ''"),
	/**
	 * is null 是空值
	 */
	nu_is	(" is null"),
	/**
	 * is not null 不是空值
	 */
	nu_not	(" is not null");
	
	public String code;
	SType(String code){
		this.code = code;
	}
	
	/**
	 * 获取枚举实例
	 * @param code
	 * @return SType
	 */
	public static SType getType(String code){
		for(SType type : SType.values()){
			if(type.code.equals(code)){
				return type;
			}
		}
		return null;
	}

}
