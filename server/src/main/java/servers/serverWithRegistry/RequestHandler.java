package servers.serverWithRegistry;

import entity.RpcRequest;
import entity.RpcResponse;
import enumeration.ResponseCode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {
    public Object handler(RpcRequest rpcRequest,Object service){
        Object result = null;
        try{
            result = invokeTargetMethod(rpcRequest,service);
            System.out.println("服务:"+rpcRequest.getInterfactName()
            +" 成功调用方法："+rpcRequest.getMethodName());
        }catch (IllegalAccessException|InvocationTargetException e){
            System.out.println("调用时错误"+e);
        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest request,Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try{
            method = service.getClass().getMethod(
                    request.getMethodName(),
                    request.getParamTypes()
            );
        }catch (NoSuchMethodException e){
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }
        return method.invoke(service,request.getParameters());
    }
}
