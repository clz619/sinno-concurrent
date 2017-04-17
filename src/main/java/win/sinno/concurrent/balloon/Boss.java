package win.sinno.concurrent.balloon;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.concurrent.CustomThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/14 16:17
 */
public class Boss<DATA> {

    private static final Logger LOG = LoggerFactory.getLogger(Boss.class);

    private String name;

    public int maxTotal = 8;

    private int maxIdle = 8;

    private int minIdle = 0;

    private boolean blockWhenExhausted = true;

    private boolean isSoftCheck = true;

    private long checkTs = 1000 * 30;

    private long checkIdleTs = 1000 * 60 * 10;

    private IWokerFactory<DATA> workerFactory;

    private GenericObjectPool<IWorker<DATA>> pool;

    private ExecutorService service;

    public Boss(String name, IWokerFactory<DATA> workerFactory) {
        this(name, workerFactory, null);
    }

    public Boss(String name, IWokerFactory<DATA> workerFactory, BossConfig bossConfig) {
        this.name = name;
        this.workerFactory = workerFactory;

        if (bossConfig != null) {
            this.maxTotal = bossConfig.getMaxTotal();
            this.maxIdle = bossConfig.getMaxIdle();
            this.minIdle = bossConfig.getMinIdle();
            this.blockWhenExhausted = bossConfig.isBlockWhenExhausted();
            this.checkTs = bossConfig.getCheckTs();
            this.isSoftCheck = bossConfig.isSoftCheck();
            this.checkIdleTs = bossConfig.getCheckIdleTs();
        }

        PooledObjectFactory<IWorker<DATA>> factory = new PooledWorkerFactory<DATA>(workerFactory);
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();

        config.setMaxTotal(this.maxTotal);
        config.setMaxIdle(this.maxIdle);
        config.setMinIdle(this.minIdle);
        config.setBlockWhenExhausted(this.blockWhenExhausted);

        config.setTimeBetweenEvictionRunsMillis(checkTs);

        // 检测时间
        if (isSoftCheck) {
            config.setSoftMinEvictableIdleTimeMillis(checkIdleTs);
        } else {
            config.setMinEvictableIdleTimeMillis(checkIdleTs);
        }

        config.setLifo(true);
        config.setTestWhileIdle(true);
        config.setTestOnCreate(true);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setJmxEnabled(false);
        pool = new GenericObjectPool<IWorker<DATA>>(factory, config);

        service = Executors.newCachedThreadPool(new CustomThreadFactory("balloon-[" + name + "]"));
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public boolean isBlockWhenExhausted() {
        return blockWhenExhausted;
    }

    public void setBlockWhenExhausted(boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    public boolean isSoftCheck() {
        return isSoftCheck;
    }

    public void setSoftCheck(boolean softCheck) {
        isSoftCheck = softCheck;
    }

    public long getCheckIdleTs() {
        return checkIdleTs;
    }

    public void setCheckIdleTs(long checkIdleTs) {
        this.checkIdleTs = checkIdleTs;
    }

    public void deal(final DATA data) throws Exception {

        final IWorker<DATA> worker = pool.borrowObject();

        Runnable dealer = new Runnable() {

            @Override
            public void run() {

                try {
                    worker.deal(data);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                } finally {
                    if (worker != null) {
                        pool.returnObject(worker);
                    }
                }

            }
        };

        service.execute(dealer);

    }

}
