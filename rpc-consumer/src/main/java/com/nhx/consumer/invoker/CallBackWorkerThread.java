package com.nhx.consumer.invoker;

import com.nhx.core.futureTask.UnCompletedFuture;

public class CallBackWorkerThread implements Runnable{
    UnCompletedFuture<?> unCompletedFuture;

    public CallBackWorkerThread(UnCompletedFuture<?> unCompletedFuture) {
        this.unCompletedFuture = unCompletedFuture;
    }

    @Override
    public void run() {
        //对结果的一些其他处理
        this.unCompletedFuture.complete();
    }
}
