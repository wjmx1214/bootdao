package com.boot.dao.impl;

/**
 * 数据源连接的数据库类型
 * @author 2021-08-11 create wang.jia.le
 * @version 1.1.0
 */
public enum BaseSourceType {
	
	mysql		(1, "mysql"),
	oracle		(2, "oracle"),
	sqlserver	(3, "sqlserver"),
	clickhouse	(4, "clickhouse");

	public int num;
	public String name;
	BaseSourceType(int num, String name){
		this.num = num;
		this.name = name;
	}
	
	@Override
	public String toString() {
        return name;
    }

}
