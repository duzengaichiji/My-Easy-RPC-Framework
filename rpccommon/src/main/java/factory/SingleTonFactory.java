package factory;

import java.util.HashMap;
import java.util.Map;

//单例工厂
public class SingleTonFactory {
    //单例对象的容器
    private static Map<Class,Object> objectMap = new HashMap<>();
    private SingleTonFactory(){}

    public static <T> T getInstance(Class<T> clazz){
        Object instance = objectMap.get(clazz);
        //如果当前类对应的对象存在的话，就不会创建
        if(instance==null) {
            synchronized (clazz) {
                if (instance == null) {
                    try {
                        instance = clazz.newInstance();
                        objectMap.put(clazz, instance);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return clazz.cast(instance);
    }
}
