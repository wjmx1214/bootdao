package com.boot.dao.config;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.boot.dao.util.BaseDAOLog;

/**
 * 多数据源默认配置类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.6
 */
@Configuration
public class BaseSourceMoreConfig {

	private static String druidType = "com.alibaba.druid.pool.DruidDataSource";
	private static Method createMethod;
	private static Method buildMethod;

	static {
		try {
			Class<?> druidDataSourceBuilderClass = Thread.currentThread().getContextClassLoader().loadClass("com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder");
			createMethod = druidDataSourceBuilderClass.getDeclaredMethod("create");
			buildMethod = druidDataSourceBuilderClass.getDeclaredMethod("build");
		} catch (Exception e) {}
	}
	
    private DataSource createDataSource(String type) {
    	DataSource dataSource = null;
    	if(type != null && druidType.equals(type)) {
    		if(buildMethod != null) {
				try {dataSource = (DataSource)buildMethod.invoke(createMethod.invoke(null)); //阿里巴巴连接池druid创建方式
				} catch (Exception e) {BaseDAOLog.printException(e);} 
			}else {
				BaseDAOLog.printException(new ClassNotFoundException("not found " + druidType));
			}
    		//dataSource = com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder.create().build(); //阿里巴巴连接池druid创建方式
    	}else {
    		dataSource = DataSourceBuilder.create().build(); //Spring默认使用的连接池创建方式(未配置type时为hikariCP)
    	}
    	return dataSource;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "dataSource")
    @ConditionalOnProperty(prefix="spring.datasource", name="username")
    @ConfigurationProperties(prefix="spring.datasource")
    @Value("${spring.datasource.type:#{null}}")
    public DataSource dataSource(String type) {
    	return createDataSource(type);
    }
	
    @Bean
    @ConditionalOnProperty(prefix="spring.datasource1", name="username")
    @ConfigurationProperties(prefix="spring.datasource1")
    @Value("${spring.datasource1.type:#{null}}")
    public DataSource dataSource1(String type) {
    	return createDataSource(type);
    }

    @Bean
    @ConditionalOnProperty(prefix="spring.datasource2", name="username")
    @ConfigurationProperties(prefix="spring.datasource2")
    @Value("${spring.datasource2.type:#{null}}")
    public DataSource dataSource2(String type) {
    	return createDataSource(type);
    }
    
    //----------------------------------------------druid-----------------------------------------------------
    
    @Bean(name = "dataSource")
    @Primary
    @ConditionalOnMissingBean(name = "dataSource")
    @ConditionalOnProperty(prefix="spring.datasource.druid", name="username")
    @ConfigurationProperties(prefix="spring.datasource.druid")
    public DataSource druidDataSource() {
    	return createDataSource(druidType);
    }
	
    @Bean(name = "dataSource1")
    @ConditionalOnProperty(prefix="spring.datasource1.druid", name="username")
    @ConfigurationProperties(prefix="spring.datasource1.druid")
    public DataSource druidDataSource1() {
    	return createDataSource(druidType);
    }

    @Bean(name = "dataSource2")
    @ConditionalOnProperty(prefix="spring.datasource2.druid", name="username")
    @ConfigurationProperties(prefix="spring.datasource2.druid")
    public DataSource druidDataSource2() {
    	return createDataSource(druidType);
    }
    
    //----------------------------------------------配置事务管理器---------------------------------------------
 
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "transactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
    	return new DataSourceTransactionManager(dataSource);
    }
    
    @Bean
    @ConditionalOnBean(name="dataSource1")
    public DataSourceTransactionManager transactionManager1(@Qualifier("dataSource1") DataSource dataSource1) {
    	return new DataSourceTransactionManager(dataSource1);
    }

    @Bean
    @ConditionalOnBean(name="dataSource2")
    public DataSourceTransactionManager transactionManager2(@Qualifier("dataSource2") DataSource dataSource2) {
    	return new DataSourceTransactionManager(dataSource2);
    }

}
