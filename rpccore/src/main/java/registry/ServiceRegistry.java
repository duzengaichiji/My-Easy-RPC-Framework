package registry;

//注册中心
public interface ServiceRegistry {
    //服务注册
    <T> void register(T service);
    //获取服务
    Object getService(String serviceName);
}
