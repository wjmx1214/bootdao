# 工程简介
#说明：


 <b>bootdao是基于spring-boot的持久层封装</b>

 <pre>
 简介：
  1.函数式封装，适合轻量业务以常见泛型函数方式访问数据层
  2.可与其他持久层框架并存，无依赖式兼容JPA或mybatis-plus的实体注解，仅依赖spring-boot基础包
  3.支持扩展更多函数，支持entity、dto、vo无感知无差别调用(配置好映射路径即可)
  4.支持静默多数据源，若yml或xml按特定名称配置好多数据源后，无需其他配置即可使用多数据源
  5.支持注解式多条件动态查询，参考：com.boot.dao.api.SearchMeta 或 com.boot.dao.api.Search
  6.支持SQL语句静态常量化，所以可自行实现静态SQL语句存放位置，如独立的xxx.dao文件，或可在Service层实现一个接口，用来配置SQL常量
  7.目前尚未经过大规模性能和稳定性测试，暂不支持缓存

 使用：
  1.可直接在服务层注入IBaseDAO进行泛型函数式调用, 无需定义任何业务DAO
  2.可继承BaseTDAO<Entity>进行泛型类方式调用(可指定一个带数据源的DAO来构造, 若未指定则默认为BaseDAO)
  3.可同时兼并使用mybatis、jpa或JdbcTemplate等框架

 作者：wang.jia.le	2020-12-01	若发现BUG或疑惑请至信	wjmx1214@sina.com
 </pre>

 pom：
 
	 <dependency>
        <groupId>com.bootdao</groupId>
        <artifactId>bootdao-spring-boot-starter</artifactId>
        <version>1.0.4</version>
    </dependency>

 yml配置(选配)：
<pre>
	bootdao: #关系型数据库持久层函数式封装, 多数据源配置以及更多详细说明请参考IBaseReadme.class
	    entity-paths: com.xxx.xxx.entity #实体类包路径, 用于entity、dto、vo无差别调用(可指定多个包路径用逗号分隔; 也可不配置, 由@EntityPath注解到Dto上)
	    #auto-createtime: true #当有创建时间字段时, 是否自动生成值(默认false)(根据名称createTime或createDate推理)(mysql5.x无法同时创建时间和更新时间自动配置, mysql8.x无问题)
	    #show-sql: true #是否显示SQL语句, 主要用于调试(默认=false)
	    #show-param: true #是否显示SQL参数, 主要用于调试(默认=false)
	    #show-source: true #是否显示数据源相关信息, 主要用于调试(默认=false)
	    #different-names: Dto, Vo #实体类与DTO或VO类名不相同的部分, 用于entity、dto、vo无差别调用, 可直接将其作为参数类型(可指定多个名称, 默认Dto,Vo)
</pre>

 若yml未配置或类名无法对应，但需要entity、dto、vo无差别调用时，可在Dto类中通过@EntityPath注解配置
 
	@EntityPath("com.xxx.xxx.entity.Student")
	public class StuDto {
		private Long stuId;
		//...
	}

 IBaseDAO使用示例：
```

	@Service
	@Transactional(rollbackFor=Exception.class)
	public class StuService implements IStuService{
	   @Autowired
	   private IBaseDAO baseDAO;
	   
		@Override
		public void list() throws Exception{
			String sql = "SELECT * FROM stu WHERE age > ?";
			List<StuDto> list = baseDAO.getEntitys(sql, StuDto.class, 15);
	        for(StuDto stu : list) {
	            System.out.println(stu);
	        }
		}
	}
	

 多条件动态查询示例：

	public class StuSearch extends PageSearch{ //BaseSearch
		private Long id;
		@Search(column="stu_name", type=SType.like_r)
		private String name;
		@Search(column="stu_name", type=SType.like_a, index=2, label="s")
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

# 延伸阅读

