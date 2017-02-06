package win.sinno.concurrent.ant.custom;

/**
 * 数据处理器
 * <p>
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/10/25 上午11:46
 */
public interface IDataHandlerWithoutRet<DATA> {

    /**
     * 时间处理
     *
     * @param data
     * @return
     */
    void handler(DATA data);
}
