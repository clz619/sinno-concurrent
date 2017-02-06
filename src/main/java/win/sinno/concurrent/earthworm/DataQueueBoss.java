package win.sinno.concurrent.earthworm;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    private Queue<DATA> queue = new ConcurrentLinkedQueue<DATA>();

    /**
     * 工作者数量
     */
    private int workerCount;


    public DataQueueBoss(int workerCount) throws IllegalArgumentException {

        if (workerCount <= 0) {
            throw new IllegalArgumentException("the boss can not no worker ,because the task need worker handler.your parameter is:[" + workerCount + "]");
        }

        //工作者数量
        this.workerCount = workerCount;


    }

    /**
     * boss 分配任务
     *
     * @param data
     */
    void dispathTask(DATA data) {
        this.queue.add(data);
    }

    /**
     * boss 分配任务集合
     *
     * @param datas
     */
    void dispathTasks(Collection<DATA> datas) {
        this.queue.addAll(datas);
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

    /**
     * 获取一个任务，若没有则返回null
     *
     * @return
     */
    DATA getOneTask() {
        return queue.poll();
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

}
