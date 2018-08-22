package com.princeli.framework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

/**
 * @program: spring-demo-2.0
 * @description: ${description}
 * @author: ly
 * @create: 2018-08-20 14:14
 **/
public class HandlerAdapter {

    private Map<String,Integer> paramMapping;

    public HandlerAdapter(Map<String, Integer> paramMapping) {
        this.paramMapping = paramMapping;
    }

    /**
     *
     * @param req
     * @param resp  为了将其赋值给方法的参数
     * @param handler 包含了controller和method，url
     * @return
     */
    public ModelAndView handle(HttpServletRequest req, HttpServletResponse resp, HandlerMapping handler) throws InvocationTargetException, IllegalAccessException {
        //用户请求的参数信息要和method的参数信息动态匹配

        //1.准备好这个方法的形式参数列表
        Class<?>[] paramTypes = handler.getMethod().getParameterTypes();

        //2.拿到自定义命名参数所在位置
        //用户传过来的参数列表
        Map<String,String[]> reqParameterMap  =  req.getParameterMap();

        //3.构造实参列表
        Object [] paramValues = new Object[paramTypes.length];
        for (Map.Entry<String,String[]> param:reqParameterMap.entrySet()){
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]","").replaceAll("\\s","");

            if(!this.paramMapping.containsKey(param.getKey())){
                continue;
            }

            int index = this.paramMapping.get(param.getKey());

            //
            paramValues[index] = caseStringValue(value,paramTypes[index]);

        }

        if (this.paramMapping.containsKey(HttpServletRequest.class.getName())){
            int reqIndex = this.paramMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if (this.paramMapping.containsKey(HttpServletResponse.class.getName())){
            int respIndex = this.paramMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }


        //4.从handler中取出controller,method然后反射调用
        Object result = handler.getMethod().invoke(handler.getController(),paramValues);
        if (result ==null){
            return null;
        }

        boolean isModelAndView = handler.getMethod().getReturnType() == ModelAndView.class;
        if (isModelAndView){
            return (ModelAndView)result;
        }else {
            return null;
        }

    }



    private Object caseStringValue(String value,Class<?> clazz){
        if(clazz == String.class){
            return value;
        }else if (clazz == Integer.class){
            return Integer.valueOf(value);
        }else if (clazz == int.class){
            return Integer.valueOf(value).intValue();
        }else {

        }
        return null;
    }
}
