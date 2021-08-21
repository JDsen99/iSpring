package com.ss.service;

import com.ss.spring.ApplicationContext;
import com.ss.spring.BeanPostProcess;
import com.ss.spring.annotations.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Timestamp;

/**
 * @author JDsen99
 * @description
 * @createDate 2021/8/10-15:45
 */
@Component(value = "beanPost")
public class BeanPost implements BeanPostProcess {

    public BeanPost() {
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (beanName.equals("userService")) {
            System.out.println("初始化前");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (beanName.equals("userService")) {
            System.out.println("初始化后");
            Object instance = Proxy.newProxyInstance(BeanPost.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                    System.out.println("代理逻辑");
                    return method.invoke(bean, args);
                }
            });
            return instance;
        }
        return bean;
    }
}
