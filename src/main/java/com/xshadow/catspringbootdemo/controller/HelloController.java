package com.xshadow.catspringbootdemo.controller;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 *
 * @Description:
 * @Author: 绝影
 * @Created: 2019/1/5 5:14 PM
 */
@Slf4j
@RestController
public class HelloController {



  @RequestMapping("/hello")
  private String hello() {
    Transaction transaction = Cat.newTransaction("HelloController", "hello");
    try {
      transaction.setStatus(Transaction.SUCCESS);
      log.info("HelloController | hello, {Hello World}");

    } catch (Exception e){
      transaction.setStatus(e);
      Cat.logError(e);
      log.warn("err");
    } finally {
      transaction.complete();
    }
    return "Hello World!";
  }

  @RequestMapping("/hello/{id}")
  private String hello2() {
    return "Hello World 2!";
  }




}