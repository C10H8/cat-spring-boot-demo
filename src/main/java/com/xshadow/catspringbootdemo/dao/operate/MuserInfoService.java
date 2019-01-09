package com.xshadow.catspringbootdemo.dao.operate;

import com.xshadow.catspringbootdemo.dao.entity.UserInfo;
import com.xshadow.catspringbootdemo.dao.mapper.UserInfoMapper;
import java.util.Date;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 *
 * @Description:
 * @Author: 绝影
 * @Created: 2019/1/9 1:27 PM
 */
@Service
public class MuserInfoService {
  @Resource
  private UserInfoMapper userInfoMapper;

  public boolean insertUserInfo(UserInfo userInfo) {
    userInfo.setUpdateTime(new Date());
    return userInfoMapper.insertSelective(userInfo) > 0;
  }

}
