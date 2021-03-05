package com.boot.dao.mapping;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.mapping.MappingException;

import com.boot.dao.api.EntityPath;
import com.boot.dao.config.BaseDAOConfig;
import com.boot.dao.util.BaseDAOLog;
import com.boot.dao.util.BaseScanClassUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * DAO映射缓存类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.1
 */
@Slf4j
public abstract class BaseMappingCache {
	
	//多条件动态查询映射缓存(使用ConcurrentHashMap防止冲突, 不过就算线程冲突也没啥问题)
	private static Map<Class<?>, List<BaseSearchMapping>> searchMappingCache = new ConcurrentHashMap<>();
	
	/**
	 * 通过实例获取查询映射
	 * @param search
	 * @return List<BaseSearchMapping>
	 */
	public static List<BaseSearchMapping> getSearchMapping(Object search){
		if(search == null)
			return null;
		return getSearchMapping(search.getClass());
	}

	/**
	 * 通过类获取查询映射
	 * @param clz
	 * @return List<BaseSearchMapping>
	 */
	public static List<BaseSearchMapping> getSearchMapping(Class<?> clz){
		if(clz == null)
			return null;
		List<BaseSearchMapping> list = searchMappingCache.get(clz);
		if(list == null) {
			list = BaseSearchMappingUtil.createSearchMapping(clz);
			searchMappingCache.put(clz, list);
		}
		return list;
	}
	
	
	//-----------------------------------------------实体或DTO、VO映射缓存-------------------------------------------------

	//模板映射缓存
	private static Map<Class<?>, BaseTableMapping> tableMappingCache = new ConcurrentHashMap<>();
	
	private static BaseTableMapping createTableMappingCache(Class<?> clz) throws Exception {
		BaseTableMapping tm = BaseTableMappingUtil.createTableMapping(clz);
		if(tm.columnMappings.size() == 0) //未找到任何字段，则抛出映射异常
			throw new MappingException("mapping error! the class field = 0; className: " + clz.getName());
		tableMappingCache.put(clz, tm);
		return tm;
	}

	/**
	 * 通过实例获取映射模板
	 * @param t
	 * @return BaseTableMapping
	 */
	public static BaseTableMapping getTableMapping(Object t){
		if(t == null)
			return null;
		return getTableMapping(t.getClass());
	}

	/**
	 * 通过类获取映射模板
	 * @param clz
	 * @return BaseTableMapping
	 */
	public static BaseTableMapping getTableMapping(Class<?> clz){
		if(clz == null)
			return null;
		BaseTableMapping tm = tableMappingCache.get(clz);
		//未找到缓存
		if(tm == null){
			try {
				//尝试找对应的实体类
				Class<?> entityClz = getEntityClass(clz);
				if(entityClz != null) {
					//找到则查缓存
					BaseTableMapping entityTm = tableMappingCache.get(entityClz);
					if(entityTm == null) {
						entityTm = createTableMappingCache(entityClz);
					}
					//若自身是实体类
					if(entityClz == clz) {
						return entityTm;
					}
					//根据实体映射信息创建Dto或Vo类对应的映射信息
					tm = BaseTableMappingUtil.createByEntityMapping(clz, entityTm);
					tableMappingCache.put(clz, tm);
				}else {
					//未找到则缓存自身(也许自身就是实体类, 但未配置扫描)
					tm = createTableMappingCache(clz);
				}
			} catch (Exception e) {
				BaseDAOLog.printException(e);
			}
		}
		return tm;
	}
	

	//根据当前类型查找对应实体类型
	private static Class<?> getEntityClass(Class<?> clz){
		try {
			Class<?> entityClz = fromClassMeta(clz); //从类注解获取对应实体模板(优先于统一配置)
			if(entityClz == null)
				entityClz = fromConfig(clz); //从统一配置获取对应实体模板
			return entityClz;
		}catch (Exception e) {
			BaseDAOLog.printException(e);
			return null;
		}
	}
	
	//从类注解获取对应实体模板
	private static Class<?> fromClassMeta(Class<?> clz) throws Exception {
		if(clz.isAnnotationPresent(EntityPath.class)) {
			EntityPath entityPath = clz.getAnnotation(EntityPath.class);
			if(entityPath.value().length() > 0) {
				try {
					return Class.forName(entityPath.value());
				} catch (ClassNotFoundException e) {
					throw new MappingException("未找到类[" + clz.getName() + "]对应的实体类, 请检查注解映射... "
							+ "not found class [" + clz.getName() + "] mapping entity class, please check meta mapping...");
				}
			}
		}
		return null;
	}
	
	//从统一配置获取对应实体模板
	private static Class<?> fromConfig(Class<?> clz) throws Exception {
		String className = clz.getSimpleName();
		String differentName = getDifferentName(className);
		if(BaseDAOConfig.entityPaths == null) {
			return null;
		}
		String entityName = (differentName.length() == 0) ? className : className.substring(0, className.length() - differentName.length());
		for (String entityPath : BaseDAOConfig.entityPaths) {
			try {
				return Class.forName(entityPath + "." + entityName);
			} catch (ClassNotFoundException e) {}
		}
		throw new MappingException("未找到类[" + clz.getName() + "]对应的实体类, 请检查配置路径... "
				+ "not found class [" + clz.getName() + "] mapping entity class, please check config path...");

	}
	
	//若为非实体类，则返回名称的不同部分；返回""表示相同类名，或未配置的类型
	private static String getDifferentName(String className) {
		for (String differentName : BaseDAOConfig.differentNames) {
			if(className.indexOf(differentName) > -1){
				return differentName;
			}
		}
		return "";
	}
	
	//扫描实体包(包含子包)，并加载配置了相应注解的实体映射模型
	public static void scan() throws Exception {
		if(BaseDAOConfig.entityPaths == null)
			return;

		for (String entityPath : BaseDAOConfig.entityPaths) {
			List<String> classNames = BaseScanClassUtil.getClassName(entityPath, true);
			for (String className : classNames) {
				try {
					Class<?> clz = Class.forName(className);
					if(tableMappingCache.get(clz) == null) { //未发现缓存则创建
						BaseTableMapping tm = BaseTableMappingUtil.createTableMapping(clz);
						if(tm.metaType > 0) { //只缓存配置了ID注解的实体模板
							tableMappingCache.put(clz, tm);
							log.info("已扫描到实体类：" + className);
						}
					}
				} catch (ClassNotFoundException e) {}
			}
		}
	}
	
}
