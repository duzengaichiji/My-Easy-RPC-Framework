package com.nhx.core.registry.Jregistry;

import java.util.Collection;

/**
 * @ClassName RegistryService
 * @Author nhx
 * @Date 2020/9/22 20:18
 **/
public interface RegistryService {
    /**
     * 将服务注册到注册中心
     * @param registerMeta
     */
    void register(RegisterMeta registerMeta);

    /**
     * 将服务从注册中心上注销
     * @param registerMeta
     */
    void unregister(RegisterMeta registerMeta);

    /**
     *
     * @param registerMeta
     */
    //void subscribe(RegisterMeta registerMeta);

    /**
     * 查找服务
     * @param serviceMeta
     * @return
     */
    Collection<RegisterMeta> lookup(RegisterMeta.ServiceMeta serviceMeta);


}
