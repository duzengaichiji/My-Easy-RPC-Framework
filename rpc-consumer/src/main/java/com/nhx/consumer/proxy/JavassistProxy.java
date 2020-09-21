package com.nhx.consumer.proxy;

import com.nhx.core.entity.RpcRequest;
import com.nhx.common.enumeration.InvokerCode;
import com.nhx.consumer.invoker.Invoker;
import javassist.*;
import javassist.bytecode.AccessFlag;
import com.nhx.consumer.nettyClient.NettyClient;
import com.nhx.consumer.client.RpcClient;

import java.lang.reflect.Method;

//用javassist实现动态代理
@Deprecated
public class JavassistProxy extends AbstractProxy{
    private static ClassPool mpool;

    public JavassistProxy(RpcClient client, InvokerCode invoker) {
        super(client, invoker);
        mpool = ClassPool.getDefault();
    }

    //创建一个通用的构造请求的方法
    public String makeMethodSrc(){
        //组合方法体...
        StringBuilder src = new StringBuilder("");
        //方法访问权限
        src.append("public ");
        //返回值类型
        src.append("Object[] ");
        //方法名字
        src.append("invokeMethod");
        //加入参数列表
        StringBuilder paramS = new StringBuilder("(");
        paramS.append("java.lang.reflect.Method method,");
        paramS.append("Object[] args");
        paramS.append(")");
        src.append(paramS);
        src.append("{\n");
        //加入方法逻辑
        src.append("\tString serviceName = method.getDeclaringClass().getName();\n");
        String nettyClientRef = NettyClient.class.getCanonicalName();
        //构造请求体
        src.append("\tString groupId = "+nettyClientRef+".getServiceGroupMap().get(serviceName);\n");
        String rpcRef = RpcRequest.class.getCanonicalName();
        src.append("\t"+rpcRef+" rpcRequest = new "+rpcRef+"(\n" +
                "                java.util.UUID.randomUUID().toString(),\n" +
                "                method.getDeclaringClass().getName(),\n" +
                "                method.getName(),\n" +
                "                args,\n" +
                "                method.getParameterTypes(),\n" +
                "                (String)groupId\n" +
                "        );\n");
        //获取连接
        src.append("\tio.netty.channel.Channel channel = client.getChannel(rpcRequest);\n");
        String rpcExceptionRef = "RpcException";
        String rpcErrorRef = "RpcError";
        src.append("\tif (channel == null) {\n" +
                "            throw new "+rpcExceptionRef+"("+rpcErrorRef+".CLIENT_CONNECT_SERVER_FAILURE);\n" +
                "        }\n");
        //迷之调用失败。。
        //src.append("\tObject result = invoker.invoke(channel,rpcRequest);\n");
        src.append("\treturn new Object[]{rpcRequest,channel};\n");
        src.append("}");
        //System.out.println(src);
        return src.toString();
    }

    public <T> T getProxy(Class<T> clazz) throws Exception {
        //获取被代理类的各方法
        Method[] methods = clazz.getMethods();
        //创建一个空的类
        CtClass mctc = mpool.makeClass(clazz.getName()+"-javassistProxy");
        //添加字段:client,invoker
        mpool.insertClassPath(new ClassClassPath(RpcClient.class));
        CtClass clientCc = mpool.get(RpcClient.class.getName());
        CtField clientField0 = new CtField(clientCc, "com/nhx/consumer/client",mctc);
        clientField0.setModifiers(AccessFlag.PRIVATE);
        mctc.addField(clientField0);

        mpool.insertClassPath(new ClassClassPath(Invoker.class));
        CtClass invokerCc = mpool.get(Invoker.class.getName());
        CtField clientField1 = new CtField(clientCc, "com/nhx/consumer/invoker",mctc);
        clientField1.setModifiers(AccessFlag.PRIVATE);
        mctc.addField(clientField1);
        //添加构造函数
        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{clientCc,invokerCc},mctc);
        // $0代表this, $1代表构造函数的第1个参数
        ctConstructor.setBody("{$0.client=$1;$0.invoker=$2;}");
        mctc.addConstructor(ctConstructor);
        //为这个类赋予接口
        mpool.insertClassPath(new ClassClassPath(clazz));
        mctc.addInterface(mpool.get(clazz.getName()));
        //只为这种代理创建一个方法，就是invoker，调用的时候显示的将method，paramters...等传入
        mctc.addMethod(
                    CtNewMethod.make(
                            makeMethodSrc(),
                            mctc
                    )
            );
        Class<?> pc = mctc.toClass();
        T proxy = (T) pc.getConstructor(RpcClient.class,Invoker.class).newInstance(this.getClient(),this.getInvoker());
        return proxy;
    }
}
