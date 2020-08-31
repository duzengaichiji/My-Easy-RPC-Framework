package registry;

import java.net.InetSocketAddress;

public interface ServiceRegistryCenter {
    //注册服务到注册中心
    void register(String serviceName, InetSocketAddress inetSocketAddress);
    //查找服务
    InetSocketAddress lookupService(String serviceName);
}
