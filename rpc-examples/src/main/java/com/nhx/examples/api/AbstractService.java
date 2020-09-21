package com.nhx.examples.api;

import java.lang.reflect.Method;

public interface AbstractService {
    public Object invokeMethod(Method method, Object[] args);
}
