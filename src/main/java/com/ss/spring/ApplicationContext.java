package com.ss.spring;

import com.ss.mine.AppConfig;
import com.ss.spring.annotations.Autowired;
import com.ss.spring.annotations.Component;
import com.ss.spring.annotations.ComponentScan;
import com.ss.spring.annotations.Scope;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JDsen99
 * @description
 * @createDate 2021/8/9-17:51
 */
public class ApplicationContext {

    private ConcurrentHashMap<String,Object> iocContainer = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private List<BeanPostProcess> beanPostProcessList = new ArrayList<>();

    private Class configClass;

    public ApplicationContext(Class configClass){
        this.configClass = configClass;

        scan(configClass);

        for (Map.Entry<String,BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = createBean(beanDefinition,beanName);
                iocContainer.put(beanName,bean);
            }
        }
    }

    /**
     * 创建bean
     * @param beanDefinition
     * @return
     */
    private Object createBean(BeanDefinition beanDefinition, String beanName) {
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();

            for (BeanPostProcess beanPostProcess : beanPostProcessList) {
                beanPostProcess.postProcessBeforeInitialization(instance, beanName);
            }

            //依赖注入
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    Object bean = getBean(declaredField.getName());
                    declaredField.setAccessible(true);
                    declaredField.set(instance,bean);
                }
            }

            if (instance instanceof InitializingBean) {
                try {
                    ((InitializingBean)instance).afterPropertiesSet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (BeanPostProcess beanPostProcess : beanPostProcessList) {
                instance = beanPostProcess.postProcessAfterInitialization(instance, beanName);
            }
            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析配置类
     */
    private void scan(Class configClass) {
        ComponentScan componentScan = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScan.value();
        path = path.replace(".","/");
        //扫描
        //根据路径得到所有的类。
        //Bootstrap -->jre/lib
        //Ext--------->jre/ext/lib
        //app--------->classpath
        ClassLoader classLoader = ApplicationContext.class.getClassLoader();

        URL resource = classLoader.getResource(path);
        File file = new File(resource.getFile());

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {

                String fileName = f.getAbsolutePath();
                if (fileName.endsWith(".class")) {
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    className = className.replace("\\", ".");

                    try {

                        Class<?> clazz = classLoader.loadClass(className);

                        if (clazz.isAnnotationPresent(Component.class)) {
                            //表示当前的类是一个Bean
                            //解析类，判断当前bean是单例bean，还是prototype的bean
                            //BeanDefinition

                            if (BeanPostProcess.class.isAssignableFrom(clazz)) {
                                BeanPostProcess instance = (BeanPostProcess) clazz.getDeclaredConstructor().newInstance();
                                beanPostProcessList.add(instance);
                            }

                            Component componentAnnotation = clazz.getAnnotation(Component.class);
                            String beanName = componentAnnotation.value();

                            BeanDefinition beanDefinition = new BeanDefinition();

                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
                                beanDefinition.setScope(scopeAnnotation.value());
                            } else {
                                beanDefinition.setScope("singleton");
                            }
                            beanDefinition.setClazz(clazz);
                            beanDefinitionMap.put(beanName,beanDefinition);



                        }



                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    /**
     * 获取bean对象
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
               Object o = iocContainer.get(beanName);
               return o;
            }else {
                //TODO 去创建bean对象
                return createBean(beanDefinition,beanName);
            }
        } else {
            //TODO 处理异常
        }
        return null;
    }

}
