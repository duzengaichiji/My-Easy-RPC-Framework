package registry;

import entity.RpcRequest;
import enumeration.RegistryCode;
import enumeration.RpcError;
import exception.RpcException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GroupServiceRegistry implements ServiceRegistry {

    private Map<String, Map<String,Object>> serviceMap = new ConcurrentHashMap<>();
    private Set<String> registeredService = new HashSet<>();

    public GroupServiceRegistry(){
        //未指定分组的服务就放在默认分组当中
        Map<String,Object> map = new ConcurrentHashMap<>();
        serviceMap.put("default",map);
    }

    @Override
    public <T> void register(T service, String... groupId) {
        String serviceName = service.getClass().getCanonicalName();
        Class<?> interfaceName = null;
        try {
            interfaceName = service.getClass().getInterfaces()[0];
            String group;
            if(groupId.length==0) group = "default";
            else group = groupId[0];
            Map<String,Object> serviceGroup = serviceMap.get(group);
            if(serviceGroup!=null){
                serviceGroup.put(interfaceName.getCanonicalName(),service);
            }else{
                Map<String,Object> newGroup = new ConcurrentHashMap<>();
                newGroup.put(interfaceName.getCanonicalName(),service);
                serviceMap.put(group,newGroup);
            }
            registeredService.add(serviceName);
            System.out.println("向分组:"+group+" ，接口:"+interfaceName.getName()+" 注册服务:"+serviceName);
        }catch (Exception e){
            e.printStackTrace();
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
    }

    @Override
    public Object getService(RpcRequest rpcRequest) {
        String groupId = rpcRequest.getGroupId();
        if(groupId==null) groupId = "default";
        Map<String,Object> serviceGroup = serviceMap.get(groupId);
        if(serviceGroup==null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        Object service = serviceGroup.get(rpcRequest.getInterfactName());
        if(service==null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }

    @Override
    public int getCode() {
        return RegistryCode.valueOf("GroupImpl").getCode();
    }
}
