package com.boot.dao.config;

import java.util.Stack;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.boot.dao.api.TransactionalMore;
import com.boot.dao.util.ApplicationContextUtil;

/**
 * 同一个事务中包含多个数据源时，利用自定义切面注解实现事务整合
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.4
 */
@Aspect
@Configuration
public class BaseTransactionAopConfig {

	@Pointcut("@annotation(com.boot.dao.api.TransactionalMore)")
	public void TransactionalMore() {}

	@Around(value = "TransactionalMore()&&@annotation(annotation)")
	public Object twiceAsOld(ProceedingJoinPoint thisJoinPoint, TransactionalMore annotation) throws Throwable {
		Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack = new Stack<DataSourceTransactionManager>();
		Stack<TransactionStatus> transactionStatuStack = new Stack<TransactionStatus>();
		try {
			if (!openTransaction(dataSourceTransactionManagerStack, transactionStatuStack, annotation)) {
				return null;
			}
			Object ret = thisJoinPoint.proceed();
			commit(dataSourceTransactionManagerStack, transactionStatuStack);
			return ret;
		} catch (Throwable e) {
			rollback(dataSourceTransactionManagerStack, transactionStatuStack);
			//log.error(String.format("MultiTransactionalAspect, method:%s-%s occors error:", thisJoinPoint.getTarget().getClass().getSimpleName(), thisJoinPoint.getSignature().getName()), e);
			throw e;
		}
	}

	// 开启事务处理方法
	private boolean openTransaction(Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack,
			Stack<TransactionStatus> transactionStatuStack, TransactionalMore transactionalMore) {
		String[] transactionMangerNames = transactionalMore.value();
		if (transactionalMore.value().length == 0) {
			return false;
		}

		for (String beanName : transactionMangerNames) {
			DataSourceTransactionManager dataSourceTransactionManager = ApplicationContextUtil.getBean(beanName);
			TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(new DefaultTransactionDefinition());
			transactionStatuStack.push(transactionStatus);
			dataSourceTransactionManagerStack.push(dataSourceTransactionManager);
		}
		return true;
	}

	// 提交处理方法
	private void commit(Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack,
			Stack<TransactionStatus> transactionStatuStack) {
		while (!dataSourceTransactionManagerStack.isEmpty()) {
			dataSourceTransactionManagerStack.pop().commit(transactionStatuStack.pop());
		}
	}

	// 回滚处理方法
	private void rollback(Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack,
			Stack<TransactionStatus> transactionStatuStack) {
		while (!dataSourceTransactionManagerStack.isEmpty()) {
			dataSourceTransactionManagerStack.pop().rollback(transactionStatuStack.pop());
		}
	}

}
