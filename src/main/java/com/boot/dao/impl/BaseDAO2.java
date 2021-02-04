package com.boot.dao.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.boot.dao.api.IBaseDAO2;

/**
 * 数据源datasource2对应的DAO
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.0
 */
@Repository
@ConditionalOnProperty(prefix="spring.datasource.datasource2", name="username") //配置了才注册
public class BaseDAO2 extends BaseEntityDAO implements IBaseDAO2{

	public BaseDAO2() {
		super("datasource2");
	}

}
