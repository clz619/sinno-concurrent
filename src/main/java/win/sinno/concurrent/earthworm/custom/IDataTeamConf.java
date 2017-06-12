package win.sinno.concurrent.earthworm.custom;

/**
 * 数据团队配置
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/1/17 下午3:45
 */
public interface IDataTeamConf<DATA> {

    /**
     * 获取队伍名称 ，将数据进行流水线分配。
     * 根据data的特性分配到特定team
     *
     * @param data
     * @return
     */
    String getTeamName(DATA data);

    /**
     * 获取worker 数量，获取team的工人数，即team的线程数
     *
     * @return
     */
    int getWorkerNum();

    /**
     * 队列大小，队列会阻塞
     *
     * @return
     */
    int getQueueSize();

    /**
     * 获取数据处理器
     *
     * @return
     */
    IDataHandler<DATA> getDataHandler();

    /**
     * 获取是否超时
     *
     * @return
     */
    Boolean getUseTimeout();

    /**
     * 获取超时时间，单位：毫秒
     *
     * @return
     */
    Long getTimeout();

}
