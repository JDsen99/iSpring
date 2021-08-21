package com.ss.mine;


import com.ss.service.UserService;
import com.ss.service.UserServiceImpl;
import com.ss.spring.ApplicationContext;

/**
 * @author JDsen99
 * @description
 * @createDate 2021/8/9-17:56
 */
public class TestDemo {
    public static void main(String[] args) {
        ApplicationContext app = new ApplicationContext(AppConfig.class);
        UserService userService = (UserService) app.getBean("userService");
//        System.out.println(app.getBean("userService"));
//        System.out.println(app.getBean("userService"));
//        System.out.println(app.getBean("userService"));
//        userService.test();
//        BeanPost beanPost = (BeanPost) app.getBean("beanPost");
//        System.out.println(beanPost);
//        userService.test();
    }
}
