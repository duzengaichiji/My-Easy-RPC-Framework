package codec;

import entity.RpcRequest;
import entity.RpcResponse;
import enumeration.PackageType;
import enumeration.RpcError;
import exception.RpcException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import serializer.CommonSerializer;

import java.util.List;

public class CommonDecoder extends ReplayingDecoder {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magic = byteBuf.readInt();
        if(magic!=MAGIC_NUMBER){
            System.out.println("无法识别的协议包");
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        int packageCode = byteBuf.readInt();
        Class<?> packageClass = null;
        if(packageCode== PackageType.REQUEST_PACK.getCode()){
            packageClass = RpcRequest.class;
        }else if(packageCode==PackageType.RESPONSE_PACK.getCode()){
            packageClass = RpcResponse.class;
        }else{
            System.out.println("无法识别的数据包");
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        int serializerCode = byteBuf.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if(serializer==null){
            System.out.println("不识别的反序列化器");
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Object object = serializer.deserialize(bytes,packageClass);
        list.add(object);
    }
}
