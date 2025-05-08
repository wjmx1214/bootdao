package com.boot.dao.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionManager;

import com.boot.dao.api.IBaseDAO;
import com.boot.dao.impl.BaseDAO;

import lombok.extern.slf4j.Slf4j;

/**
 * 多数据源默认配置类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.2.1
 */
@Slf4j
@Component
@SuppressWarnings("unchecked")
public class BaseSourceMoreConfig {

	private ApplicationContext applicationContext;

	private ConfigurableEnvironment environment;

	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext) {
		log.info("开始初始化bootdao数据源...");
		this.applicationContext = applicationContext;
		this.environment = (ConfigurableEnvironment) applicationContext.getEnvironment();
		init();
		log.info("初始化bootdao数据源完成!!!");
	}

	private void init() {
		// 获取注册器
		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
		
		// 获取spring默认数据源
		Map<String, Map<DataSource, String>> dms = new HashMap<>();
		DataSource defaultDataSource = null;
		try {
			defaultDataSource = applicationContext.getBean(DataSource.class);
		}catch (Exception e) {
			log.error("默认dataSource数据源初始化失败，请检查配置!!!", e);
			System.exit(0);
		}

		// 创建其它方式的数据源并注册到spring
		Map<String, DataSource> dataSources = DataSourceMoreCreate.createOtherDataSource(environment);

		// 获取baomidou方式的数据源
		String primaryDataSourceName = null;
		List<Object> baomidouDataSources = getBaomidouDataSource(defaultDataSource);
		if (baomidouDataSources != null) {
			primaryDataSourceName = (String) baomidouDataSources.get(0);
			dataSources.putAll((Map<String, DataSource>) baomidouDataSources.get(1));
		} else {
			primaryDataSourceName = "dataSource";
			dataSources.put(primaryDataSourceName, defaultDataSource);
			Map<DataSource, String> defaultDm = new HashMap<>();
			defaultDm.put(defaultDataSource, "transactionManager");
			dms.put(primaryDataSourceName, defaultDm);
		}
		
		// 注册数据源和事务管理器
		for (Entry<String, DataSource> en : dataSources.entrySet()) {
			String dataSourceName = en.getKey();
			DataSource dataSource = en.getValue();
			if(!"dataSource".equals(dataSourceName)) {
				registerDataSourceBean("_" + dataSourceName, dataSource, registry); // 注册数据源
				TransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
				String transactionManagerName = "transactionManager_" + dataSourceName;
		        boolean isPrimary = dataSourceName.equals(primaryDataSourceName); // 设置主数据源对应的事务管理器为 primary  
				registerTransactionManager(transactionManagerName, transactionManager, registry, isPrimary); // 注册事务管理器
				Map<DataSource, String> dm = new HashMap<>();
				dm.put(dataSource, transactionManagerName);
				dms.put(dataSourceName, dm);
			}
		}

		// 统一注册bootdao数据源
		registryBootdao(registry, dms, primaryDataSourceName);
	}

	// 统一注册bootdao数据源
	public void registryBootdao(BeanDefinitionRegistry registry, Map<String, Map<DataSource, String>> dms,
			String primaryDataSourceName) {
		for (String dataSourceName : dms.keySet()) {
			Map<DataSource, String> dm = dms.get(dataSourceName);
			dm.forEach((dataSource, transactionManagerName) -> {
				// 创建一个 GenericBeanDefinition 以定义 BaseDAO 的 Bean
				GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
				beanDefinition.setBeanClass(BaseDAO.class); // 设置 Bean 类

				// 定义 Bean 的实例化逻辑
				beanDefinition.setInstanceSupplier(() -> {
					IBaseDAO baseDAO = new BaseDAO(); // 新实例化 BaseDAO 的实现类
					try {
						baseDAO.setDataSource("_"+dataSourceName, dataSource); // 设置 DataSource
					} catch (SQLException e) {
						log.error(e.getMessage(), e);
					} 
					return baseDAO; // 返回初始化好的 BaseDAO 实例
				});
				
				if(!"dataSource".equals(dataSourceName)) {
					registry.registerBeanDefinition(dataSourceName, beanDefinition);
					log.info("已将数据源({})注册到IBaseDAO,注册名称为:{},事务管理器名称为:{}", "_"+dataSourceName, dataSourceName, transactionManagerName);
				}
				// 主数据源多注册一个别名
				if (dataSourceName.equals(primaryDataSourceName)) {
					registry.registerBeanDefinition("baseDAO", beanDefinition);
					log.info("已将主数据源({})注册到IBaseDAO,注册名称为:{},事务管理器名称为:{}", "_"+primaryDataSourceName, "baseDAO", transactionManagerName);
				}
			});
		}
	}

	// 注册数据源 Bean 到 Spring 容器
	private void registerDataSourceBean(String name, DataSource dataSource, BeanDefinitionRegistry registry) {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(dataSource.getClass());
		beanDefinition.setInstanceSupplier(() -> dataSource);

		// 注册数据源到 Spring 的 BeanDefinitionRegistry
		registry.registerBeanDefinition(name, beanDefinition);
	}

	// 注册事务管理器方法
	private void registerTransactionManager(String name, TransactionManager transactionManager,
			BeanDefinitionRegistry registry, boolean primary) {
		GenericBeanDefinition transactionManagerDefinition = new GenericBeanDefinition();
		transactionManagerDefinition.setBeanClass(transactionManager.getClass());
		transactionManagerDefinition.setInstanceSupplier(() -> transactionManager); // 直接返回实例

		if (primary) {  
			transactionManagerDefinition.setPrimary(true);  // 设置主事务管理器  
		}  
		
		// 注册事务管理器到 Spring 的 BeanDefinitionRegistry
		registry.registerBeanDefinition(name, transactionManagerDefinition);
	}

	// 获取baomidou方式的数据源
	private List<Object> getBaomidouDataSource(DataSource defaultDataSource) {
		if(defaultDataSource != null) {
			try {
				Class<?> dynamicRoutingDataSourceClass = Thread.currentThread().getContextClassLoader()
						.loadClass("com.baomidou.dynamic.datasource.DynamicRoutingDataSource");
				if (dynamicRoutingDataSourceClass != null) {
					List<Object> list = new ArrayList<>();
					Field primaryField = dynamicRoutingDataSourceClass.getDeclaredField("primary");
					if(primaryField != null) {
						primaryField.setAccessible(true); // 强制访问
						String primary = (String) primaryField.get(defaultDataSource);
						list.add(primary); // 主数据源名称
		
						Method getCurrentDataSources = dynamicRoutingDataSourceClass.getMethod("getCurrentDataSources");
						Map<String, DataSource> dataSources = (Map<String, DataSource>) getCurrentDataSources.invoke(defaultDataSource);
						list.add(dataSources); // 所有数据源
						return list;
					}
				}
			} catch (Exception e) {}
		}
		return null;
	}

}
