package registry;

import entity.RpcRequest;

//注册中心
public interface ServiceRegistry {
    //服务注册
    <T> void register(T service);
    //获取服务
    Object getService(RpcRequest rpcRequest);

    int getCode();

    static ServiceRegistry getByCode(int code){
        switch (code){
            case 0:
                return new DefaultServiceRegistry();
            case 1:
                return new MultiImplServiceRegistry();
            default:
                return null;
        }
    }
}
