package com.xshadow.catspringbootdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;



//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@SpringBootApplication
public class CatSpringBootDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(CatSpringBootDemoApplication.class, args);
  }

}

