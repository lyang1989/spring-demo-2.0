package com.princeli.demo.mvc.aspect;

/**
 * @program: spring-demo-2.0
 * @description: ${description}
 * @author: ly
 * @create: 2018-08-23 14:59
 **/
public class LogAspect {

    /**
     * 调用切面方法前执行
     */
    public void before(){
        System.out.println("invoker before method");
    }


    /**
     * 调用切面方法后执行
     */
    public void after(){
        System.out.println("invoker after method");
    }

}
