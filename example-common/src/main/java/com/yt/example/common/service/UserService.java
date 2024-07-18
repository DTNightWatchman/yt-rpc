package com.yt.example.common.service;

import com.yt.example.common.model.User;

/**
 * @author by yt
 * @Classname User
 * @Description TODO
 * @Date 2024/3/12 16:31
 */
public interface UserService {

    /**
     * 获取用户信息
     *
     * @param user
     * @return
     */
    User getUser(User user);


    /**
     * 新方法 - 获取数字
     */
    short getNumber();
}
