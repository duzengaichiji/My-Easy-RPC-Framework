package entity;

import java.util.Arrays;

public class MethodSign {
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;

    public MethodSign(RpcRequest rpcRequest){
        interfaceName = rpcRequest.getInterfactName();
        methodName = rpcRequest.getMethodName();
        parameters = rpcRequest.getParameters();
        paramTypes = rpcRequest.getParamTypes();
    }

    //对比两个目标方法的签名是否完全相同
    public boolean compare(MethodSign methodSign){
        if(!interfaceName.equals(methodSign.getInterfaceName())) return false;
        if(!methodName.equals(methodSign.getMethodName())) return false;
        if(parameters.length!=methodSign.getParameters().length) return false;
        Object[] tempParameters = methodSign.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if(!parameters[i].equals(tempParameters[i])) return false;
        }
        if(paramTypes.length!=methodSign.getParameters().length) return false;
        Class<?>[] tempParamTypes = methodSign.getParamTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            if(!paramTypes[i].equals(tempParamTypes[i])) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MethodSign{" +
                "interfaceName='" + interfaceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                '}';
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
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

    public MethodSign(String interfaceName, String methodName, Object[] parameters, Class<?>[] paramTypes) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameters = parameters;
        this.paramTypes = paramTypes;
    }
}
