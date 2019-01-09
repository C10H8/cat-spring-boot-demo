package com.xshadow.catspringbootdemo.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.dianping.cat.servlet.CatFilter;

/**
 * Created by IntelliJ IDEA.
 * https://github.com/dianping/cat/blob/master/integration/spring-boot/CatFilterConfigure.java
 * @Description:
 * @Author: 绝影
 * @Created: 2019/1/5 6:13 PM
 */
@Configuration
public class CatFilterConfigure {

  @Bean
  public FilterRegistrationBean catFilter() {
    FilterRegistrationBean registration = new FilterRegistrationBean();
    CatFilter filter = new CatFilter();
    registration.setFilter(filter);
    registration.addUrlPatterns("/*");
    registration.setName("cat-filter");
    registration.setOrder(1);
    return registration;
  }
}
