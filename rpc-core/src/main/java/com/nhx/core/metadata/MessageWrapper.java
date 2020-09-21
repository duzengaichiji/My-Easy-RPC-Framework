package com.nhx.core.metadata;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

public class MessageWrapper implements Serializable {
    private static final long serialVersionUID = 8593655513212544502L;

    private String appName; //应用名称
    private final ServiceMetadata metadata; //目标服务数据
    private String methodName;  //目标方法名称
    private Object[] args;  //目标方法参数
    private Map<String,String> attachments;

    public MessageWrapper(ServiceMetadata metadata) {
        this.metadata = metadata;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public ServiceMetadata getMetadata() {
        return metadata;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }



    @Override
    public String toString() {
        return "MessageWrapper{" +
                "appName='" + appName + '\'' +
                ", metadata=" + metadata +
                ", methodName='" + methodName + '\'' +
                ", args=" + Arrays.toString(args) +
                ", attachments=" + attachments +
                '}';
    }
}
