package com.boot.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.boot.dao.config.BaseDAOConfig;
import com.boot.dao.mapping.BaseColumnMapping;
import com.boot.dao.mapping.BaseTableMapping;
import com.boot.dao.util.BaseDAOLog;
import com.boot.dao.util.BaseDAOUtil;

/**
 * 查询封装类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.1.4
 */
class BaseJDBCQuery {
	
	DataSource dataSource;
	
	Connection conn;

	PreparedStatement ps;

	ResultSet rs;

	int columnCount = 0;
	
	List<BaseColumnMapping> resultColumns;
	
	/*
	 * 执行
	 * @param dataSource
	 * @param conn
	 * @param sql
	 * @param params
	 * @throws Exception
	 */
	ResultSet query(DataSource dataSource, Connection conn, String sql, Object... params) throws Exception {
		BaseDAOLog.printSQLAndParam(BaseDAOConfig.showSQL, BaseDAOConfig.showParam, sql, params);
		this.dataSource = dataSource;
		this.conn = conn;
		this.ps = conn.prepareStatement(sql);
		BaseDAOUtil.setParams(ps, params); 	//设置参数
		this.rs = ps.executeQuery(); 		//获取结果集
		this.columnCount = rs.getMetaData().getColumnCount();
		return this.rs;
	}
	
	/*
	 * 执行
	 * @param tm
	 * @param dataSource
	 * @param conn
	 * @param sql
	 * @param params
	 * @throws Exception
	 */
	ResultSet query(BaseTableMapping tm, DataSource dataSource, Connection conn, String sql, Object... params) throws Exception {
		BaseDAOLog.printSQLAndParam(BaseDAOConfig.showSQL, BaseDAOConfig.showParam, sql, params);
		this.dataSource = dataSource;
		this.conn = conn;
		this.ps = conn.prepareStatement(sql);
		BaseDAOUtil.setParams(ps, params); 	//设置参数
		this.rs = ps.executeQuery(); 		//获取结果集
		this.columnCount = rs.getMetaData().getColumnCount();
		this.resultColumns = new ArrayList<>();
		for(int i=0; i<this.columnCount; i++) {
			BaseColumnMapping cm = tm.columnMappings.get(this.rs.getMetaData().getColumnLabel(i+1).toLowerCase());
			this.resultColumns.add(cm);
		}
		return this.rs;
	}
	
	/*
	 * 关闭资源(若配置了事务，则Connection资源交由spring处理；未配置事务，则在此释放Connection)
	 */
	void close(){
		try {
			if(resultColumns != null) resultColumns = null;
			if(rs != null) rs.close();
			if(ps != null) ps.close();
			if(conn != null && !TransactionSynchronizationManager.isActualTransactionActive()) { //未配置事务则手动释放
				org.springframework.jdbc.datasource.DataSourceUtils.releaseConnection(conn, dataSource);
				if(BaseDAOConfig.showSource) {
					BaseDAOLog.info("当前查询业务未配置事务，已自动释放连接；若为多次连续的查询业务，频繁释放连接可能导致性能降低!");
				}
			}
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		}
	}

}
