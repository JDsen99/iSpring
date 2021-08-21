package com.ss.spring;

/**
 * @author JDsen99
 * @description
 * @createDate 2021/8/9-20:21
 */
public interface InitializingBean {

    void afterPropertiesSet() throws Exception;
}
