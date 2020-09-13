package api;

import entity.RpcResponse;
import futureTask.UnCompletedFuture;

import java.lang.reflect.Method;

//服务接口
public interface HelloService extends AbstractService{
    Object hello(HelloObject object);
}
