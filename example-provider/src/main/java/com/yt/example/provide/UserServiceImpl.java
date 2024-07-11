package com.yt.example.provide;

import com.yt.example.common.model.User;

/**
 * @author by yt
 * @Classname UserServiceImpl
 * @Description 用户服务提供类
 * @Date 2024/3/12 16:47
 */
public class UserServiceImpl {


    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }

}
