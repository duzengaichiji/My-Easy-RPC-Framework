package rpcInterfaces;

public interface RpcServer {
    void start();

    <T> void publishService(Object service,Class<T> serviceClass);
}
