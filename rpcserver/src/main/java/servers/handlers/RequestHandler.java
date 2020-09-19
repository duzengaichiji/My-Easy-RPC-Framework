package servers.handlers;

import entity.RpcRequest;
import entity.RpcResponse;
import enumeration.ResponseCode;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {
    private static Logger logger = Logger.getLogger(RequestHandler.class.getClass());
    public Object handler(RpcRequest rpcRequest,Object service){
        Object result = null;
        try{
            result = invokeTargetMethod(rpcRequest,service);
            logger.info("服务:"+rpcRequest.getInterfactName()
            +" 成功调用方法："+rpcRequest.getMethodName());
        }catch (IllegalAccessException|InvocationTargetException e){
            logger.error("调用时错误"+e);
        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest request,Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try{
            //通过反射获取目标方法和目标实现对象
            method = service.getClass().getMethod(
                    request.getMethodName(),
                    request.getParamTypes()
            );
        }catch (NoSuchMethodException e){
            //没有找到对应实现，调用失败
            logger.error("方法调用失败:"+e);
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }
        return method.invoke(service,request.getParameters());
    }
}
