package EasyClient;

import api.HelloObject;
import api.HelloService;

import java.util.concurrent.Callable;

public class ClientThread implements Callable {

    private HelloObject helloObject;
    private HelloService helloService;

    public ClientThread(HelloObject helloObject, HelloService helloService) {
        this.helloObject = helloObject;
        this.helloService = helloService;
    }

    @Override
    public Object call() throws Exception {
        String res = helloService.hello(helloObject);
        return res;
    }
}
