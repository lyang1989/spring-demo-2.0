package com.princeli.framework.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: spring-demo-2.0
 * @description: 对aop的expresstion封装  对目标类中的方法增强
 * @author: ly
 * @create: 2018-08-23 15:05
 **/
public class AopConfig {

    /**
     * 目标对象的method作为key，需要增强的代码作为value
     */
    private Map<Method,Aspect> points = new HashMap<Method,Aspect>();

    public void put(Method target,Object aspect,Method[] points){
        this.points.put(target,new Aspect(aspect,points));
    }

    public Aspect get(Method method){
        return this.points.get(method);
    }

    public boolean contains(Method method){
        return this.points.containsKey(method);
    }

    /**
     * 增强代码封装
     */
    public class Aspect{

        /**
         * 将LogAspect赋值给它
         */
        private Object aspect;
        /**
         * 将LogAspect的before方法个after方法赋值给它
         */
        private Method[] points;


        public Aspect(Object aspect,Method[] points){
            this.aspect = aspect;
            this.points = points;
        }

        public Object getAspect() {
            return aspect;
        }

        public void setAspect(Object aspect) {
            this.aspect = aspect;
        }

        public Method[] getPoints() {
            return points;
        }

        public void setPoints(Method[] points) {
            this.points = points;
        }
    }

}
