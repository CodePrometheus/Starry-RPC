package com.star.server.impl;

import com.star.api.User;
import com.star.api.UserService;
import com.star.framework.annotation.StarryService;

/**
 * @Author: zzStar
 * @Date: 05-29-2021 15:39
 */
@StarryService(version = "version_1.0", group = "group_2")
public class UserServiceImpl2 implements UserService {

    @Override
    public User getInfo() {
        User user = new User();
        user.setName("star")
                .setAge(20)
                .setSex("handsomeBoy");
        return user;
    }

    @Override
    public boolean printInfo(User user) {
        if (user != null) {
            System.out.println(user);
            return true;
        }
        return false;
    }
}
