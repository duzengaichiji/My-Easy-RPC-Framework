package factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ThreadPoolFactory {
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private static Map<String, ExecutorService> threadPoolsMap = new HashMap<String, ExecutorService>();

    private ThreadPoolFactory(){

    }

    public static ExecutorService createDefaultThreadPool(String namePrefix){
        return createDefaultThreadPool(namePrefix,false);
    }

    private static ExecutorService createDefaultThreadPool(final String namePrefix, Boolean b) {
        ExecutorService pool = threadPoolsMap.computeIfAbsent(namePrefix,k->createThreadPool(namePrefix,b));
        if(pool.isShutdown()||pool.isTerminated()){
            threadPoolsMap.remove(namePrefix);
            pool = createThreadPool(namePrefix,b);
            threadPoolsMap.put(namePrefix,pool);
        }
        return pool;
    }

    private static ExecutorService createThreadPool(String namePrefix, Boolean b) {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createThreadFactory(namePrefix,b);
        return new ThreadPoolExecutor(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE_SIZE,KEEP_ALIVE_TIME,TimeUnit.MINUTES,workQueue,threadFactory);
    }

    private static ThreadFactory createThreadFactory(String namePrefix, Boolean b) {
        if (namePrefix != null) {
            if (b != null) {
                return new ThreadFactoryBuilder().setNameFormat(namePrefix + "-%d").setDaemon(b).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(namePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }


}
