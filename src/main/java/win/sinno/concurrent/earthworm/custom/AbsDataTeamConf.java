package win.sinno.concurrent.earthworm.custom;

/**
 * 数据执行team抽象配置
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/2/3 下午1:44
 */
public abstract class AbsDataTeamConf<DATA> implements IDataTeamConf<DATA> {

    /**
     * 工人数量
     * <p>
     * Runtime.getRuntime().availableProcessors() + 1
     *
     * @return
     */
    @Override
    public int getWorkerNum() {
        return Runtime.getRuntime().availableProcessors() + 1;
    }

    @Override
    public int getQueueSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Boolean getUseTimeout() {
        return Boolean.FALSE;
    }

    @Override
    public Long getTimeout() {
        return 30000l;
    }
}
