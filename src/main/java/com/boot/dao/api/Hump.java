package com.boot.dao.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
	是否开启驼峰映射<br>
	字段配置优先，类其次<br>
	@author 2020-12-01 create wang.jia.le
	@version 1.1.7
**/
@Retention(RetentionPolicy.RUNTIME) 			// 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.FIELD, ElementType.TYPE}) 	// 定义注解的作用目标(类，常量，字段，方法等)
@Documented 									// 表示该注解将被包含在javadoc中
public @interface Hump {

	/**
	 * 是否开启驼峰转换(true=开启)<br>
	 * @return boolean
	 */
	@AliasFor("value")
	boolean isHump() default true;
	
	/**
	 * 是否开启驼峰转换(true=开启)<br>
	 * @return boolean
	 */
	@AliasFor("isHump")
	boolean value() default true;

}
