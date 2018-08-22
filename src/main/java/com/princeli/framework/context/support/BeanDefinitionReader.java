package com.princeli.framework.context.support;

import com.princeli.framework.beans.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @program: spring-demo-2.0
 * @description:  对配置文件进行查找，读取，解析
 * @author: ly
 * @create: 2018-08-16 09:14
 **/
public class BeanDefinitionReader {

    private Properties config = new Properties();

    private List<String> registyBeanClasses = new ArrayList<String>();

    /**
     * 配置文件扫描包路径
     */
    private final String SCAN_PACKAGE = "scanPackage";

    public BeanDefinitionReader(String ... locations) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    public List<String> loadBeanDefinitions(){
        return this.registyBeanClasses;
    }

    /**
     * 每注册一个className，就返回一个BeanDefinition
     * @param className
     * @return
     */
    public BeanDefinition registerBean(String className){
        if (this.registyBeanClasses.contains(className)){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setFactoryBeanName(lowerFistCase(className.substring(className.lastIndexOf(".") + 1)));
            return beanDefinition;
        }
        return null;
    }


    public Properties getConfig(){
        return this.config;
    }


    /**
     * 递归扫描包下面所有class，保存到list中
     * @param packageName
     */
    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.","/"));
        File classDir = new File(url.getFile());


        for (File file:classDir.listFiles()) {
            if(file.isDirectory()){
                doScanner(packageName+"."+file.getName());
            }else {
                registyBeanClasses.add(packageName+"."+file.getName().replace(".class",""));
            }
        }

    }

    private String lowerFistCase(String str){
        char [] chars = str.toCharArray();
        chars[0] += 32;
        return  String.valueOf(chars);
    }

}
