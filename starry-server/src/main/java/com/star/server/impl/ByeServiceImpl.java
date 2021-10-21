package com.star.server.impl;

import com.star.api.ByeService;
import com.star.framework.annotation.StarryService;

/**
 * @Author: zzStar
 * @Date: 05-29-2021 15:29
 */
@StarryService(version = "version_1.0", group = "group_1", register = false)
public class ByeServiceImpl implements ByeService {


    @Override
    public String sayBye(String name) {
        return name + "too";
    }

}
