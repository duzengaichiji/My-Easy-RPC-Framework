package api;

import java.lang.reflect.Method;

//服务接口
public interface HelloService extends AbstractService{
    String hello(HelloObject object);
}
