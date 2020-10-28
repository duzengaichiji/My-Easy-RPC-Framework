package com.nhx.provider.container;

import com.nhx.core.metadata.ServiceWrapper;

import java.util.List;

/**
 * 本地provider容器，不是注册中心
 * @ClassName ServiceProviderContainer
 * @Author nhx
 * @Date 2020/9/22 20:33
 **/
public interface ServiceProviderContainer {
    /**
     * 注册服务到本地容器
     * @param uniqueKey
     * @param serviceWrapper
     */
    void registerService(String uniqueKey, ServiceWrapper serviceWrapper);

    /**
     * 本地容器中查找服务
     * @param uniqueKey
     * @return
     */
    ServiceWrapper lookupService(String uniqueKey);

    /**
     * 从本地容器移除服务
     * @param uniqueKey
     * @return
     */
    ServiceWrapper removeService(String uniqueKey);

    /**
     * 获取本地容器中的所有服务
     * @return
     */
    List<ServiceWrapper> getAllServices();
}
