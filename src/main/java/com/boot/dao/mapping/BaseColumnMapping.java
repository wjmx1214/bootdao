package com.boot.dao.mapping;

import java.lang.reflect.Field;

/**
 * 列映射
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.0
 */
public class BaseColumnMapping {

	public String columnName = "";
	public Field field;
	public String formatDate;

	public BaseColumnMapping(){}

	public BaseColumnMapping(String columnName, Field field) {
		this.columnName = columnName;
		this.field = field;
	}

	public BaseColumnMapping(String columnName, Field field, String formatDate) {
		this(columnName, field);
		this.formatDate = formatDate;
	}
}
