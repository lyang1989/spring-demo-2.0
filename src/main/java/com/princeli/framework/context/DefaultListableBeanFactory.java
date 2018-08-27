package com.princeli.framework.context;

import com.princeli.framework.beans.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: spring-demo-2.0
 * @description: ${description}
 * @author: ly
 * @create: 2018-08-23 14:21
 **/
public class DefaultListableBeanFactory extends AbstractApplicationContext{

    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    @Override
    protected void onRefresh(){

    }

    @Override
    protected void refreshBeanFactory() {

    }
}
