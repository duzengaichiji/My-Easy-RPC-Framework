package EasyClient;

import entity.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class EasyRpcClient {
    private String host;
    private int port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public EasyRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return "EasyRpcClient{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

    public Object sendRequest(RpcRequest request) {
        try{
            //尝试向对应端口发送rpc请求
            Socket socket = new Socket(host,port);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        }catch (IOException | ClassNotFoundException e){
            //logger.error("error:",e);
            System.out.println("error:"+e);
            return null;
        }
    }

    public void setServiceGroup(Class service, String groupId) {

    }


}
