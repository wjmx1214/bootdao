package com.boot.dao.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.boot.dao.mapping.BaseMappingCache;

/**
 * bootdao基础配置
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.1
 */
@Configuration
public class BaseDAOConfig {

	public static String[] entityPaths; 	//实体类包路径, 用于entity、dto、vo无差别调用(可指定多个包路径用逗号分隔; 也可不配置由@EntityMeta注解到Dto上)
	public static String[] differentNames; 	//实体类与DTO或VO类名不相同的部分, 用于entity、dto、vo无差别调用, 可直接将其作为参数类型(可指定多个名称, 默认Dto,Vo)
	public static boolean showSQL; 			//是否显示SQL语句, 主要用于调试(默认=false)
	public static boolean showParam; 		//是否显示SQL参数, 主要用于调试(默认=false)
	public static boolean showSource; 		//是否显示数据源相关信息, 主要用于调试(默认=false)
	public static boolean autoCreateTime;	//当有创建时间字段时, 是否自动生成值(默认false)(根据名称createTime或createDate推理)(mysql5.x无法同时创建时间和更新时间自动配置, mysql8.x无问题)
	public static String formatTime = "yyyy-MM-dd HH:mm:ss"; //时间类型默认格式化(具体参考EntityMeta.formatTime描述)
	
	@Value("${bootdao.entity-paths:#{null}}")
	public void entityPaths(String[] entityPaths) {
		BaseDAOConfig.entityPaths = entityPaths;
	}
	
	@Value("${bootdao.different-names:Dto,Vo}")
	public void differentNames(String[] differentNames) {
		BaseDAOConfig.differentNames = differentNames;
	}
	
	@Value("${bootdao.show-sql:false}")
	public void showSQL(boolean showSQL) {
		BaseDAOConfig.showSQL = showSQL;
	}
	
	@Value("${bootdao.show-param:false}")
	public void showParam(boolean showParam) {
		BaseDAOConfig.showParam = showParam;
	}
	
	@Value("${bootdao.show-source:false}")
	public void showSource(boolean showSource) {
		BaseDAOConfig.showSource = showSource;
	}
	
	@Value("${bootdao.auto-createtime:false}")
	public void autoCreateTime(boolean autoCreateTime) {
		BaseDAOConfig.autoCreateTime = autoCreateTime;
	}

	// 根据配置扫描实体类，若未配置，则在运行时生成映射
	// 扫描只加载配置了实体注解的映射模型(包含子包)
	@PostConstruct
	public void init() throws Exception {
		BaseMappingCache.scan();
	}
	
}
