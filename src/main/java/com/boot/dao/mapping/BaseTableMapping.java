package com.boot.dao.mapping;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.boot.dao.util.BaseDAOLog;

/**
 * 表映射
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.5
 */
public class BaseTableMapping {

	public String tableName = "";				//表名
	public String idColumnName = "";			//ID列名
	public Field idField;						//ID对应的Field
	public boolean idAuto = false;				//ID是否为自增
	public BaseColumnMapping createTime;		//创建时间列映射(根据名称createTime或createDate推理)
	public boolean hasCreateTime = false;		//是否有创建时间, 用于DTO、VO类型新增判断(新增记录时根据配置决定是否自动生成)
	public boolean isHump = true;				//是否开启驼峰转换
	public boolean isEntity = true;				//当前映射是否为Entity
	public int mappingType = 0;					//当前实际的注解方式(没有或未知ID注解=0, EntityTable=1, mybatis-plus=2, JPA=3)(=0时仅支持SQL查询)

	public Map<String, BaseColumnMapping> columnMappings = new HashMap<>(); //列映射集合(列名称:列映射)
	public Map<String, BaseColumnMapping> fieldMappings = new HashMap<>(); 	//列映射集合(属性名:列映射)

	public Serializable idFieldGet(Object obj) {
		Serializable s = null;
		try {
			Object id = idField.get(obj);
			if(!isBlankObj(id) && !"0".equals(id.toString())) {
				s = (Serializable)id;
			}
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		}
		return s;
	}
	
	public void idFieldSet(Object obj, Serializable value) throws Exception {
		idField.set(obj, value);
	}
	
	//判断一个Object是否为空, 且toString()可转为字符串类型 (true为空)
	private static boolean isBlankObj(Object obj) {
        if (obj == null)
            return true;
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
