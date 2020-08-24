package registry;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceRegistry implements  ServiceRegistry{

    private Map<String,Object> serviceMap = new ConcurrentHashMap<>();
    private Set<String> registeredService = new HashSet<>();

    @Override
    public synchronized <T> void register(T service) {
        String serviceName = service.getClass().getCanonicalName();
        if(registeredService.contains(serviceName)) return;;
        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length==0){
            //throw
        }for (Class<?> i:interfaces){
            serviceMap.put(i.getCanonicalName(),service);
        }
        System.out.println("向接口:"+interfaces+" 注册服务 "+serviceName);
    }

    @Override
    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service==null){
            //throw
        }
        return service;
    }
}
