package com.boot.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Import;

import com.boot.dao.config.BaseDAOConfig;
import com.boot.dao.config.BaseSourceMoreConfig;
import com.boot.dao.config.BaseTransactionAopConfig;
import com.boot.dao.util.ApplicationContextUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据源类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.2.0
 */
@Slf4j
//装配基础配置类,spring上下文工具类,多数据源配置类,多数据源事务整合配置类
@Import({BaseDAOConfig.class, ApplicationContextUtil.class, BaseSourceMoreConfig.class, BaseTransactionAopConfig.class})
abstract class BaseSource{

	private String dataSourceName;
	
	private DataSource dataSource;
	
	protected BaseSourceType sourceType = BaseSourceType.mysql;
	
	public void setDataSource(String dataSourceName, DataSource dataSource) throws SQLException {
		if(this.dataSource == null) {
			this.dataSourceName = dataSourceName;
			this.dataSource = dataSource;
			Connection conn = org.springframework.jdbc.datasource.DataSourceUtils.getConnection(dataSource);
			String url = conn.getMetaData().getURL();
			if(url.indexOf(":mysql:") > -1) {
				sourceType = BaseSourceType.mysql;
			}else if(url.indexOf(":oracle:") > -1) {
				sourceType = BaseSourceType.oracle;
			}else if(url.indexOf(":sqlserver:") > -1) {
				sourceType = BaseSourceType.sqlserver;
			}else if(url.indexOf(":clickhouse:") > -1) {
				sourceType = BaseSourceType.clickhouse;
			}
			org.springframework.jdbc.datasource.DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}
	
	/**
	 * 获取当前DAO所使用的数据源
	 * @return DataSource
	 * @throws SQLException
	 */
	protected DataSource getDataSource() {
		return dataSource;
	}
	
	/**
	 * 获取当前线程的DAO所使用的Connection连接
	 * @return Connection
	 * @throws SQLException
	 */
	protected Connection getConnection() throws SQLException {
		Connection conn = org.springframework.jdbc.datasource.DataSourceUtils.getConnection(this.dataSource);
		if(BaseDAOConfig.showSource) {
			log.info(new StringBuffer("\n\n----- 当前DAO类型: ").append(this.getClass().getName())
					.append("; 数据源(").append(sourceType.toString()).append("): ").append(dataSource.getClass().getName() + "@" + Integer.toHexString(dataSource.hashCode()))
					.append("; 数据源名称：").append(dataSourceName).append(" -----\n").append("----- 数据源详情: \n").append(dataSource)
					.append("\n----- 当前Connection：").append(conn).append(" -----\n").toString());
		}
		return conn;
	}

}
