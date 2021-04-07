package com.boot.dao.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Repository;

import com.boot.dao.api.IBaseDAO2;

/**
 * 数据源dataSource2对应的DAO
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.4
 */
@Repository
@ConditionalOnBean(name = "dataSource2") //配置了才注册
public class BaseDAO2 extends BaseEntityDAO implements IBaseDAO2{

	public BaseDAO2() {
		super("dataSource2", "transactionManager2");
	}

}
