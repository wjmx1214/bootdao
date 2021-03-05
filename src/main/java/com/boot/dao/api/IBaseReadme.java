package com.boot.dao.api;

/**
 * 使用示例与详细描述请查看readme()
 * @author 2020-12-01 create wang.jia.le	email	wjmx1214@sina.com
 * @version 1.0.1
 */
public interface IBaseReadme {

/**	
<pre> 
	说明：
	bootdao是基于spring-boot的持久层封装
	
	优点：函数式封装，适合简单轻量业务以常见函数方式访问数据层
		 可与其他持久层框架并存，无依赖式兼容JPA或mybatis-plus的实体注解，仅依赖spring-boot基础包
		 支持扩展更多函数，支持entity、dto、vo无感知无差别调用(配置好映射路径即可)
		 支持注解式多条件动态查询，参考：com.boot.dao.api.SearchMeta 或 com.boot.dao.api.Search
		 若yml或xml按特定名称配置好多数据源后，无需其他配置即可使用多数据源
	
	缺点：未经过大规模性能和稳定性测试，没有数据缓存功能，尚未支持模板SQL(暂可在Service层实现一个接口,用于配置SQL常量)
	
	场景：业务简单但SQL语句较多时，可继承BaseTDAO(可指定一个带数据源的DAO来构造, 若未指定则默认为BaseDAO)
		 业务更简单时，则直接在服务层注入IBaseDAO进行泛型函数式调用, 无需定义任何业务DAO
		 业务复杂或对性能要求较高时可同时兼并使用mybatis或jpa等框架
		 
	作者：wang.jia.le		若发现BUG或疑惑请至信	wjmx1214@sina.com

	pom：
	 <dependency>
	    <groupId>com.bootdao</groupId>
	    <artifactId>bootdao-spring-boot-starter</artifactId>
	    <version>1.0.1</version>
	</dependency>


	yml配置(选配)： 
		#关系型数据库持久层函数式封装; 如需使用多数据源, 使用限定数据源名称即可
		#将数据源名称配置为: spring.datasource[datasource0 && (datasource1 || datasource2)]
		#其中数据源名称 datasource0 必须存在, 且会自动转换为 dataSource 以便默认兼容其他框架
		#使用时分别对应: [IBaseDAO && (IBaseDAO1 || IBaseDAO2)]
		#若不使用默认实现或默认实现不够用, 则继承 @Repository pulibc YourNameDAO extends BaseSourceMore{}
		#注意: 多数据源时若使用了druid连接池, 请在启动类上加入@Import(com.boot.dao.config.BaseSourceMoreConfig.class)
		#或添加包扫描com.boot.dao.config, 以便优先于druid自动配置加载

		bootdao: #关系型数据库持久层函数式封装, 多数据源配置以及更多详细说明请参考IBaseReadme.class
		    entity-paths: com.xxx.xxx.entity #实体类包路径, 用于entity、dto、vo无差别调用(可指定多个包路径用逗号分隔; 也可不配置, 由@EntityPath注解到Dto上)
		    #auto-createtime: true #当有创建时间字段时, 是否自动生成值(默认false)(根据名称createTime或createDate推理)(mysql5.x无法同时创建时间和更新时间自动配置, mysql8.x无问题)
		    #show-sql: true #是否显示SQL语句, 主要用于调试(默认=false)
		    #show-param: true #是否显示SQL参数, 主要用于调试(默认=false)
		    #show-source: true #是否显示数据源相关信息, 主要用于调试(默认=false)
		    #different-names: Dto, Vo #实体类与DTO或VO类名不相同的部分, 用于entity、dto、vo无差别调用, 可直接将其作为参数类型(可指定多个名称, 默认Dto,Vo)



	若yml未配置或类名无法对应，但需要entity、dto、vo无差别调用时，可在Dto类中通过@EntityPath注解配置
	+@EntityPath("com.xxx.xxx.entity.Student")
	public class StuDto {
	    private Long stuId;
	    //...
	}

	IBaseDAO使用示例：
	+@Service
	+@Transactional(rollbackFor=Exception.class)
	public class StuService implements IStuService{
	    +@Autowired
	    private IBaseDAO baseDAO;
	    
	    +@Override
	    public void list() throws Exception{
	        String sql = "SELECT * FROM stu WHERE age > ?";
	        List<StuDto> list = baseDAO.getEntitys(sql, StuDto.class, 15);
	        for(StuDto stu : list) {
	            System.out.println(stu);
	        }
	    }
	}
	
	分离SQL示例：
	+@Service
	+@Transactional(rollbackFor=Exception.class)
	public class StuService implements IStuService, StuServiceSQL{
	    +@Autowired
	    private IBaseDAO baseDAO;
	    
	    +@Override
	    public void stuList() throws Exception{
	        List<StuDto> list = baseDAO.getEntitys(stuList_sql1, StuDto.class, 15);
	        for(StuDto stu : list) {
	            System.out.println(stu);
	        }
	        //... stuList_sql2
	    }
	    
	    +@Override
	    public void getStuByName(String name) throws Exception{
	    	//...getStuByName_sql
	    }
	}
	public interface StuServiceSQL{
		String stuList_sql1 = "SELECT * FROM stu WHERE age >= ?";
		String stuList_sql2 = "SELECT * FROM stu WHERE age < ?";
		String getStuByName_sql = "SELECT * FROM stu WHERE name = ?";
		//...
	}
	
	多条件动态查询示例：
	public class StuSearch extends PageSearch{ //BaseSearch
		private Long id;
		+@Search(column="stu_name", type=SType.like_r)
		private String name;
		+@Search(column="stu_name", type=SType.like_a, index=2, label="s")
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
*/
void readme();

}
