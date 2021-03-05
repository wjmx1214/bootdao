package com.boot.dao.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.boot.dao.api.IBaseDAO1;

/**
 * 数据源datasource1对应的DAO
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.1
 */
@Repository
@ConditionalOnProperty(prefix="spring.datasource.datasource1", name="username") //配置了才注册
public class BaseDAO1 extends BaseEntityDAO implements IBaseDAO1{
	
	public BaseDAO1() {
		super("datasource1");
	}

}
