package com.boot.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Import;

import com.boot.dao.config.BaseDAOConfig;
import com.boot.dao.config.BaseSourceMoreConfig;
import com.boot.dao.util.ApplicationContextUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据源类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.1
 */
@Slf4j
@Import({BaseDAOConfig.class, ApplicationContextUtil.class, BaseSourceMoreConfig.class}) //装配基础配置类,spring上下文工具类,多数据源配置类
abstract class BaseSource{

	private String dataSourceName;

	private DataSource dataSource;
	
	public BaseSource(String dataSourceName) {
		this.dataSourceName = dataSourceName;
		if("dataSource".equals(dataSourceName) || "datasource0".equals(dataSourceName)) {
			log.info("已启用数据源:"+dataSourceName+"(默认)\t调用请注入对应接口:com.boot.dao.api.I"+this.getClass().getSimpleName());
		}else if("datasource1".equals(dataSourceName) || "datasource2".equals(dataSourceName)) {
			log.info("已启用数据源:"+dataSourceName+"\t\t调用请注入对应接口:com.boot.dao.api.I"+this.getClass().getSimpleName());
		}else {
			log.info("已启用数据源:"+dataSourceName+"\t\t调用请注入对应DAO(或接口):"+this.getClass().getName());
		}
	}

	/**
	 * 获取当前DAO所使用的数据源
	 * @return DataSource
	 * @throws SQLException
	 */
	protected DataSource getDataSource() throws SQLException{
		if(dataSource == null)
			dataSource = ApplicationContextUtil.getBean(dataSourceName);
		return dataSource;
	}
	
	/**
	 * 获取当前DAO所使用的Connection连接
	 * @return Connection
	 * @throws SQLException
	 */
	protected Connection getConnection() throws SQLException {
		Connection con = org.springframework.jdbc.datasource.DataSourceUtils.getConnection(this.getDataSource());
		if(BaseDAOConfig.showSource) {
			log.info(new StringBuffer("\n\n----- 当前DAO类型: ").append(this.getClass().getName())
					.append("; 数据源: ").append(dataSource.getClass().getName() + "@" + Integer.toHexString(dataSource.hashCode()))
					.append("; 数据源名称：").append(dataSourceName).append(" -----\n").append("----- 数据源详情: \n").append(dataSource)
					.append("\n----- 当前Connection：").append(con).append(" -----\n").toString());
		}
		return con;
	}

}
