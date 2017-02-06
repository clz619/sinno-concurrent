package win.sinno.concurrent.earthworm;

import org.apache.commons.collections4.CollectionUtils;
import win.sinno.concurrent.earthworm.custom.IDataTeamConf;
import win.sinno.concurrent.earthworm.custom.IDataHandler;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据队列中心默认可支撑8条队列
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/11/7 上午9:54
 */
public class DataQueueCenter {

    private final static Object CREATE_TEAM_LOCK = new Object();

    private final static int DEFAULT_QUEUE_NUM = 8;

    public final static int FIXED_THREAD_POOL_FLAG = 0;

    public final static int CACHED_THREAD_POOL_FLAG = 1;

    private int queueNum;

    /**
     * 数据队列
     */
    private Map<String, DataQueueTeam> dataQueueTeamMap = new ConcurrentHashMap<String, DataQueueTeam>();

    private ExecutorService executorService;


    public DataQueueCenter() {
        this(DEFAULT_QUEUE_NUM);
    }

    public DataQueueCenter(int queueNum) {
        //默认为固定的线程池
        this(queueNum, CACHED_THREAD_POOL_FLAG);
    }

    public DataQueueCenter(int queueNum, int poolType) {
        this.queueNum = queueNum;
        if (CACHED_THREAD_POOL_FLAG == poolType) {
            executorService = Executors.newCachedThreadPool();
        } else {
            executorService = Executors.newFixedThreadPool(queueNum);
        }
    }


    public <DATA> DataQueueTeam<DATA> getDataQueueTeam(String name) {
        return dataQueueTeamMap.get(name);
    }

    /**
     * 创建一个数据队列团队，没有超时
     *
     * @param teamName
     * @param workerNum
     * @param dataHandler
     * @param <DATA>
     */
    public <DATA> DataQueueTeam<DATA> createDataQueueTeam(String teamName, int workerNum, IDataHandler<DATA> dataHandler) {

        return createDataQueueTeam(teamName, workerNum, dataHandler, Boolean.FALSE, 30000l);
    }

    /**
     * 创建队列
     *
     * @param teamName
     * @param workerNum
     * @param dataHandler
     * @param useTimeout
     * @param timeout
     * @param <DATA>
     * @return
     */
    public <DATA> DataQueueTeam<DATA> createDataQueueTeam(String teamName, int workerNum, IDataHandler<DATA> dataHandler, Boolean useTimeout, Long timeout) {

        //队列
        DataQueueTeam<DATA> dataQueueTeam = dataQueueTeamMap.get(teamName);

        if (dataQueueTeam == null) {

            synchronized (CREATE_TEAM_LOCK) {
                //创建队伍锁

                dataQueueTeam = dataQueueTeamMap.get(teamName);

                if (dataQueueTeam == null) {

                    if (workerNum < 1 || dataHandler == null) {
                        throw new IllegalArgumentException("data queue team name:[" + teamName + "],workerNum:[" + workerNum + "],data handler:[" + dataHandler + "] param is not valid.");
                    }

                    //数据队列 - 添加超时选项
                    dataQueueTeam = new DataQueueTeam(teamName, new DataQueueBoss(workerNum), dataHandler, useTimeout, timeout);

                    //数据队列的队伍map
                    dataQueueTeamMap.put(teamName, dataQueueTeam);

                    executorService.submit(dataQueueTeam);
                }
            }
        }

        return dataQueueTeam;

    }

    /**
     * 创建数据队列team
     *
     * @param teamName
     * @param dataTeamConf
     * @param <DATA>
     * @return
     */
    public <DATA> DataQueueTeam<DATA> createDataQueueTeam(String teamName, IDataTeamConf<DATA> dataTeamConf) {

        //创建数据队列team
        return createDataQueueTeam(teamName, dataTeamConf.getWorkerNum(), dataTeamConf.getDataHandler(), dataTeamConf.getUseTimeout(), dataTeamConf.getTimeout());
    }


    /**
     * 添加任务
     *
     * @param teamName 任务队列名
     * @param data     单个任务
     * @param <DATA>
     * @throws IllegalArgumentException
     */
    public <DATA> void addTask(String teamName, DATA data) throws IllegalArgumentException {
        //数据处理组
        DataQueueTeam<DATA> dataQueueTeam = getDataQueueTeam(teamName);

        if (dataQueueTeam == null) {
            //组为null，抛出错误
            throw new IllegalArgumentException("data queue team name:" + teamName + " is not exist.");
        }

        //增加处理组
        dataQueueTeam.addTask(data);

    }

    /**
     * 添加任务
     *
     * @param teamName 任务队列名
     * @param datas    任务集合
     * @param <DATA>
     * @throws IllegalArgumentException
     */
    public <DATA> void addTasks(String teamName, Collection<DATA> datas) throws IllegalArgumentException {
        //数据处理组
        DataQueueTeam<DATA> dataQueueTeam = getDataQueueTeam(teamName);

        if (dataQueueTeam == null) {
            //组为null，抛出错误
            throw new IllegalArgumentException("data queue team name:" + teamName + " is not exist.");
        }

        //增加处理组
        dataQueueTeam.addTasks(datas);

    }

    /**
     * @param data
     * @param dataTeamConf
     * @param <DATA>
     * @throws IllegalArgumentException
     */
    public <DATA> void addTask(DATA data, IDataTeamConf<DATA> dataTeamConf) throws IllegalArgumentException {
        String teamName = dataTeamConf.getTeamName(data);

        int workerNum = dataTeamConf.getWorkerNum();
        IDataHandler<DATA> dataHandler = dataTeamConf.getDataHandler();

        //是否使用超时
        Boolean useTimeout = dataTeamConf.getUseTimeout();
        //超时
        Long timeout = dataTeamConf.getTimeout();

        //数据处理组
        DataQueueTeam<DATA> dataQueueTeam = createDataQueueTeam(teamName, workerNum, dataHandler, useTimeout, timeout);

        if (dataQueueTeam == null) {
            //组为null，抛出错误
            throw new IllegalArgumentException("data queue team name:" + teamName + " is not exist.");
        }

        dataQueueTeam.addTask(data);
    }


    /**
     * @param datas
     * @param dataTeamConf
     * @param <DATA>
     * @throws IllegalArgumentException
     */
    public <DATA> void addTasks(Collection<DATA> datas, IDataTeamConf<DATA> dataTeamConf) throws IllegalArgumentException {
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }

        for (DATA data : datas) {
            addTask(data, dataTeamConf);
        }
    }


}
