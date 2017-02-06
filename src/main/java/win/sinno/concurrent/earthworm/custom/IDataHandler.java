package win.sinno.concurrent.earthworm.custom;

/**
 * 数据处理器
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/11/4 下午7:44
 */
public interface IDataHandler<DATA> {
    /**
     * 处理数据
     *
     * @param data
     */
    void handler(DATA data) throws InterruptedException;
}
