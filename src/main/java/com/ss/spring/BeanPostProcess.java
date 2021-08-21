package com.ss.spring;

/**
 * @author JDsen99
 * @description
 * @createDate 2021/8/10-15:44
 */
public interface BeanPostProcess {

    Object postProcessBeforeInitialization(Object bean,String beanName);

    Object postProcessAfterInitialization(Object bean,String beanName);
}
