package com.princeli.framework.context;

/**
 * @program: spring-demo-2.0
 * @description: ${description}
 * @author: ly
 * @create: 2018-08-23 14:16
 **/
public abstract class AbstractApplicationContext {

    //子类重写
    protected void onRefresh(){

    }

    protected abstract void refreshBeanFactory();
}
