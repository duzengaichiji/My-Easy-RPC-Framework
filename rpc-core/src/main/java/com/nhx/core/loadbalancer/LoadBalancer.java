package com.nhx.core.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public interface LoadBalancer {
    Instance select(List<Instance> instanceList);

    int getCode();

    public static LoadBalancer getByCode(int code) {
        switch (code) {
            case 0:
                return new RandomLoadBalancer();
            case 1:
                return new RoundRobinLoadBalancer();
            default:
                return null;
        }
    }
}
