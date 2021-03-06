package com.nhx.provider.server;

import com.nhx.core.annotation.ReflectUtil;
import com.nhx.core.annotation.Service;
import com.nhx.core.annotation.ServiceScan;
import com.nhx.common.enumeration.RpcError;
import com.nhx.common.exception.RpcException;
import org.apache.log4j.Logger;

import java.util.Set;

public class AbstractRpcServer  implements RpcServer{
    private static Logger logger = Logger.getLogger(AbstractRpcServer.class.getClass());
    @Override
    public void start() {

    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass, String... groupId) {

    }

    public void scanServices() {
        //获取程序调用栈，获取启动类的全包名
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            //反射获取启动类的class对象，判断启动类是否加入serviceScan注解
            startClass = Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        //获取注解的参数
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if("".equals(basePackage)) {
            //获取启动类所在的路径
            //basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
            //如果没有指定路径，默认加载整个包
            basePackage = "";
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        logger.info("there are total "+classSet.size()+" classes");
        for(Class<?> clazz : classSet) {
            //如果扫描的类包含了 Service 注解
            if(clazz.isAnnotationPresent(Service.class)) {
                //定义服务的名字，暂时没用。。
                String serviceName = clazz.getAnnotation(Service.class).name();
                String serviceGroup = clazz.getAnnotation(Service.class).group();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
//                if("".equals(serviceName)) {
//                    Class<?>[] interfaces = clazz.getInterfaces();
//                    for (Class<?> oneInterface: interfaces){
//                        //publishService(obj, oneInterface.getCanonicalName(),serviceGroup);
//                    }
//                } else {
//                    publishService(obj, serviceName,serviceGroup);
//                }
                //注册服务对象
                publishService(obj,clazz.getInterfaces()[0],serviceGroup);
            }
        }
    }
}
