package com.nhx.provider.serverWithRegistry;

import com.nhx.core.entity.RpcRequest;
import com.nhx.core.entity.RpcResponse;
import com.nhx.core.registry.ServiceRegistry;
import com.nhx.core.handlers.providerhandlers.RequestHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RequesthandlerThread implements Runnable {

    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;

    public RequesthandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            String interfaceName = rpcRequest.getInterfactName();
            //从注册中心获取服务
            Object service = serviceRegistry.getService(rpcRequest);
            //调用handler方法，反射调用目标方法
            Object result = requestHandler.handler(rpcRequest, service);
            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("调用或发送时有错误发生："+e);
        }
    }
}
