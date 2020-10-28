package com.nhx.core.metadata;

import com.nhx.common.util.Pair;
import com.nhx.core.flow.control.FlowController;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.nhx.common.util.Requires.requireNotNull;

/**
 * @ClassName nhx
 * @Author nhx
 * @Date 2020/9/22 20:03
 **/
public class ServiceWrapper implements Serializable {

    private static final long serialVersionUID = 7617404671290201596L;

    //服务元数据
    private final ServiceMetadata metadata;
    //服务对象
    private final Object serviceProvider;
    //key: method name
    //value: pair.first: 方法的参数类型
    //       pair.second:方法显示声明抛出的异常类型
    private final Map<String, List<Pair<Class<?>[],Class<?>[]>>> extensions;
    //权重
    private int weight = 50;
    //provider私有线程池
    private Executor executor;
    //provider 私有流量控制器
    private FlowController<?> flowController;

    public ServiceWrapper(String group,
                          String providerName,
                          String version,
                          Object serviceProvider,
                          Map<String, List<Pair<Class<?>[], Class<?>[]>>> extensions) {

        metadata = new ServiceMetadata(group, providerName, version);

        this.extensions = requireNotNull(extensions, "extensions");
        this.serviceProvider = requireNotNull(serviceProvider, "serviceProvider");
    }

    public ServiceMetadata getMetadata() {
        return metadata;
    }

    public Object getServiceProvider() {
        return serviceProvider;
    }

    public Map<String, List<Pair<Class<?>[], Class<?>[]>>> getExtensions() {
        return extensions;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public FlowController<?> getFlowController() {
        return flowController;
    }

    public void setFlowController(FlowController<?> flowController) {
        this.flowController = flowController;
    }

    /**
     * 获取方法签名
     * @param methodName
     * @return
     */
    public List<Pair<Class<?>[], Class<?>[]>> getMethodExtension(String methodName) {
        return extensions.get(methodName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceWrapper wrapper = (ServiceWrapper) o;

        return metadata.equals(wrapper.metadata);
    }

    @Override
    public int hashCode() {
        return metadata.hashCode();
    }

    @Override
    public String toString() {
        return "ServiceWrapper{" +
                "metadata=" + metadata +
                ", serviceProvider=" + serviceProvider +
                ", extensions=" + extensions +
                ", weight=" + weight +
                ", executor=" + executor +
                ", flowController=" + flowController +
                '}';
    }
}
