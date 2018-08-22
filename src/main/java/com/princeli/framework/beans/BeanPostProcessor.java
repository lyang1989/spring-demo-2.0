package com.princeli.framework.beans;

/**
 * @program: spring-demo-2.0
 * @description: 用户事件监听
 * @author: ly
 * @create: 2018-08-16 16:16
 **/
public class BeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean,String beanName){
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean,String beanName){
        return bean;
    }

}
