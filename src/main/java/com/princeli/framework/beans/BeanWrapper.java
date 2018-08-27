package com.princeli.framework.beans;

import com.princeli.framework.aop.AopConfig;
import com.princeli.framework.aop.AopProxy;
import com.princeli.framework.core.FactoryBean;

/**
 * @program: spring-demo-2.0
 * @description: ${description}
 * @author: ly
 * @create: 2018-08-16 09:42
 **/
public class BeanWrapper extends FactoryBean {

    private AopProxy aopProxy = new AopProxy();


    private BeanPostProcessor postProcessor;

    public BeanPostProcessor getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(BeanPostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }

    private Object wrapperInstance;

    //原始对象
    private Object originalInstance;


    public BeanWrapper(Object instance) {
        this.wrapperInstance = aopProxy.getProxy(instance);
        this.originalInstance = instance;
    }

    public Object getWrappedInstance(){
        return this.wrapperInstance;
    }

    public Class<?> getWrappedClass(){
        return this.wrapperInstance.getClass();
    }


    public void setAopConfig(AopConfig config){
        aopProxy.setConfig(config);
    }


    public Object getOriginalInstance() {
        return originalInstance;
    }
}
