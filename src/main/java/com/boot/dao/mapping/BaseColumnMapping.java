package com.boot.dao.mapping;

import java.lang.reflect.Field;

/**
 * 列映射
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.7
 */
public class BaseColumnMapping {

	public String columnName = "";
	public boolean saveMapping = true;
	public boolean createMapping = true;
	public boolean updateMapping = true;
	public Field field;
	public String formatTime;

	public BaseColumnMapping(){}

	public BaseColumnMapping(String columnName, Field field, boolean saveMapping, boolean createMapping, boolean updateMapping) {
		this.columnName = columnName;
		this.field = field;
		this.saveMapping = saveMapping;
		this.createMapping = createMapping;
		this.updateMapping = updateMapping;
	}

	public BaseColumnMapping(String columnName, Field field, boolean saveMapping, boolean createMapping, boolean updateMapping, String formatTime) {
		this(columnName, field, saveMapping, createMapping, updateMapping);
		this.formatTime = formatTime;
	}
}
