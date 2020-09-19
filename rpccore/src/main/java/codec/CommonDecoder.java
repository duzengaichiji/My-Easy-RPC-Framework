package codec;

import entity.HeartbeatRequest;
import entity.RpcRequest;
import entity.RpcResponse;
import enumeration.PackageType;
import enumeration.RpcError;
import exception.RpcException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.apache.log4j.Logger;
import serializer.CommonSerializer;

import java.util.List;

public class CommonDecoder extends ReplayingDecoder {
    private static Logger logger = Logger.getLogger(CommonEncoder.class.getClass());
    //协议包识别码，要求和encoder里面保持一致
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //读取最前面的4个字节（魔数），后面的不用说了，就是按照协议定义的数据包，将消息解包
        int magic = byteBuf.readInt();
        if(magic!=MAGIC_NUMBER){
            logger.error("无法识别的协议包");
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        int packageCode = byteBuf.readInt();
        Class<?> packageClass = null;
        if(packageCode== PackageType.REQUEST_PACK.getCode()){
            packageClass = RpcRequest.class;
        }else if(packageCode==PackageType.RESPONSE_PACK.getCode()){
            packageClass = RpcResponse.class;
        }else if(packageCode==PackageType.HEARTBEAT_PACK.getCode()){
            packageClass = HeartbeatRequest.class;
        }
        else{
            logger.error("无法识别的数据包");
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        int serializerCode = byteBuf.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if(serializer==null){
            logger.error("不识别的反序列化器");
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        //反序列化
        Object object = serializer.deserialize(bytes,packageClass);
        list.add(object);
    }
}
