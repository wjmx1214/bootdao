package com.boot.dao.config;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

import com.boot.dao.util.BaseDAOLog;

/**
 * 多数据源其它配置创建方式
 * @author 2025-01-20 create wang.jia.le
 * @version 1.2.0
 */
@SuppressWarnings("unchecked")
public class DataSourceMoreCreate {

	private static String druidType = "com.alibaba.druid.pool.DruidDataSource";
	private static Method createMethod;
	private static Method buildMethod;

	static {
		try {
			Class<?> druidDataSourceBuilderClass = Thread.currentThread().getContextClassLoader()
					.loadClass("com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder");
			createMethod = druidDataSourceBuilderClass.getDeclaredMethod("create");
			buildMethod = druidDataSourceBuilderClass.getDeclaredMethod("build");
		} catch (Exception e) {
		}
	}

	// 创建其它方式的数据源
	public static Map<String, DataSource> createOtherDataSource(ConfigurableEnvironment environment) {
		Map<String, DataSource> dataSources = new HashMap<>();
		Map<String, Map<String, String>> sourceMaps = fetchDataSourceProperties(environment);
		sourceMaps.forEach((sourceName, sourceConfig) -> {
			DataSource dataSource = createDataSource("spring." + sourceName, sourceConfig);
			dataSources.put(sourceName, dataSource);
		});
		return dataSources;
	}

	// 获取其它方式的数据源配置
	private static Map<String, Map<String, String>> fetchDataSourceProperties(ConfigurableEnvironment environment) {
		Map<String, Map<String, String>> sourceMaps = new HashMap<>();
		Map<String, Integer> sourceNameMap = new HashMap<>();
		// 获取环境中的所有 PropertySource
		for (PropertySource<?> propertySource : environment.getPropertySources()) {
			if (propertySource.getSource() instanceof Map) {
				Map<String, Object> allMap = (Map<String, Object>) propertySource.getSource();
				boolean flag = false;
				for (String key : allMap.keySet()) {
					String lowerKey = key.toLowerCase();
					if (lowerKey.startsWith("spring")) {
						if (lowerKey.contains("driver-class-name")) {
							String[] propertyName = lowerKey.split("\\.");
							if (!"datasource".equals(propertyName[1].toLowerCase())) {
								sourceNameMap.put(propertyName[1], 1);
								flag = true;
							}
						}
					}
				}
				if(!flag) {
					continue;
				}
				for (String sourceName : sourceNameMap.keySet()) {
					Map<String, String> sourceMap = new HashMap<>();
					for (String key : allMap.keySet()) {
						String lowerKey = key.toLowerCase();
						if (lowerKey.startsWith("spring." + sourceName.toLowerCase())) {
							sourceMap.put(key, allMap.get(key).toString());
						}
					}
					sourceMaps.put(sourceName, sourceMap);
				}
			}
		}
		return sourceMaps;
	}

	private static DataSource createDataSource(String prefix, Map<String, String> properties) {
		try {
			// 获取数据源类型
			String type = properties.get(prefix + ".type");
			DataSource dataSource = createDataSource(type);
			// 动态绑定属性到数据源
			bindPropertiesToDataSource(dataSource, properties);
			return dataSource;
		} catch (Exception e) {
			throw new RuntimeException("Failed to create DataSource", e);
		}
	}

	private static void bindPropertiesToDataSource(Object dataSource, Map<String, String> properties) throws Exception {
		for (String key : properties.keySet()) {
			String value = properties.get(key);
			Method setter = null;
			try {
				// 将属性名转换为 setter 方法名
				String name = key.substring(key.lastIndexOf('.')+1);
				name = convertToCamelCase(name);
				String setterName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
				setter = findSetterMethod(dataSource, setterName, value);
				if(setter == null) {
					if("setUrl".equals(setterName)) {
						setter = findSetterMethod(dataSource, "setJdbcUrl", value);
					} else if("setJdbcUrl".equals(setterName)) {
						setter = findSetterMethod(dataSource, "setUrl", value);
					}
				}
			} catch (Exception e) {
				// 忽略无法绑定的属性
				System.out.println("Failed to bind property: " + key + " to " + dataSource.getClass().getName());
			}
			if (setter != null) {
				Class<?> parameterType = setter.getParameterTypes()[0];
				if (parameterType == String.class) {
					setter.invoke(dataSource, value);
				} else if (parameterType == int.class ||  parameterType == Integer.class) {
					setter.invoke(dataSource, Integer.parseInt(value));
				} else if (parameterType == long.class ||  parameterType == Long.class) {
					setter.invoke(dataSource, Long.parseLong(value));
				} else if (parameterType == double.class ||  parameterType == Double.class) {
					setter.invoke(dataSource, Double.parseDouble(value));
				} else if (parameterType == boolean.class ||  parameterType == Boolean.class) {
					setter.invoke(dataSource, Boolean.parseBoolean(value));
				} else {
					setter.invoke(dataSource, value);
				}
			}
		}
	}
	
	private static DataSource createDataSource(String type) {
		DataSource dataSource = null;
		if (type != null && druidType.equals(type)) {
			if (buildMethod != null) {
				try {
					dataSource = (DataSource) buildMethod.invoke(createMethod.invoke(null)); // 阿里巴巴连接池druid创建方式
				} catch (Exception e) {
					BaseDAOLog.printException(e);
				}
			} else {
				BaseDAOLog.printException(new ClassNotFoundException("not found " + druidType));
			}
		} else {
			dataSource = DataSourceBuilder.create().build(); // Spring默认使用的连接池创建方式(未配置type时为hikariCP)
		}
		return dataSource;
	}

	private static Method findSetterMethod(Object target, String setterName, Object value) {
		Method[] methods = target.getClass().getMethods();
		for (Method method : methods) {
			if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
				return method;
			}
		}
		return null;
	}
	
	/**  
	 * 将中划线形式的属性名（例如 "initial-size"）转换为驼峰命名（例如 "initialSize"） 
	 * @param propertyName 中划线形式的属性名  
	 * @return 驼峰命名形式的属性名  
	 */  
	private static String convertToCamelCase(String propertyName) {  
	    StringBuilder result = new StringBuilder();  
	    boolean toUpperCase = false; // 标记下一个字符是否需要大写  
	    for (char c : propertyName.toCharArray()) {  
	    	if (c == '-' || c == '_') {  
	            toUpperCase = true; // 遇到中划线，标记下一个字符需要大写  
	        } else {  
	            result.append(toUpperCase ? Character.toUpperCase(c) : c);  
	            toUpperCase = false; // 重置标记  
	        }  
	    }  
	    return result.toString();  
	}  

}
