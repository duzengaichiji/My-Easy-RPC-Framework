package com.nhx.core.registry.Jregistry;

import com.nhx.core.annotation.Service;
import com.nhx.core.entity.Jentity.JRequest;
import com.nhx.core.flow.control.FlowController;
import com.nhx.core.metadata.ServiceWrapper;
import com.nhx.core.transport.JAcceptor;

import java.util.concurrent.Executor;

public interface JServer extends Registry{
    /**
     * 本地服务注册
     */
    interface ServiceRegistry{
        ServiceRegistry interfaceClass(Class<?> interfaceClass);

        /**
         * 设置服务组别
         * @param group
         * @return
         */
        ServiceRegistry group(String group);

        /**
         * 设置服务名称
         * @param providerName
         * @return
         */
        ServiceRegistry provideName(String providerName);

        /**
         * 设置服务版本号
         * @param version
         * @return
         */
        ServiceRegistry version(String version);

        /**
         * 设置服务权重
         * @param weight 0 < weight <= 100
         * @return
         */
        ServiceRegistry weight(int weight);

        /**
         * 设置服务提供者的私有线程池
         * @param executor
         * @return
         */
        ServiceRegistry executor(Executor executor);

        /**
         * 设置一个私有的流量控制器
         * @param flowController
         * @return
         */
        ServiceRegistry flowController(FlowController<JRequest> flowController);

        /**
         * 注册服务到本地容器
         * @return
         */
        ServiceWrapper register();
    }

    interface ProviderInitializer<T> {
        /**
         * 初始化服务提供者
         * @param provider
         */
        void init(T provider);
    }

    /**
     * 网络层acceptor.
     */
    JAcceptor acceptor();

    /**
     * 设置网络层acceptor.
     */
    JServer withAcceptor(JAcceptor acceptor);

    /**
     * 注册服务实例
     */
     RegistryService registryService();

    /**
     * 设置全局的流量控制器.
     */
    void withGlobalFlowController(FlowController<JRequest> flowController);

    /**
     * 获取服务注册(本地)工具.
     */
    ServiceRegistry serviceRegistry();


}
