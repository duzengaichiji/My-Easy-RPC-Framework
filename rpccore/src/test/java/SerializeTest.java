import entity.HeartbeatRequest;
import entity.RpcRequest;
import enumeration.SerializerCode;
import org.junit.Test;
import serializer.CommonSerializer;

public class SerializeTest {
    @Test
    public void serializeTest1(){
        HeartbeatRequest heartbeatRequest = new HeartbeatRequest("sb");
        RpcRequest rpcRequest = new RpcRequest("111","111","111",null,null);
        CommonSerializer commonSerializer = CommonSerializer.getByCode(SerializerCode.KRYO.getCode());
        System.out.println(commonSerializer.deserialize(commonSerializer.serialize(heartbeatRequest),HeartbeatRequest.class));
        System.out.println(commonSerializer.deserialize(commonSerializer.serialize(rpcRequest),RpcRequest.class));
    }
}
