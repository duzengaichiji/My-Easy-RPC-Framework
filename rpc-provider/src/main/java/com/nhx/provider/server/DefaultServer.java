package com.nhx.provider.server;

import com.nhx.core.entity.Jentity.JRequest;
import com.nhx.core.flow.control.FlowController;
import com.nhx.core.registry.Jregistry.JServer;
import com.nhx.core.registry.Jregistry.RegistryService;
import com.nhx.core.transport.JAcceptor;
import com.nhx.provider.container.DefaultServiceProviderContainer;
import com.nhx.provider.container.ServiceProviderContainer;
import org.apache.log4j.Logger;

/**
 * @ClassName DefaultServer
 * @Author nhx
 * @Date 2020/9/22 20:28
 **/
public class DefaultServer implements JServer {
    private static final Logger logger = Logger.getLogger(DefaultServer.class.getClass());

    //本地容器
    private final ServiceProviderContainer providerContainer = new DefaultServiceProviderContainer();
    //服务发布（SPI)
    //private final RegistryService registryService;

    //全局流量控制器
    private FlowController<JRequest> globalFlowController;

    //IO Acceptor
    private JAcceptor acceptor;

    public DefaultServer(){

    }

    @Override
    public JAcceptor acceptor() {
        return acceptor;
    }

    @Override
    public JServer withAcceptor(JAcceptor acceptor) {
        return null;
    }

    @Override
    public RegistryService registryService() {
        //return registryService;
        return null;
    }

    @Override
    public void withGlobalFlowController(FlowController<JRequest> flowController) {

    }

    @Override
    public ServiceRegistry serviceRegistry() {
        return null;
    }

    @Override
    public void connectToRegistryServer(String connectString) {

    }
}
