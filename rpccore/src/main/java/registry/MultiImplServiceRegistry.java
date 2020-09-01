package registry;

import annotation.Service;
import entity.MethodSign;
import entity.RpcRequest;
import enumeration.RegistryCode;
import enumeration.RpcError;
import exception.RpcException;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MultiImplServiceRegistry implements ServiceRegistry{

    Map<String,Map<MethodSign,Object>> serviceMap = new ConcurrentHashMap<>();
    Set<String> registeredService = new HashSet<>();

    public Map<String, Map<MethodSign, Object>> getServiceMap() {
        return serviceMap;
    }

    public void setServiceMap(Map<String, Map<MethodSign, Object>> serviceMap) {
        this.serviceMap = serviceMap;
    }

    public Set<String> getRegisteredService() {
        return registeredService;
    }

    public void setRegisteredService(Set<String> registeredService) {
        this.registeredService = registeredService;
    }

    @Override
    public <T> void register(T service) {
        String serviceName = service.getClass().getCanonicalName();
        //该服务的实现已经存在
        if(registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);

        String interfaceName = service.getClass().getInterfaces()[0].getName();
        //如果当前服务还未有实现，添加该类的实现列表
        Map<MethodSign,Object> serviceImpls = serviceMap.get(interfaceName);
        if(serviceImpls==null) {
            serviceImpls = new ConcurrentHashMap<>();
        }
        Method[] methods = service.getClass().getMethods();
        Method method = null;
        //逐个检查服务对应的方法是否有新的实现
        for (int i = 0; i < methods.length; i++) {
            //方法签名作为键，实现类对象作为值
            method = methods[i];
            MethodSign methodSign = new MethodSign(serviceName,method.getName(),method.getParameters(),method.getParameterTypes());
            serviceImpls.put(methodSign,service);
            System.out.println("向接口"+interfaceName+" 注册服务"+serviceName+"方法"+method.getName());
        }
    }

    @Override
    public Object getService(RpcRequest request) {
        Map<MethodSign,Object> targetMethodMap = serviceMap.get(request.getInterfactName());
        if(targetMethodMap==null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        MethodSign methodSign = new MethodSign(request);
        Object service = targetMethodMap.get(methodSign);
        if(service==null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }

    @Override
    public int getCode() {
        return RegistryCode.valueOf("MultiImpl").getCode();
    }

}
