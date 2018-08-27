package com.princeli.framework.webmvc.servlet;

import com.princeli.demo.mvc.action.DemoAction;
import com.princeli.framework.annotation.*;
import com.princeli.framework.aop.AopProxyUtils;
import com.princeli.framework.context.ApplicationContext;
import com.princeli.framework.webmvc.HandlerAdapter;
import com.princeli.framework.webmvc.HandlerMapping;
import com.princeli.framework.webmvc.ModelAndView;
import com.princeli.framework.webmvc.ViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: spring-demo-1.0
 * @description: ${description}
 * @author: ly
 * @create: 2018-08-09 15:49
 **/
public class DispatchServlet extends HttpServlet {

    private  final String LOCATION = "contextConfigLocation";

    //private Map<String, HandlerMapping> handlerMapping = new HashMap<>();

    private List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>();

    private Map<HandlerMapping,HandlerAdapter> handlerAdapters = new HashMap<HandlerMapping,HandlerAdapter>();


    private List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req,resp);
        }catch (Exception e){
            resp.getWriter().write("<font size='25' color='blue'>500 Exception</font><br/>Details:<br/>" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]","")
                    .replaceAll("\\s","\r\n") +  "<font color='green'><i>Copyright@princeli</i></font>");
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        HandlerMapping handler = getHandler(req);
        if(handler == null){
            resp.getWriter().write("<font size='25' color='red'>404 Not Found</font><br/><font color='green'><i>Copyright@princeli</i></font>");
            return;
        }

        HandlerAdapter ha = getHandlerAdapter(handler);

        //调用方法得到返回值
        ModelAndView mv = ha.handle(req,resp,handler);


        processDispatchResult(resp,mv);

    }

    private void processDispatchResult(HttpServletResponse resp, ModelAndView mv) throws Exception {
        if (null == mv){
            return;
        }
        if (this.viewResolvers.isEmpty()){
            return;
        }

        for (ViewResolver viewResolver:this.viewResolvers){
            if (!mv.getViewName().equals(viewResolver.getViewName())){
                continue;
            }

           String out = viewResolver.viewResolver(mv);
            if (out != null){
                resp.getWriter().write(out);
                break;
            }
        }

    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()){
            return null;
        }

        return this.handlerAdapters.get(handler);
    }

    private HandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()){
            return null;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");

        for (HandlerMapping handler : this.handlerMappings){
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()){
                continue;
            }
            return handler;
        }

        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        //相当于把IOC容器初始化了
        ApplicationContext context = new ApplicationContext(config.getInitParameter(LOCATION));


        initStrategies(context);

    }

    protected void initStrategies(ApplicationContext context) {

        //文件上传解析
        initMultipartResolver(context);
        //本地化解析
        initLocaleResolver(context);
        //主题解析
        initThemeResolver(context);

        //用来保存Controller里RequestMapping和Method的对应关系
        initHandlerMappings(context);
        //用来动态匹配Method参数，类转换，动态赋值
        initHandlerAdapters(context);
        //遇到异常交给initHandlerExceptionResolvers解析
        initHandlerExceptionResolvers(context);
        //直接解析请求到视图名
        initRequestToViewNameTranslator(context);
        //通过ViewResolvers实现动态模板解析
        initViewResolvers(context);
        //Flash映射管理器
        initFlashMapManager(context);
    }

    private void initFlashMapManager(ApplicationContext context) {
    }
    private void initRequestToViewNameTranslator(ApplicationContext context) {
    }
    private void initHandlerExceptionResolvers(ApplicationContext context) {
    }
    private void initThemeResolver(ApplicationContext context) {
    }
    private void initLocaleResolver(ApplicationContext context) {
    }
    private void initMultipartResolver(ApplicationContext context) {


    }

    private void initHandlerMappings(ApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();

        try {

        for (String beanName : beanNames) {
            Object proxy = context.getBean(beanName);
            Object controller = AopProxyUtils.getTargetObject(proxy);

            Class<?> clazz = controller.getClass();
            //
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }

            String baseUrl = "";

            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }

            //扫描public方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }

                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String regex = ("/" + baseUrl + requestMapping.value().replaceAll("\\*", ".*")).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappings.add(new HandlerMapping(controller, method, pattern));
                System.out.println("Mapping: "+regex+" , "+ method);
            }

        }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initHandlerAdapters(ApplicationContext context) {
        for (HandlerMapping handlerMapping : this.handlerMappings){
            Map<String,Integer> paramMapping = new HashMap<String,Integer>();

            //controller中的方法注解的参数
            Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
            for (int i = 0;i< pa.length;i++){
                for (Annotation a : pa[i]){
                    if (a instanceof RequestParam){
                        String paramName = ((RequestParam)a).value();
                        if(!"".equals(paramName.trim())){
                            paramMapping.put(paramName,i);
                        }
                    }
                }
            }

            //controller中的方法未注解的参数
            Class<?>[] paraTypes = handlerMapping.getMethod().getParameterTypes();
            for (int i = 0;i < paraTypes.length;i++) {
                Class<?> type = paraTypes[i];
                if(type == HttpServletRequest.class || type == HttpServletResponse.class){
                    paramMapping.put(type.getName(),i);
                }
            }

            this.handlerAdapters.put(handlerMapping,new HandlerAdapter(paramMapping));
        }

    }

    private void initViewResolvers(ApplicationContext context) {
        //解决页面文件和模板名字关联问题
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File template : templateRootDir.listFiles()) {
            this.viewResolvers.add(new ViewResolver(template.getName(),template));
        }

    }

}
