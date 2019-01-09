package com.xshadow.catspringbootdemo.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by IntelliJ IDEA.
 *
 * @Description:
 * @Author: 绝影
 * @Created: 2019/1/5 6:07 PM
 */
@EnableTransactionManagement
@Configuration
@MapperScan(basePackages={"com.xshadow.catspringbootdemo.dao.mapper"}, sqlSessionFactoryRef="sqlSessionFactoryBean")
public class DBConfig {

  @Value("${hikari.jdbcUrl}")
  private String jdbcUrl;

  @Value("${hikari.userName}")
  private String userName;

  @Value("${hikari.passWord}")
  private String passWord;

  @Value("${hikari.poolSize}")
  private int poolSize;


  @Bean(name="DataSource")
  @Primary
  public DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(userName);
    config.setPassword(passWord);
    config.setDriverClassName("com.mysql.jdbc.Driver");
    config.setMaximumPoolSize(poolSize);
    config.setConnectionInitSql("SELECT 1");
    config.addDataSourceProperty("useUnicode", "true");
    config.addDataSourceProperty("characterEncoding", "utf8");
    config.setPoolName("masterPool");
    config.setAutoCommit(true);
    config.setMinimumIdle(5);
    return new HikariDataSource(config);
  }


  @Bean(name = "sqlSessionFactoryBean")
  @Primary
  public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(dataSource());
    sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource[] resources = resolver.getResources("classpath:mybatis/mappers/*.xml");
    sqlSessionFactoryBean.setMapperLocations(resources);
    return sqlSessionFactoryBean.getObject();
  }

  @Bean(name = "transactionManager")
  @Primary
  public PlatformTransactionManager annotationDrivenTransactionManager() {
    return new DataSourceTransactionManager(dataSource());
  }
}
