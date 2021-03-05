package com.boot.dao.config;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
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
 * @version 1.0.1
 */
@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class BaseSourceMoreConfig {

	private static String druidType = "com.alibaba.druid.pool.DruidDataSource";
	private static Method createMethod;
	private static Method buildMethod;

	static {
		try {
			Class<?> druidDataSourceBuilderClass = Class.forName("com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder");
			createMethod = druidDataSourceBuilderClass.getDeclaredMethod("create");
			buildMethod = druidDataSourceBuilderClass.getDeclaredMethod("build");
		} catch (Exception e) {}
	}

    @Bean
    @Primary
    @ConditionalOnProperty(prefix="spring.datasource.datasource0", name="username")
    @ConfigurationProperties(prefix="spring.datasource.datasource0")
    @Value("${spring.datasource.datasource0.type:#{null}}")
    public DataSource dataSource(String type) {
    	return createDataSource(type);
    }
	
    @Bean
    @ConditionalOnProperty(prefix="spring.datasource.datasource1", name="username")
    @ConfigurationProperties(prefix="spring.datasource.datasource1")
    @Value("${spring.datasource.datasource1.type:#{null}}")
    public DataSource datasource1(String type) {
    	return createDataSource(type);
    }

    @Bean
    @ConditionalOnProperty(prefix="spring.datasource.datasource2", name="username")
    @ConfigurationProperties(prefix="spring.datasource.datasource2")
    @Value("${spring.datasource.datasource2.type:#{null}}")
    public DataSource datasource2(String type) {
    	return createDataSource(type);
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
    
    //-------------------------------------------------------配置事务管理器-------------------------------------------------
 
    @Bean
    @Primary
    @ConditionalOnBean(name="dataSource")
    @ConditionalOnProperty(prefix="spring.datasource.datasource0", name="username")
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
    	return new DataSourceTransactionManager(dataSource);
    }
    
    @Bean
    @ConditionalOnBean(name="datasource1")
    public DataSourceTransactionManager transactionManager1(@Qualifier("datasource1") DataSource datasource1) {
    	return new DataSourceTransactionManager(datasource1);
    }

    @Bean
    @ConditionalOnBean(name="datasource2")
    public DataSourceTransactionManager transactionManager2(@Qualifier("datasource2") DataSource datasource2) {
    	return new DataSourceTransactionManager(datasource2);
    }

}
