package win.sinno.concurrent.ant;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 本地多线程池,包含多个线程池，根据业务及批次进行分批执行
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/10/24 下午6:28
 */
public class LocalExecutorPool {

    private static final Logger LOG = LoggerFactory.getLogger("ant");

    private static final int DEFAULT_POOL_SIZE = 4;

    //选线程执行map
    private Map<String, ExecutorService> executorServiceMap = new ConcurrentHashMap<String, ExecutorService>();

    //正在进行中
    private Queue<NamedExecutorService> doingExecutorServiceQueue = new ConcurrentLinkedQueue<NamedExecutorService>();

    //等待执行
    private Queue<NamedExecutorService> waitExecutorServiceQueue = new ConcurrentLinkedQueue<NamedExecutorService>();

    private Semaphore semaphore;

    private NamedExecutorService mainExecutorService;

    //池大小,(默认为4)
    private int poolSize;

    /**
     * 获取有效池-锁
     */
    private Object GET_AVAILABLE_EXEC_LOCK = new Object();

    /**
     * 设置有效池-锁
     */
    private Object SET_AVAILABLE_EXEC_LOCK = new Object();

    public LocalExecutorPool() {
        //池大小为有效核心数
        this(DEFAULT_POOL_SIZE, Runtime.getRuntime().availableProcessors());
    }

    public LocalExecutorPool(int poolSize) {
        //池大小为有效核心数
        this(poolSize, Runtime.getRuntime().availableProcessors());
    }

    public LocalExecutorPool(int poolSize, int threadNum) {
        if (poolSize < 1) {
            //非法参数
            throw new IllegalArgumentException("pool size:" + poolSize + " is illegal argument");
        }
        if (threadNum < 1) {
            //非法参数
            threadNum = Runtime.getRuntime().availableProcessors();
        }

        this.poolSize = poolSize;

        for (int i = 0; i < poolSize; i++) {
            String key = String.valueOf(i);

            addExecutorService(key, threadNum);
        }

        //信号量为线程池数量-
        semaphore = new Semaphore(poolSize);

        //使用缓存队列
//        ExecutorService executorService = Executors.newCachedThreadPool(new DataThreadFactory("main"));
//        mainExecutorService = new NamedExecutorService("main", executorService);

        //主控线程数以最大的线程池数量来限定
        mainExecutorService = new NamedExecutorService("main", Executors.newFixedThreadPool(poolSize, new DataThreadFactory("main")));

        LOG.info("new main NamedExecutorService name:{},poolSize:{}", new Object[]{"main", poolSize});
    }


    /**
     * 添加线程服务
     *
     * @param name
     * @param threadNum
     */
    private synchronized void addExecutorService(String name, int threadNum) {

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum, new DataThreadFactory(name));

        executorServiceMap.put(name, executorService);

        waitExecutorServiceQueue.add(new NamedExecutorService(name, executorService));

        LOG.info("add executor service name:{},thread num:{}", new Object[]{name, threadNum});
    }

    /**
     * 获取执行服务
     *
     * @param name
     * @return
     */
    public ExecutorService getExecutorService(String name) {
        return executorServiceMap.get(name);
    }

    /**
     * 获取线程池名称
     *
     * @return
     */
    public Set<String> getExecutorNames() {
        return executorServiceMap.keySet();
    }

    /**
     * 获取主线程池执行器
     *
     * @return
     */
    public NamedExecutorService getMainExecutorService() {
        return mainExecutorService;
    }

    /**
     * 获取有效执行服务-//对于类型单独增加处理线程池-指定处理数据，防止数据处理过慢
     *
     * @return
     */
    public NamedExecutorService getAvailableExecutorService() throws InterruptedException {

        NamedExecutorService namedExecutorService = null;

        synchronized (GET_AVAILABLE_EXEC_LOCK) {

            semaphore.acquire();

            while (namedExecutorService == null) {
                //命名的线程池服务为空
                //从等待处理服务队列中获取处理服务
                namedExecutorService = waitExecutorServiceQueue.poll();

                try {
                    if (namedExecutorService == null) {
                        //如果为空则休眠10ms
                        Thread.sleep(10l);
                    }
                } catch (InterruptedException e) {
                    //ingore
                }

            }

            //丢入正在处理服务队列
            doingExecutorServiceQueue.add(namedExecutorService);

            LOG.info("get name:{} executor service for doing", new Object[]{namedExecutorService.getName()});

            //执行服务
            return namedExecutorService;
        }

    }

    /**
     * 设置有效执行服务
     *
     * @param namedExecutorService
     */
    public void setAvailableExecutorService(NamedExecutorService namedExecutorService) {

        synchronized (SET_AVAILABLE_EXEC_LOCK) {

            boolean flag = doingExecutorServiceQueue.remove(namedExecutorService);

            if (flag) {

                waitExecutorServiceQueue.add(namedExecutorService);

                LOG.info("set name:{} executor service for available", new Object[]{namedExecutorService.getName()});

                //释放信号量
                semaphore.release();
            }
        }

    }

    /**
     * 被命名的线程池服务
     */
    static class NamedExecutorService {

        private String name;
        private ExecutorService executorService;

        public NamedExecutorService(String name, ExecutorService executorService) throws IllegalArgumentException {

            if (StringUtils.isEmpty(name)) {
                LOG.error("name can not null");
                throw new IllegalArgumentException("name can not null");
            }
            if (executorService == null) {
                LOG.error("executor service can not null");
                throw new IllegalArgumentException("executor service can not null");
            }

            this.name = name;
            this.executorService = executorService;
        }

        public String getName() {
            return name;
        }

        public ExecutorService getExecutorService() {
            return executorService;
        }

        @Override
        public boolean equals(Object obj) {

            if (obj == null) {
                return false;
            }

            if (!(obj instanceof NamedExecutorService)) {
                return false;
            }

            if (this.hashCode() != obj.hashCode()) {
                return false;
            }

            NamedExecutorService other = (NamedExecutorService) obj;

            //名称是否相等
            return this.getName().equals(other.getName());

        }

        @Override
        public int hashCode() {
            int hash = 17;
            if (name != null) {
                hash = hash * 31 + name.hashCode() - 1;
            }
            return hash;
        }
    }

}
