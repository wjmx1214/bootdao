package com.boot.dao.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
	多条件动态查询注解, 作用于search类字段上, 用于自动封装查询条件, 简化开发<br>
	注意：若该Search类用于多表共用时，调用appendWhere函数时，请带上实体类Class过滤非当前表的列，否则会出现SQL错误
	例：<pre>
	public class StuSearch extends PageSearch{ //BaseSearch

		private Long id;

		+@Search(column="stu_name", type=SearchType.like_right)
		private String name;

		+@Search(column="stu_name", type=SearchType.like_all, tableAs="s", index=2)
		private String name2;
		
		+@Search(column="stu_age", sort=Sort.DESC)
		private Integer age;

	}

	调用示例：
	public Page<StuDto> pageStu(StuSearch search){
		search.SQL = "(select * from stu where 1=1 #{where1或任意标识}) union (select * from stu s where s.on_class=1 #{where2或任意标识})";
		return baseDAO.page(search, StuDto.class);

		or

		search.appendWhere("(select * from stu where 1=1 #{where1或任意标识}) union (select * from stu s where s.on_class=1 #{where2或任意标识})");
		return baseDAO.page(search, StuDto.class);
	}

	public List<StuDto> listStu(StuSearch search){
		search.appendWhere("select * from stu where 1=1 #{where}");
		return baseDAO.getEntitys(search.SQL, StuDto.class, search.params);

		or

		search.SQL = "select * from stu where 1=1 #{where}";
		return baseDAO.getEntitys(search.appendWhere(), StuDto.class, search.params);
	}
	</pre>
	@author 2020-12-01 create wang.jia.le
	@version 1.0.7
**/
@Retention(RetentionPolicy.RUNTIME) 			// 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target(ElementType.FIELD) 						// 定义注解的作用目标(类，常量，字段，方法等)
@Documented 									// 表示该注解将被包含在javadoc中
public @interface Search {
	
    /**
     * 查询方式(默认eq)
     * @return SearchType
     */
	@AliasFor("value")
	SearchType type() default SearchType.eq;
	
    /**
     * 查询方式(默认eq)
     * @return SearchType
     */
	@AliasFor("type")
	SearchType value() default SearchType.eq;
	
	/**
	 * 表别名
	 * @return String
	 */
	String tableAs() default "";
	
	/**
	 * 列名或列别名
	 * @return String
	 */
	String column() default "";
	
	/**
	 * 是否开启驼峰转换, 可省去配置列名(true=开启)
	 * @return boolean
	 */
	boolean isHump() default true;

	/**
	 * 条件索引(多表或子查询时, 若出现多处where或having, 则利用此索引进行区分, 按阅读顺序)<br>
	 * 默认=1, 即默认只有一处where
	 * @return int
	 */
	int index() default 1;
	
	/**
	 * 是否排序, 声明在前的属性优先加入排序规则<br>
	 * 且不影响该属性作为查询条件(默认="")
	 * @return Sort
	 */
	Sort sort() default Sort.NOT;
	
	/**
	 * 自定义条件语句，用于复杂的条件判断<br>
	 * 当该值不为空时，则直接并且只拼接该属性字段值<br>
	 * 并根据?号个数添加对应的参数，当为字符串类型时，带逗号的参数会进行分割对应<br>
	 * 非字符串类型，或逗号分隔对应不上时则作为单值进行重复对应
	 * @return String
	 */
	String whereSQL() default "";

}
