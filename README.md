# My-Easy-RPC-Framework
简单的rpc框架实现

**rpcparent 版本控制**  
**client 客户端实现**  
**server 服务端实现**  
**rpcapi 接口和api**  
**rpccommon  公共实体对象，工具类**  
**rpccore  核心功能类**

# 1.最简单的实现
假设客户端已经知道服务端的地址，服务端监听对应的
端口，等待请求之后进行处理并返回即可。  
采用原生BIO+线程池的方式实现。
# 2.添加服务注册表
添加registry包，用map保持接口类型和实现类对象之间的关系。
请求到达时，根据请求的接口调用对应的服务实现对象。
# TODO


