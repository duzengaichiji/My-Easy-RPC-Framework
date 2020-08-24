package EasyClient;

import entity.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class EasyRpcClient {
    //private static final Logger logger = LoggerFactory.getLogger(EasyRpcClient.class);
    public Object sendRequest(RpcRequest rpcRequest,String host,int port){
        try{
            //尝试向对应端口发送rpc请求
            Socket socket = new Socket(host,port);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        }catch (IOException | ClassNotFoundException e){
            //logger.error("error:",e);
            System.out.println("error:"+e);
            return null;
        }
    }
}
