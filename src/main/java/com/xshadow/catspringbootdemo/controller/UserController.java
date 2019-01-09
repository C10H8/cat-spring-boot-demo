package com.xshadow.catspringbootdemo.controller;

import com.dianping.cat.Cat;
import com.xshadow.catspringbootdemo.dao.entity.UserInfo;
import com.xshadow.catspringbootdemo.dao.operate.MuserInfoService;
import com.xshadow.catspringbootdemo.utils.IdGeneratorUtil;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 *
 * @Description:
 * @Author: 绝影
 * @Created: 2019/1/9 1:25 PM
 */

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

  @Resource
  private MuserInfoService muserInfoService;

  @RequestMapping(value = "/adduser", method = RequestMethod.GET)
  public String regUser() {
    UserInfo userInfo = new UserInfo();

    Long userId = IdGeneratorUtil.getInstance().nextId();
    userInfo.setUserId(userId);
    userInfo.setMobile("123");
    userInfo.setAccount("绝影");
    userInfo.setEmail("cjl@gmail.com");
    muserInfoService.insertUserInfo(userInfo);

    // 记录一个事件
    Cat.logEvent("User", "newUserRegSuccess");

    // 录业务指标的总和或平均值

    // 测试 metric
    Cat.logMetricForCount("register");

    Cat.logMetricForCount("UserCount", 3);

    Cat.logMetricForDuration("UserCount", 3);


    return "true";
  }
}
