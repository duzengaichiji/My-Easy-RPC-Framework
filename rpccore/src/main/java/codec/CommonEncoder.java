package codec;

import entity.HeartbeatRequest;
import entity.RpcRequest;
import entity.RpcResponse;
import enumeration.PackageType;
import enumeration.RpcError;
import exception.RpcException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import serializer.CommonSerializer;

/*
Encoder的作用就是将消息实体从对象转换为byte，写入到bytebuf，
然后送入ChannelOutBoundHandler处理之后返回给客户端
 */

//自定义编码器，即自定义协议包
/*
这里定义的协议：
+---------------+---------------+-----------------+-------------+
|  Magic Number |  Package Type | Serializer Type | Data Length |
|    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
+---------------+---------------+-----------------+-------------+
|                          Data Bytes                           |
|                   Length: ${Data Length}                      |
+---------------------------------------------------------------+
Magic Number:一个4字节魔数，标识一个协议包；
PackageType:标识这是一个请求还是一个回应;
SerializerType:标识了数据包使用的序列化器;
DataLength:标识实际的数据包长度，为了防止粘包;
 */
public class CommonEncoder extends MessageToByteEncoder {
    //魔数，没有实际意义
    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    //序列化器
    private CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer){
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        //写入协议包的第一个字段（魔数）
        byteBuf.writeInt(MAGIC_NUMBER);
        //写入协议包的第二个字段（数据包类型，（请求/回复））
        if(o instanceof RpcRequest){
            byteBuf.writeInt(PackageType.REQUEST_PACK.getCode());
        }else if(o instanceof HeartbeatRequest){
            byteBuf.writeInt(PackageType.HEARTBEAT_PACK.getCode());
        } else if(o instanceof RpcResponse){
            byteBuf.writeInt(PackageType.RESPONSE_PACK.getCode());
        }else {
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        //写入协议包的第三个字段（序列化器类型）
        byteBuf.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(o);
        //写入协议包的第四个字段（数据长度）
        byteBuf.writeInt(bytes.length);
        //写入数据
        byteBuf.writeBytes(bytes);
    }
}
