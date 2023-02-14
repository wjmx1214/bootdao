package com.boot.dao.mapping;

import java.lang.reflect.Field;

/**
 * 列映射
 * @author 2020-12-01 create wang.jia.le
 * @version 1.1.5
 */
public class BaseColumnMapping {

	public String columnName = "";
	public boolean saveMapping = true;
	public boolean createMapping = true;
	public boolean updateMapping = true;
	public boolean saveEmpty = false;
	public boolean saveNull = false;
	public Field field;
	public String datePattern;

	public BaseColumnMapping(){}

	public BaseColumnMapping(String columnName, Field field, boolean saveMapping, boolean createMapping, boolean updateMapping, boolean saveEmpty, boolean saveNull) {
		this.columnName = columnName;
		this.field = field;
		this.saveMapping = saveMapping;
		this.createMapping = createMapping;
		this.updateMapping = updateMapping;
		this.saveEmpty = saveEmpty;
		this.saveNull = saveNull;
	}

	public BaseColumnMapping(String columnName, Field field, boolean saveMapping, boolean createMapping, boolean updateMapping, boolean saveEmpty, boolean saveNull, String datePattern) {
		this(columnName, field, saveMapping, createMapping, updateMapping, saveEmpty, saveNull);
		this.datePattern = datePattern;
	}
}
