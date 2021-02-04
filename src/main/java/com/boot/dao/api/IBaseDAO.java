package com.boot.dao.api;

/**
 * 数据访问封装接口, 函数式调用, 适合无需DAO层的简单模型(封装Entity、DTO、VO无差别调用)<br>
 * 方法的Class参数可支持Entity.class, Dto.class, Vo.class 等符合实体模型的类型<br>
 * 甚至无需任何注解标识的类型(仅支持SQL查询)<br>
 * 可支持继承此接口的实现类BaseDAO并加入@Repository注解, 继续封装独有需求的获取数据函数<br>
 * 除非有新增函数或重写函数需求, 否则不建议继承, 直接注入使用
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.0
 */
public interface IBaseDAO extends IBaseEntityDAO{}