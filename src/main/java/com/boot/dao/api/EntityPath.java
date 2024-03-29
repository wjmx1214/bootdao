package com.boot.dao.api;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
	DTO或VO类 与 对应的实体类路径注解<br>
	用于entity、dto、vo等无差别无感知调用, 优先于统一路径配置<br>
	注意：此注解定义为实体类类名或全路径(含类名)，统一配置只有包路径<br>
	例：<pre>
	+@EntityPath("Student")
	//@EntityPath("com.xxx.xxx.entity.Student")
	public class StuVo {
		private Long id;
		private String name;
	}
	</pre>
	@author 2020-12-01 create wang.jia.le
	@version 1.1.0
**/
@Retention(RetentionPolicy.RUNTIME) 			// 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.TYPE}) 					// 定义注解的作用目标(类，常量，字段，方法等)
@Documented 									// 表示该注解将被包含在javadoc中
public @interface EntityPath {  

	/**
	 * 实体类路径<br>
	 * 例：@EntityPath(Student)<br>
	 * 例：@EntityPath(com.xxx.xxx.entity.Student)<br>
	 * 注意：若未配置此注解，有可能会根据类名自动匹配上实体类<br>
	 * 若不想匹配，则配置为：@EntityPath("no entity")
	 * @return String
	 */
	 String value() default "";

}
