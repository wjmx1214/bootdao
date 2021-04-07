package com.boot.dao.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

/**
 * 同一个事务中包含多个数据源时，使用该注解<br>
 * 可省略rollbackFor，已默认捕获Throwable异常
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.4
 */
@Retention(RetentionPolicy.RUNTIME) 			// 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.METHOD}) 					// 定义注解的作用目标(类，常量，字段，方法等)
@Documented 									// 表示该注解将被包含在javadoc中
public @interface TransactionalMore {
	
	@AliasFor("transactionManager")
	String[] value() default {"transactionManager"};
	
	@AliasFor("value")
	String[] transactionManager() default {"transactionManager"};
	
	String[] label() default {};
	
	Propagation propagation() default Propagation.REQUIRED;
	
	Isolation isolation() default Isolation.DEFAULT;
	
	int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;
	
	String timeoutString() default "";
	
	boolean readOnly() default false;
	
	Class<? extends Throwable>[] rollbackFor() default {};
	
	String[] rollbackForClassName() default {};
	
	Class<? extends Throwable>[] noRollbackFor() default {};
	
	String[] noRollbackForClassName() default {};

}
