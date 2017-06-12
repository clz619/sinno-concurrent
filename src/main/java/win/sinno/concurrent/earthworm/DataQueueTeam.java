package win.sinno.concurrent.earthworm;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.concurrent.CustomThreadFactory;
import win.sinno.concurrent.earthworm.custom.IDataHandler;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 数据队列团队
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/11/4 下午7:58
 */
public class DataQueueTeam<DATA> implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger("earthworm");

    /**
     * boss
     */
    private DataQueueBoss<DATA> dataQueueBoss;

    /**
     * team name
     */
    private String name;

    /**
     * 工作者数量
     */
    private int workerCount;

    /**
     * 数据处理
     */
    private IDataHandler<DATA> dataHandler;

    /**
     * 超时时间，单位：毫秒(默认：30000毫秒=30秒)
     */
    private Long timeout = 30000l;

    /**
     * 是否使用超时
     */
    private Boolean useTimeout = false;


    /**
     * 处理服务
     */
    private ExecutorService executorService;

    /**
     * 结果处理器服务
     */
    private ExecutorService futureExecutorService;
    /**
     * 信号量，标识工人资源
     */
    private Semaphore semaphore;

    /**
     * 结果处理器
     */
    private FutureDaemon futureDaemon;

    /**
     * 工作标识
     */
    private volatile boolean workerFlag = true;

    //创建时间
    private Long createTs;


    //任务总数
    private AtomicLong totalCount;

    /**
     * 提交数量
     */
    private AtomicLong submitCount;

    //完成数量
    private AtomicLong finishCount;

    //失败数量
    private AtomicLong failCount;

    //取消数量
    private AtomicLong cancelCount;

    //超时
    private AtomicLong timeoutCount;


    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public DataQueueTeam(String name, DataQueueBoss<DATA> dataQueueBoss, IDataHandler<DATA> dataHandler) {

        //团队名称
        this.name = name;

        //队列处理器
        this.dataQueueBoss = dataQueueBoss;

        //数据处理器
        this.dataHandler = dataHandler;

        //工作者数量
        this.workerCount = dataQueueBoss.getWorkerCount();

        //处理器服务，缓存线程池
        this.executorService = Executors.newCachedThreadPool(new CustomThreadFactory("dqt-" + name));

        //固定一个的线程池，维护future处理
        this.futureExecutorService = Executors.newFixedThreadPool(1, new CustomThreadFactory("dqt-" + name + "-ft"));

        //工人资源
        this.semaphore = new Semaphore(workerCount);

        this.futureDaemon = new FutureDaemon(this);

        this.totalCount = new AtomicLong();

        this.submitCount = new AtomicLong();

        this.finishCount = new AtomicLong();

        this.failCount = new AtomicLong();

        this.cancelCount = new AtomicLong();

        this.timeoutCount = new AtomicLong();

        this.createTs = System.currentTimeMillis();
    }

    public DataQueueTeam(String name, DataQueueBoss<DATA> dataQueueBoss, IDataHandler<DATA> dataHandler, Boolean useTimeout, Long timeout) {
        this(name, dataQueueBoss, dataHandler);
        //设置超时
        this.useTimeout = useTimeout;
        this.timeout = timeout;
    }

    /**
     * 添加任务
     *
     * @param data
     */
    public void addTask(DATA data) {

        try {
            this.dataQueueBoss.dispathTask(data);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }

        this.totalCount.incrementAndGet();
    }

    /**
     * 添加多个任务
     *
     * @param datas
     */
    public void addTasks(Collection<DATA> datas) {

        if (CollectionUtils.isNotEmpty(datas)) {

            try {
                this.dataQueueBoss.dispathTasks(datas);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }

            this.totalCount.addAndGet(datas.size());
        }
    }

    /**
     * 获取当前第一个任务
     *
     * @return
     */
    public DATA getFirstTask() {
        return this.dataQueueBoss.getFirstTask();
    }

    /**
     * 获取任务数
     *
     * @return
     */
    public int getTaskCount() {
        return this.dataQueueBoss.getTaskCount();
    }

    public void clearTask() {
        this.dataQueueBoss.clearTask();
    }

    @Override
    public void run() {
        //执行结果，及资源释放服务
        this.futureExecutorService.submit(futureDaemon);

        DATA task = null;

        while (this.workerFlag) {

            try {
                if ((task = this.dataQueueBoss.getOneTask()) != null) {

                    try {
                        //获取资源
                        acquireOneResource();
                    } catch (InterruptedException e) {
                        LOG.error(e.getMessage(), e);
                        continue;
                    }

                    try {
                        //处理结果
                        Future future = this.executorService.submit(new DataQueueWorker<DATA>(this.dataHandler, task));

                        //提交数，增加1
                        this.submitCount.incrementAndGet();

                        //处理结果列表
                        this.futureDaemon.addFuture(task, future);

                    } catch (Exception e) {

                        LOG.error(e.getMessage(), e);

                        //处理失败-进行资源释放
                        releaseOneResource();

                        this.failCount.incrementAndGet();
                    }

                }
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }

        }
    }

    /**
     * 获取一个资源
     *
     * @throws InterruptedException
     */
    public void acquireOneResource() throws InterruptedException {
        this.semaphore.acquire();
    }

    public void releaseOneResource() {
        this.semaphore.release();
    }

    /**
     * 团队进行释放
     */
    public void release() {
        this.dataQueueBoss = null;
        this.dataHandler = null;
    }

    /**
     * 停止
     */
    public void stop() {
        this.workerFlag = false;
        //结果集处理线程进行停止
        this.futureDaemon.stop();
    }

    public Long getTimeout() {
        return timeout;
    }

    /**
     * 设置超时时间
     *
     * @param timeout
     */
    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Boolean getUseTimeout() {
        return useTimeout;
    }

    /**
     * 设置是否超时
     *
     * @param useTimeout
     */
    public void setUseTimeout(Boolean useTimeout) {
        this.useTimeout = useTimeout;
    }

    /**
     * team状态,配置状态+数据执行统计
     *
     * @return
     */
    public String status() {
        //状态
        StringBuilder status = new StringBuilder();
        status.append("\n=====Team==============");
        status.append("\n name         ").append(name);
        status.append("\n workerCount  ").append(workerCount);
        status.append("\n useTimeOut   ").append(useTimeout);
        status.append("\n timeount     ").append(timeout);
        status.append("\n createTs     ").append(DATE_FORMAT.format(new Date(createTs)));
        status.append("\n-----Status------------");
        status.append("\n totalCount   ").append(totalCount.get());
        status.append("\n submitCount  ").append(submitCount.get());
        status.append("\n finishCount  ").append(finishCount.get());
        status.append("\n failCount    ").append(failCount.get());
        status.append("\n cancelCount  ").append(cancelCount.get());
        status.append("\n timeoutCount ").append(timeoutCount.get());

        return status.toString();
    }

    /**
     * 处理结果集合-守护进程
     */
    private class FutureDaemon<DATA> implements Runnable {

        private List<FutureHandler<DATA>> futureHandlerList;

        private List<FutureHandler<DATA>> holderFutureHandlerList;

        private DataQueueTeam dataQueueTeam;

        //工作状态,线程可见
        private volatile boolean workerFlag = true;

        //读写锁
        private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

        //写入锁
        private Lock wLock = rwLock.writeLock();


        public FutureDaemon(DataQueueTeam dataQueueTeam) {

            this.futureHandlerList = new LinkedList<FutureHandler<DATA>>();
            this.holderFutureHandlerList = new LinkedList<FutureHandler<DATA>>();

            this.dataQueueTeam = dataQueueTeam;
        }

        /**
         * 添加future
         *
         * @param data
         * @param future
         */
        public void addFuture(DATA data, Future future) {
            wLock.lock();
            try {
                this.futureHandlerList.add(new FutureHandler<DATA>(data, future));
            } finally {
                wLock.unlock();
            }
        }


        //迁移future队列 至缓存队列，进行处理
        private void moveFuture() {

            if (CollectionUtils.isNotEmpty(futureHandlerList)) {
                try {
                    wLock.lock();
                    this.holderFutureHandlerList.addAll(futureHandlerList);
                    this.futureHandlerList.clear();
                } finally {
                    wLock.unlock();
                }
            }

        }

        private void consumeFuture() {

            Iterator<FutureHandler<DATA>> futureIterator = holderFutureHandlerList.iterator();
            while (futureIterator.hasNext()) {

                FutureHandler<DATA> futureHandler = futureIterator.next();
                Future future = futureHandler.getFuture();

                if (future.isDone() || future.isCancelled()) {
                    //完成 或 取消
                    futureIterator.remove();
                    //释放一个资源
                    this.dataQueueTeam.releaseOneResource();

                    if (future.isCancelled()) {
                        //canceled also is done
                        this.dataQueueTeam.cancelCount.incrementAndGet();
                    } else {
                        this.dataQueueTeam.finishCount.incrementAndGet();
                    }

                } else if (dataQueueTeam.useTimeout) {

                    // 超时处理
                    Long maxTs = futureHandler.getBeginTs() + dataQueueTeam.timeout;

                    if (maxTs < System.currentTimeMillis()) {

                        //最大处理时间小于当前时间，则为处理超时，超时处理
                        if (!(future.isDone() || future.isCancelled())) {

                            //开始时间
                            LOG.warn("{} beginTs:{} is time out.", new Object[]{futureHandler.getOriginData().toString(), futureHandler.getBeginTs()});

                            //执行取消
                            future.cancel(true);

                            this.dataQueueTeam.timeoutCount.incrementAndGet();
                        }
                    }

                }
            }
        }

        @Override
        public void run() {
            while (workerFlag) {
                try {
                    moveFuture();

                    if (CollectionUtils.isNotEmpty(holderFutureHandlerList)) {
                        //holder 的future不为空 进行状态检测
                        consumeFuture();
                    } else {
                        Thread.sleep(10l);
                    }
                } catch (Exception e) {
                    //ingore
                    LOG.error(e.getMessage(), e);
                }
            }
        }

        public void stop() {
            this.workerFlag = false;
        }

        /**
         *
         */
        private class FutureHandler<DATA> {

            /**
             * 处理结果
             */
            private Future future;

            /**
             * 原始数据
             */
            private DATA originData;

            /**
             * 创建时间点
             */
            private Long beginTs;

            /**
             * 最大处理时间点
             */
            private Long maxExecTs;

            public FutureHandler() {
            }

            public FutureHandler(DATA data, Future future) {
                this.originData = data;
                this.future = future;
                //当前时间
                this.beginTs = System.currentTimeMillis();
            }

            public Future getFuture() {
                return future;
            }

            public void setFuture(Future future) {
                this.future = future;
            }

            public DATA getOriginData() {
                return originData;
            }

            public void setOriginData(DATA originData) {
                this.originData = originData;
            }

            public Long getBeginTs() {
                return beginTs;
            }

            public void setBeginTs(Long beginTs) {
                this.beginTs = beginTs;
            }

            public Long getMaxExecTs() {
                return maxExecTs;
            }

            public void setMaxExecTs(Long maxExecTs) {
                this.maxExecTs = maxExecTs;
            }
        }
    }

}
