package com.princeli.framework.beans;

/**
 * @program: spring-demo-2.0
 * @description: class配置信息包装
 * @author: ly
 * @create: 2018-08-16 09:42
 **/
public class BeanDefinition {

    private String beanClassName;

    private String factoryBeanName;

    private boolean lazyInit = false;

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
}
