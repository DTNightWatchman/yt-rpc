package com.yt.example.consumer;

import com.yt.example.common.model.User;
import com.yt.example.common.service.UserService;
import com.yt.ytrpc.proxy.ServiceProxyFactory;

/**
 * @author by Ricardo
 * @Classname EasyConsumerExample
 * @Description 简单的消费者样例
 * @Date 2024/6/27 17:11
 */
public class EasyConsumerExample {

    public static void main(String[] args) {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();

        user.setName("yt");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("new user is null");
        }
    }
}
