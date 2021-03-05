package com.boot.dao.api;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
	[全名版]
	多条件动态查询注解, 作用于search类字段上, 用于自动封装查询条件, 简化开发
	例：<pre>
	public class StuSearch extends PageSearch{ //BaseSearch

		private Long id;

		+@SearchMeta(columnName="stu_name", searchType=SearchType.like_right)
		private String name;

		+@SearchMeta(columnName="stu_name", searchType=SearchType.like_all, whereIndex=2, tableLabel="s")
		private String name2;

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
	@version 1.0.1
**/
@Retention(RetentionPolicy.RUNTIME) 			// 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target(ElementType.FIELD) 						// 定义注解的作用目标(类，常量，字段，方法等)
@Documented 									// 表示该注解将被包含在javadoc中
public @interface SearchMeta {
	
    /**
     * 查询方式(默认equal)
     * @return SearchType
     */
	SearchType searchType() default SearchType.equal;
	
	/**
	 * 表别名
	 * @return String
	 */
	String tableLabel() default "";
	
	/**
	 * 列名或列别名
	 * @return String
	 */
	String columnName() default "";
	
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
	int whereIndex() default 1;

}
