package com.princeli.framework.context;

import com.princeli.framework.annotation.Autowried;
import com.princeli.framework.annotation.Controller;
import com.princeli.framework.annotation.Service;
import com.princeli.framework.aop.AopConfig;
import com.princeli.framework.beans.BeanDefinition;
import com.princeli.framework.beans.BeanPostProcessor;
import com.princeli.framework.beans.BeanWrapper;
import com.princeli.framework.context.support.BeanDefinitionReader;
import com.princeli.framework.core.BeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: spring-demo-2.0
 * @description: ${description}
 * @author: ly
 * @create: 2018-08-15 11:22
 **/
public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {

    private String[] configLocations;

    private BeanDefinitionReader reader;


    private Map<String,Object> beanCacheMap = new HashMap<String, Object>();

    private Map<String,BeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, BeanWrapper>();


    public ApplicationContext(String ... configLocations){
        this.configLocations = configLocations;
        refresh();
    }

    public void refresh(){
        //定位
        this.reader = new BeanDefinitionReader(configLocations);

        //加载
        List<String> beanDefinitions = reader.loadBeanDefinitions();

        //注册
        doRegisty(beanDefinitions);

        //依赖注入
        doAutowired();
    }

    private void doAutowired() {
        for (Map.Entry<String,BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                Object obj = getBean(beanName);
                System.out.println(obj);
            }
        }


        for (Map.Entry<String,BeanWrapper> beanWrapperEntry : this.beanWrapperMap.entrySet()){
             populateBean(beanWrapperEntry.getKey(),beanWrapperEntry.getValue().getOriginalInstance());
        }

    }


    public void  populateBean(String beanName,Object instance){
        Class clazz = instance.getClass();

        if (!(clazz.isAnnotationPresent(Controller.class) ||
                clazz.isAnnotationPresent(Service.class))){
            return;
        }

        Field [] fields = clazz.getDeclaredFields();

        for (Field field : fields){
            if(!field.isAnnotationPresent(Autowried.class)){
                continue;
            }

            Autowried autowried = field.getAnnotation(Autowried.class);

            String autowriedBeanName = autowried.value().trim();

            if ("".equals(autowriedBeanName)){
                autowriedBeanName =  field.getType().getName();
            }

            field.setAccessible(true);

            try {
                field.set(instance,this.beanWrapperMap.get(autowriedBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


        }

    }

    private void doRegisty(List<String> beanDefinitions) {

        try {


            for (String className : beanDefinitions) {
                //beanName有三种情况
                Class<?> beanClass = Class.forName(className);

                //接口
                if(beanClass.isInterface()){
                    continue;
                }

                //
                BeanDefinition beanDefinition = reader.registerBean(className);

                if(beanDefinition != null){
                    this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
                }

                //如果实现类有接口，把接口也注册进去
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i:interfaces) {
                    this.beanDefinitionMap.put(i.getName(),beanDefinition);
                }

            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 。
     * @param beanName
     * @return
     */
    @Override
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        String className = beanDefinition.getBeanClassName();

        try {

            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();

            Object instance = instantionBean(beanDefinition);
            if (null == instance){
                return null;
            }

            beanPostProcessor.postProcessBeforeInitialization(instance,beanName);


            BeanWrapper beanWrapper = new BeanWrapper(instance);
            beanWrapper.setAopConfig(instantionAopConfig(beanDefinition));
            beanWrapper.setPostProcessor(beanPostProcessor);
            this.beanWrapperMap.put(beanName,beanWrapper);


            beanPostProcessor.postProcessAfterInitialization(instance,beanName);

            //populateBean(beanName,instance);


            return this.beanWrapperMap.get(beanName).getWrappedInstance();

        }catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }


    private AopConfig instantionAopConfig(BeanDefinition beanDefinition) throws Exception {
        AopConfig config = new AopConfig();
        String expression = reader.getConfig().getProperty("pointCut");
        String[] before = reader.getConfig().getProperty("aspectBefore").split("\\s");
        String[] after =  reader.getConfig().getProperty("aspectAfter").split("\\s");

        String className = beanDefinition.getBeanClassName();
        Class<?> clazz = Class.forName(className);

        Pattern pattern = Pattern.compile(expression);

        Class aspectClass = Class.forName(before[0]);
        //原生方法
        for (Method m:clazz.getMethods()){

            //public .* com\.princeli\.demo\.mvc\.service\..*Service\..*\(.*\)
            //public java.lang.String com.princeli.demo.mvc.service.impl.ModifyService.add(java.lang.String)

            Matcher matcher = pattern.matcher(m.toString());

            if (matcher.matches()){
                config.put(m,aspectClass.newInstance(),new Method[]{aspectClass.getMethod(before[1]),aspectClass.getMethod(after[1])});
            }
        }

        return config;
    }

    /**
     * 通过beanDefinition中的信息，反射创建一个实例返回
     * @param beanDefinition
     * @return
     */

    private Object instantionBean(BeanDefinition beanDefinition){
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {

            if (this.beanCacheMap.containsKey(className)){
                instance = this.beanCacheMap.get(className);
            }else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.beanCacheMap.put(className,instance);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return instance;
    }


    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }


    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }


    public Properties getConfig(){
        return reader.getConfig();
    }

}
