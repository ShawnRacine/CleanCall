package com.racine.cleancalls.net.ThreadTask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Shawn Racine.
 */
public abstract class AbsThreadTask<Params, Result> {
    private static final String TAG = "ThreadTask";

    private static final int CORE_LIMIT = 4;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT > CORE_LIMIT ? CORE_LIMIT : CPU_COUNT;
    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE;
    private static final int KEEP_ALIVE = 30;

//    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
//        private final AtomicInteger mCount = new AtomicInteger(1);
//
//        public Thread newThread(Runnable r) {
//
//            return new Thread(r, "New Thread #" + mCount.getAndIncrement());
//        }
//    };

    private static final ThreadFactory sThreadFactory = Executors.defaultThreadFactory();

    //KEEP_ALIVE is invalid, only when allowCoreThreadTimeOut(true);
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

//    private static final BlockingQueue<Runnable> sPoolWorkQueue =
//            new SynchronousQueue<>();

    private static final RejectedExecutionHandler sRejectedHandler =
            new ThreadPoolExecutor.CallerRunsPolicy();

    /**
     * An {@link Executor} that can be used to execute tasks in parallel.
     */
    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory, sRejectedHandler);

    private InternalHandler sHandler;

    private static final int MESSAGE_POST_RESULT = 0x1;

    private Runnable mWorker;

    private Params params;

    private Handler getHandler() {
        synchronized (AbsThreadTask.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler();
            }
            return sHandler;
        }
    }

    public AbsThreadTask() {

        THREAD_POOL_EXECUTOR.allowCoreThreadTimeOut(true);

        mWorker = new Runnable() {

            @Override
            public void run() {
                Result result = doInBackground(params);
                Message message = getHandler().obtainMessage(MESSAGE_POST_RESULT, result);
                message.sendToTarget();
            }
        };
    }

    protected abstract Result doInBackground(Params params);

    protected Params onPreExecute() {
        return null;
    }

    protected void onPostExecute(Result result) {
    }

    private class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    onPostExecute((Result) msg.obj);
                    break;
            }
        }
    }

    public AbsThreadTask<Params, Result> execute() {
        params = onPreExecute();
        THREAD_POOL_EXECUTOR.execute(mWorker);
        return this;
    }
}
