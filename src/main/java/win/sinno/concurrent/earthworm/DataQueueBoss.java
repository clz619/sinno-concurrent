package win.sinno.concurrent.earthworm;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 数据队列boss
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/11/4 下午7:36
 */
public class DataQueueBoss<DATA> {

    /**
     * 队列
     */
    private BlockingQueue<DATA> queue;

    /**
     * 工作者数量
     */
    private int workerCount;

    private int queueSize;


    public DataQueueBoss(int workerCount) throws IllegalArgumentException {

        this(workerCount, Integer.MAX_VALUE);

    }

    public DataQueueBoss(int workerCount, int queueSize) throws IllegalArgumentException {

        if (workerCount <= 0) {
            throw new IllegalArgumentException("the boss can not no worker ,because the task need worker handler.your parameter is:[" + workerCount + "]");
        }

        if (queueSize <= 0) {
            throw new IllegalArgumentException("the boss can not queue ,because the task need worker handler.your parameter queueSize is:[" + queueSize + "]");
        }

        //工作者数量
        this.workerCount = workerCount;
        this.queueSize = queueSize;
        this.queue = new LinkedBlockingQueue<DATA>(queueSize);
    }

    /**
     * boss 分配任务
     *
     * @param data
     */
    void dispathTask(DATA data) throws InterruptedException {
        this.queue.put(data);
    }

    /**
     * boss 分配任务集合
     *
     * @param datas
     */
    void dispathTasks(Collection<DATA> datas) throws InterruptedException {
        if (CollectionUtils.isNotEmpty(datas)) {
            for (DATA data : datas) {
                dispathTask(data);
            }
        }
    }

    /**
     * 获取第一个task，不从队列中移除数据
     *
     * @return
     */
    DATA getFirstTask() {
        return this.queue.peek();
    }

    /**
     * 队列长度
     *
     * @return
     */
    int getTaskCount() {
        return this.queue.size();
    }

    void clearTask() {
        this.queue.clear();
    }

    /**
     * 获取一个任务，若没有则返回null
     *
     * @return
     */
    DATA getOneTask() throws InterruptedException {
        return queue.take();
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

}
