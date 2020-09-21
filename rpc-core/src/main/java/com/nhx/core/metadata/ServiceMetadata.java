package com.nhx.core.metadata;

import java.io.Serializable;

import static com.nhx.common.util.Requires.requireNotNull;

public class ServiceMetadata extends Directory implements Serializable {
    private static final long serialVersionUID = 5085296803331516547L;

    private String group;   //服务分组
    private String serviceProviderName; //服务名称
    private String version; //服务版本

    public ServiceMetadata() {
    }

    public ServiceMetadata(String group, String serviceProviderName, String version) {
        this.group = requireNotNull(group, "group");
        this.serviceProviderName = requireNotNull(serviceProviderName, "serviceProviderName");
        this.version = requireNotNull(version, "version");
    }


    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getServiceProviderName() {
        return serviceProviderName;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setServiceProviderName(String serviceProviderName) {
        this.serviceProviderName = serviceProviderName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        int result = group.hashCode();
        result = 31 * result + serviceProviderName.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj) return true;
        if(obj==null||getClass()!= obj.getClass()) return false;

        ServiceMetadata serviceMetadata = (ServiceMetadata) obj;
        return group.equals(serviceMetadata.group)
                && serviceProviderName.equals(serviceMetadata.serviceProviderName)
                && version.equals(serviceMetadata.version);
    }

    @Override
    public String toString() {
        return "ServiceMetadata{" +
                "group='" + group + '\'' +
                ", serviceProviderName='" + serviceProviderName + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
