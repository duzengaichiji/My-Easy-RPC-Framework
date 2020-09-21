package com.nhx.core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.nhx.common.enumeration.LoadBalancerCode;
import com.nhx.common.enumeration.RpcError;
import com.nhx.common.exception.RpcException;
import com.nhx.core.loadbalancer.LoadBalancer;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NacosServiceRegistryCenter implements ServiceRegistryCenter {
    private static Logger logger = Logger.getLogger(NacosServiceRegistryCenter.class.getClass());
    //注册中心的地址
    private final String SERVER_ADDR;
    //NamingService用于查找服务
    private static NamingService namingService;
    //记录所有注册了的服务的名字
    private Set<String> serviceNames;
    //负载均衡器
    private final LoadBalancer loadBalancer;


    public NacosServiceRegistryCenter(String address,Integer... lb) {
        SERVER_ADDR = address;
        Integer lbc = LoadBalancerCode.RANDOMLOADBALANCER.getCode();
        if(lb.length!=0) lbc = lb[0];
        loadBalancer = LoadBalancer.getByCode(lbc);
        serviceNames = new HashSet<>();
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            logger.error("Nacos服务器连接失败");
            e.printStackTrace();
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            namingService.registerInstance(serviceName,inetSocketAddress.getHostName(),inetSocketAddress.getPort());
            logger.info("向Nacos注册中心注册服务:"+serviceName+"; 服务端口为:"+inetSocketAddress.getPort());
            serviceNames.add(serviceName);
        }catch (NacosException e){
            logger.error("向Nacos注册中心注册服务:"+serviceName+"; 服务端口为:"+inetSocketAddress.getPort()+"失败");
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            if(instances.size()==0) throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            logger.info("service:"+serviceName+" has total "+instances.size()+" implements");
            //直接返回服务列表中的第一个，在这里可以配置负载均衡
            //用负载均衡算法得到其中一个服务提供者
            Instance instance = loadBalancer.select(instances);//instances.get(0);
            //返回服务提供者的 套接字
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        }catch (NacosException e){
            logger.error("未找到服务:"+serviceName+" 对应的提供者");
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
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
        System.out.println("关闭本机，注销本机上的所有服务");
    }
}
