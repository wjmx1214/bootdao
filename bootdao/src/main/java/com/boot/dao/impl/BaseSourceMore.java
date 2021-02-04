package com.boot.dao.impl;

import org.springframework.context.annotation.Import;

/**
 * 继承此类实现(自定义名称)多数据源(实现方式请参考构造方法注释)
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.0
 */
@Import(BaseDAO.class)
public abstract class BaseSourceMore extends BaseEntityDAO{

	/**
		<b>实现类：</b>
	    <pre>
		  	@Repository
			@Import(YourSourceConfig.class)
			public class YourDAO extends BaseSourceMore{
				public YourDAO() {
					super("数据源名称");
				}
			}
		</pre>
		<b>配置类：</b>
		<pre>
		@ Configuration
		public class YourSourceConfig{
			@Bean
			@ConfigurationProperties(prefix="spring.datasource.数据源名称")
			@Value("${spring.datasource.数据源名称.type:#{null}}")
			public DataSource 数据源名称(String type) {
				if(type != null && "com.alibaba.druid.pool.DruidDataSource".equals(type)) {
					return com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder.create().build(); //阿里巴巴连接池druid创建方式
				}
			    return DataSourceBuilder.create().build(); //Spring默认支持的连接池创建方式(未配置type时为hikariCP)
			}
			
			@Bean //此名称即为@Transactional(value="事务管理器名称", rollbackFor=Exception.class)
			@ConditionalOnBean(name="数据源名称")
		    public DataSourceTransactionManager 事务管理器名称(@Qualifier("数据源名称") DataSource datasource) {
		    	return new org.springframework.jdbc.datasource.DataSourceTransactionManager(datasource);
		    }
		    
		    //@Bean
		    //copy more...
		 }
	   	 </pre>
	 */
	public BaseSourceMore(String dataSourceName) {
		super(dataSourceName);
	}

}
