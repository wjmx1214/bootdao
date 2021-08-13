package com.boot.dao.util;

import lombok.extern.slf4j.Slf4j;

/**
 * DAO日志输出管理类
 * @author 2021-03-05 create wang.jia.le
 * @version 1.1.0
 */
@Slf4j
public abstract class BaseDAOLog {
	
	/**
	 * 显示SQL或参数
	 * @param showSQL
	 * @param showParam
	 * @param sql
	 * @param params
	 */
	public static void printSQLAndParam(boolean showSQL, boolean showParam, String sql, Object... params) {
		if(showSQL)
			log.info(sql);
		if(showParam && params != null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < params.length; i++) {
				sb.append(params[i] == null ? "null" : params[i].toString()).append(',');
			}
			int len = sb.length();
			sb = (len > 0) ? sb.replace(len-1, len, "]") : sb.append(']');
			sb.insert(0, "sqlParams: [");
			log.info(sb.toString());
		}
	}
	
	/**
	 * 输出异常日志
	 * @param e
	 */
	public static void printException(Exception e) {
		log.error("bootdao exception: ", e);
	}
	
	/**
	 * 输出异常日志
	 * @param e
	 */
	public static void printException(String msg, Exception e) {
		log.error("bootdao exception: " + msg, e);
	}
	
	/**
	 * 输出提示日志
	 * @param msg
	 */
	public static void info(String msg) {
		log.info(msg);
	}
	
}
