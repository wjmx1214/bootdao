package com.boot.dao.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * 获取spring上下文
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.0
 */
@Configuration
public class ApplicationContextUtil implements ApplicationContextAware{

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ApplicationContextUtil.applicationContext = applicationContext;
	}
	
    /**
     * 获取applicationContext
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过name获取Bean
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <B> B getBean(String name) {
        return (B) applicationContext.getBean(name);
    }

    /**
     * 通过class获取Bean
     * @param clz
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <B> B getBean(Class<?> clz) {
        return (B) applicationContext.getBean(clz);
    }

}
