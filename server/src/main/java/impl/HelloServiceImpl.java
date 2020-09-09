package impl;

import annotation.Service;
import api.HelloObject;
import api.HelloService;

//服务接口的具体实现，理论上只存在于服务提供者
@Service
public class HelloServiceImpl implements HelloService {
    //private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    public String hello(HelloObject object) {
        //logger.debug("接收到:{}",object.getMessage());
        System.out.println("接收到:{}"+object.getMessage());
        return "this is the return value,id=" + object.getId();
    }
}
