package com.nhx.core.registry;

import com.nhx.core.entity.RpcRequest;
import com.nhx.common.enumeration.RegistryCode;
import com.nhx.common.enumeration.RpcError;
import com.nhx.common.exception.RpcException;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//一种服务只能对应一种实现
public class DefaultServiceRegistry implements  ServiceRegistry{
    private static Logger logger = Logger.getLogger(DefaultServiceRegistry.class.getClass());
    private Map<String,Object> serviceMap = new ConcurrentHashMap<>();
    private Set<String> registeredService = new HashSet<>();

    public Map<String, Object> getServiceMap() {
        return serviceMap;
    }

    public void setServiceMap(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    public Set<String> getRegisteredService() {
        return registeredService;
    }

    public void setRegisteredService(Set<String> registeredService) {
        this.registeredService = registeredService;
    }

    @Override
    public synchronized <T> void register(T service,String... groupId) {
        String serviceName = service.getClass().getCanonicalName();
        if(registeredService.contains(serviceName)) return;//如果该服务的实现类已经存在，直接忽略
        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length==0){
            logger.error("向接口:"+interfaces+" 注册服务 "+serviceName+"失败");
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }for (Class<?> i:interfaces){
            serviceMap.put(i.getCanonicalName(),service);
        }
        logger.info("向接口:"+interfaces+" 注册服务 "+serviceName);
    }

    @Override
    public Object getService(RpcRequest request) {
        Object service = serviceMap.get(request.getInterfactName());
        if(service==null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }

    @Override
    public int getCode() {
        return RegistryCode.valueOf("SingleImpl").getCode();
    }
}
