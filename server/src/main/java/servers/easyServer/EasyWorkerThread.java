package servers.easyServer;

import entity.RpcRequest;
import entity.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

//rpc请求处理类
public class EasyWorkerThread implements Runnable{
    //private static final Logger logger = LoggerFactory.getLogger(WorkerThread.class);

    private Socket socket;
    private Object service;

    public EasyWorkerThread(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            //获取rpcRequest请求对象，通过反射调用Service对象的处理方法（Service由传入的实现类确定）
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            //将处理结果封装为rpcResponse响应对象
            Object returnObject = method.invoke(service, rpcRequest.getParameters());
            objectOutputStream.writeObject(RpcResponse.success(returnObject));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            //logger.error("调用或发送时有错误发生：", e);
            System.out.println("调用或发送时有错误发生："+ e);
        }
    }
}
