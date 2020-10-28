package com.nhx.provider.container;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nhx.core.metadata.ServiceWrapper;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName DefaultServiceProviderContainer
 * @Author nhx
 * @Date 2020/9/22 20:36
 **/
public class DefaultServiceProviderContainer implements ServiceProviderContainer{
    private final static Logger logger = Logger.getLogger(DefaultServiceProviderContainer.class.getClass());
    private final ConcurrentHashMap<String,ServiceWrapper>serviceProviders = new ConcurrentHashMap<>();

    @Override
    public void registerService(String uniqueKey, ServiceWrapper serviceWrapper) {
        serviceProviders.put(uniqueKey, serviceWrapper);
        logger.info("ServiceProvider ["+uniqueKey+","+ serviceWrapper+"] is registered." );
    }

    @Override
    public ServiceWrapper lookupService(String uniqueKey) {
        return serviceProviders.get(uniqueKey);
    }

    @Override
    public ServiceWrapper removeService(String uniqueKey) {
        ServiceWrapper serviceWrapper = serviceProviders.remove(uniqueKey);
        if (serviceWrapper == null) {
            logger.warn("ServiceProvider ["+uniqueKey+"] not found.");
        } else {
            logger.info("ServiceProvider ["+uniqueKey+","+ serviceWrapper+"] is removed." );
        }
        return serviceWrapper;
    }

    @Override
    public List<ServiceWrapper> getAllServices() {
        return Lists.newArrayList(serviceProviders.values());
    }
}
