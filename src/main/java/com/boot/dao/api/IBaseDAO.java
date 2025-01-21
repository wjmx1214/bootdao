package com.boot.dao.api;

/**
 * 数据访问封装接口, 泛型函数式API, 使用时无需定义DAO层<br>
 * 注入方式示例:<br>
 * ;@Autowired // mysql数据源<br>
 * private IBaseDAO mysql; // 主数据源可省略注入名称<br>
 * 或<br>
 * ;@Autowired // clickhouse数据源<br>
 * ;@Qualifier("clickhouse") <br>
 * private IBaseDAO clickhouse;<br>
 * 
 * @author 2020-12-01 create wang.jia.le
 * @version 1.2.0
 */
public interface IBaseDAO extends IBaseEntityDAO{}
