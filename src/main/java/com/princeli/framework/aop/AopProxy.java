package com.princeli.framework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @program: spring-demo-2.0
 * @description: ${description}
 * @author: ly
 * @create: 2018-08-23 14:31
 **/
public class AopProxy implements InvocationHandler {

    private AopConfig config;

    private Object target;

    public void setConfig(AopConfig config) {
        this.config = config;
    }

    public Object getProxy(Object instance){
        this.target = instance;
        Class<?> clazz = instance.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(),clazz.getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //拿到原始对象方法
        Method m = this.target.getClass().getMethod(method.getName(),method.getParameterTypes());

        //在方法调用前执行
        if(config.contains(m)){
            AopConfig.Aspect aspect = config.get(m);
            aspect.getPoints()[0].invoke(aspect.getAspect());
        }

        //反射调用原始方法
        Object obj = method.invoke(this.target,args);

        //在方法调用前执行
        if(config.contains(m)){
            AopConfig.Aspect aspect = config.get(m);
            aspect.getPoints()[1].invoke(aspect.getAspect());
        }

        return obj;
    }
}
