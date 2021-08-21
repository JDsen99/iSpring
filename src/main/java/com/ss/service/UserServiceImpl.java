package com.ss.service;

import com.ss.spring.InitializingBean;
import com.ss.spring.annotations.Autowired;
import com.ss.spring.annotations.Component;
import com.ss.spring.annotations.ComponentScan;
import com.ss.spring.annotations.Scope;

import java.sql.SQLOutput;

/**
 * @author JDsen99
 * @description
 * @createDate 2021/8/9-18:03
 */
@Component(value = "userService")
//@Scope()
public class UserServiceImpl implements UserService{

    @Autowired
    private OrderService orderService;

    @Override
    public void test() {
        System.out.println("orderService");
    }


    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化");
    }
}
