package com.boot.dao.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

/**
 * DAO工具类
 * @author 2020-12-01 create wang.jia.le
 * @author yyyy-MM-dd update
 * @version 1.0.0
 */
@Slf4j
public abstract class BaseDAOUtil {

	/**
	 * 对象复制(相同或不同对象)
	 * @param fromObj
	 * @param toObj
	 * @return
	 * @throws Exception
	 */
	public static <T> T copy(Object fromObj, T toObj) throws Exception{
		if(fromObj == null || toObj == null)
			return toObj;
		Class<?> fromClz = fromObj.getClass();
		Class<?> toClz = toObj.getClass();
		Field[] toFields = getAllFields(toClz);
		if(fromClz == toClz){ //同类型对象
			for(Field toField : toFields){
				Object fromObjValue = toField.get(fromObj);
				if(fromObjValue != null){
					toField.set(toObj, fromObjValue);
				}
			}
		}else{
			Field[] fromFields = getAllFields(fromClz);
			for(Field toField : toFields){
				for(Field fromField : fromFields){
					if(toField.getName().equals(fromField.getName()) && toField.getType() == fromField.getType()){
						Object fromObjValue = fromField.get(fromObj);
						if(fromObjValue != null){
							toField.set(toObj, fromObjValue);
						}
						break;
					}
				}
			}
		}
		return toObj;
	}

	//模板属性缓存(用于对象复制)
	private static Map<Class<?>, Field[]> classFieldCache = new ConcurrentHashMap<>();
	
	/**
	 * 获取指定类型所有的属性(自身公有，私有，父类公有)
	 * @param clz
	 * @return
	 */
	public static Field[] getAllFields(Class<?> clz){
		if(clz == null)
			return null;
		Field[] fields = classFieldCache.get(clz); //获取模板属性缓存
		if(fields != null)
			return fields;
		List<Field> list = new ArrayList<>();
		Field[] fields1 = clz.getDeclaredFields();
		Field[] fields2 = clz.getFields();
		for(Field f : fields1){
			if(Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers()))
				continue;//当为final或static修饰时，则跳过
			f.setAccessible(true); // 强制访问
			list.add(f);
		}
		for(Field f : fields2){
			if(Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers()))
				continue;//当为final或static修饰时，则跳过
			boolean exist = false;
			for(Field f1 : list){
				if(f.getName().equals(f1.getName())){
					exist = true;
					break;
				}
			}
			if(!exist){
				f.setAccessible(true); // 强制访问
				list.add(f);
			}
		}
		fields = list.toArray(new Field[]{});
		classFieldCache.put(clz, fields); //缓存模板属性
		return fields;
	}
	
	
	/**
	 * 设置参数
	 * @param ps
	 * @param params SQL语句中对应的?号参数
	 * @throws Exception
	 */
	public static void setParams(PreparedStatement ps, Object... params) throws Exception{
		if(params != null){
			for (int i = 0; i < params.length; i++) {
				if(params[i] != null){
					Class<?> clz = params[i].getClass();
					if(clz == String.class) {
						ps.setString(i+1, (String)params[i]);
					}else if(clz == Date.class) {
						long time = ((Date)params[i]).getTime();
						ps.setTimestamp(i+1, new java.sql.Timestamp(time));
					}else if(clz == LocalDate.class) {
						ps.setDate(i+1, java.sql.Date.valueOf((LocalDate)params[i]));
					}else if(clz == LocalTime.class) {
						ps.setTime(i+1, java.sql.Time.valueOf((LocalTime)params[i]));
					}else if(clz == LocalDateTime.class) {
						ps.setTimestamp(i+1, java.sql.Timestamp.valueOf((LocalDateTime)params[i]));
					}else {
						ps.setObject(i+1, params[i]);
					}
				}else{
					ps.setNull(i+1, Types.NUMERIC);
				}
			}
		}
	}
	
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
     * 字符串驼峰转下划线
     * @param str
     * @return
     */
	public static String humpToUnderline(String str) {
		//return str.replaceAll("[A-Z]", "_$0").toLowerCase();
        if (str == null || str.length() == 0)
            return str;
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        sb.append(Character.toLowerCase(str.charAt(0)));
        for (int i = 1; i < len; i++) {
        	char c0 = str.charAt(i - 1);
            char c = str.charAt(i);
            if (Character.isUpperCase(c) && Character.isLowerCase(c0)) {
                sb.append('_');
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }
	

	/**
	 * 拼接分页语句, 仅限MYSQL
	 * @param index 当前页 从1开始
	 * @param size 每页显示总数(若为0, 则代表不做分页)
	 * @return
	 */
	public static String appendPage(int index, int size){
		if(size < 1) 
			return "";
		if(index < 1)
			index = 1;
		return new StringBuffer(" LIMIT ").append((index-1)*size).append(',').append(size).toString();
	}
	
	/**
	 * 通过属性名获取对应get方法
	 * @param clz
	 * @param fieldName
	 * @return
	 */
	public Method findGetMethod(Class<?> clz, String fieldName) {
		if(fieldName == null || fieldName.length() == 0)
			return null;
	    String str1 = fieldName.substring(0, 1);
	    String str2 = fieldName.substring(1, fieldName.length());
	    String methodName = "get" + str1.toUpperCase() + str2;
	    try {
	        return clz.getMethod(methodName);
	    } catch (NoSuchMethodException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	/**
	 * 通过属性名获取对应set方法
	 * @param clz
	 * @param fieldName
	 * @param parameterTypes
	 * @return
	 */
	public Method findSetMethod(Class<?> clz, String fieldName, Class<?> parameterTypes) {
		if(fieldName == null || fieldName.length() == 0 || parameterTypes == null)
			return null;
	    String str1 = fieldName.substring(0, 1);
	    String str2 = fieldName.substring(1, fieldName.length());
	    String methodName = "set" + str1.toUpperCase() + str2;
	    try {
	        return clz.getMethod(methodName, parameterTypes);
	    } catch (NoSuchMethodException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
}