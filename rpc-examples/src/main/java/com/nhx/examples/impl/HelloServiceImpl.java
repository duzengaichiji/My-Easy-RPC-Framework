package com.nhx.examples.impl;

import com.nhx.core.annotation.Service;
import com.nhx.examples.api.HelloObject;
import com.nhx.examples.api.HelloService;

import java.lang.reflect.Method;

//服务接口的具体实现，理论上只存在于服务提供者
@Service
public class HelloServiceImpl implements HelloService {
    //private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    public Object hello(HelloObject object) {
        //logger.debug("接收到:{}",object.getMessage());
        System.out.println("接收到:{}"+object.getMessage());
        return "this is the return value,id=" + object.getId();
    }

    public Object invokeMethod(Method method, Object[] args) {
        return null;
    }
}
