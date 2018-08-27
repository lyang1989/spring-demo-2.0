package com.princeli.framework.aop;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @program: spring-demo-2.0
 * @description: ${description}
 * @author: ly
 * @create: 2018-08-27 15:47
 **/
public class AopProxyUtils {

    public static Object getTargetObject(Object proxy) throws Exception {
        if (!isAopProxy(proxy)){
            return proxy;
        }
        return getProxyTargetObject(proxy);
    }

    private static boolean isAopProxy(Object object){
        return Proxy.isProxyClass(object.getClass());
    }

    /**
     * 获取代理对象的原始对象
     * @param proxy
     * @return
     * @throws Exception
     */
    private static Object getProxyTargetObject(Object proxy) throws Exception{
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy)h.get(proxy);
        Field target = aopProxy.getClass().getDeclaredField("target");
        target.setAccessible(true);
        return target.get(aopProxy);
    }
}
