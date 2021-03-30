package com.boot.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.boot.dao.config.BaseDAOConfig;
import com.boot.dao.mapping.BaseColumnMapping;
import com.boot.dao.mapping.BaseTableMapping;
import com.boot.dao.util.BaseDAOLog;
import com.boot.dao.util.BaseDAOUtil;

/**
 * 查询封装类
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.3
 */
class BaseJDBCQuery {

	PreparedStatement ps;

	ResultSet rs;

	int columnCount = 0;
	
	List<BaseColumnMapping> resultColumns;
	
	/*
	 * 执行
	 * @param conn
	 * @param sql
	 * @param params
	 * @throws Exception
	 */
	ResultSet query(Connection conn, String sql, Object... params) throws Exception {
		BaseDAOLog.printSQLAndParam(BaseDAOConfig.showSQL, BaseDAOConfig.showParam, sql, params);
		this.ps = conn.prepareStatement(sql);
		BaseDAOUtil.setParams(ps, params); 	//设置参数
		this.rs = ps.executeQuery(); 		//获取结果集
		this.columnCount = rs.getMetaData().getColumnCount();
		return this.rs;
	}
	
	/*
	 * 执行
	 * @param tm
	 * @param conn
	 * @param sql
	 * @param params
	 * @throws Exception
	 */
	ResultSet query(BaseTableMapping tm, Connection conn, String sql, Object... params) throws Exception {
		BaseDAOLog.printSQLAndParam(BaseDAOConfig.showSQL, BaseDAOConfig.showParam, sql, params);
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
	 * 关闭资源(由于涉及事务问题，Connection资源交由spring处理)
	 */
	void close(){
		try {
			if(rs != null) rs.close();
			if(ps != null) ps.close();
			if(resultColumns != null) resultColumns = null;
		} catch (Exception e) {
			BaseDAOLog.printException(e);
		}
	}

}
