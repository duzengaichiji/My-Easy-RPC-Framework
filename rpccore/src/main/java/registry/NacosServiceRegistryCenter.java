package registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import loadbalancer.LoadBalancer;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NacosServiceRegistryCenter implements ServiceRegistryCenter {
    //注册中心的地址
    private static final String SERVER_ADDR = "127.0.0.1:8848";
    //NamingService用于查找服务
    private static NamingService namingService;
    //记录所有注册了的服务的名字
    private Set<String> serviceNames = new HashSet<>();
    //负载均衡器
    private LoadBalancer loadBalancer;

    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        }catch (NacosException e){
            e.printStackTrace();
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            namingService.registerInstance(serviceName,inetSocketAddress.getHostName(),inetSocketAddress.getPort());
            //serviceNames.add(serviceName);
        }catch (NacosException e){
            System.out.println(e);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            //直接返回服务列表中的第一个，在这里可以配置负载均衡
            //用负载均衡算法得到其中一个服务提供者
            Instance instance = loadBalancer.select(instances);//instances.get(0);
            //返回服务提供者的 套接字
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        }catch (NacosException e){
            System.out.println(e);
        }
        return null;
    }

    public void clearRegistry(){
        //注销所有服务
        if(!serviceNames.isEmpty()){
            Iterator<String> iterator = serviceNames.iterator();
            while (iterator.hasNext()){
                String serviceName = iterator.next();
                try {
                    namingService.deregisterInstance(serviceName,"127.0.0.1",9000);
                }catch (NacosException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
