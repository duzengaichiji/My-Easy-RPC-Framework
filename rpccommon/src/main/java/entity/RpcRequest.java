package entity;

import java.io.Serializable;
import java.util.Arrays;

//rpc请求协议
public class RpcRequest implements Serializable {
    //待调用接口的名字
    private  String interfactName;
    //待调用方法的名字
    private String methodName;
    //调用方法的参数
    private Object[] parameters;
    //调用方法的参数类型
    private Class<?>[] paramTypes;

    public RpcRequest() {
    }

    public RpcRequest(String interfactName, String methodName, Object[] parameters, Class<?>[] paramTypes) {
        this.interfactName = interfactName;
        this.methodName = methodName;
        this.parameters = parameters;
        this.paramTypes = paramTypes;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "interfactName='" + interfactName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                '}';
    }

    public String getInterfactName() {
        return interfactName;
    }

    public void setInterfactName(String interfactName) {
        this.interfactName = interfactName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }
}
