package win.sinno.concurrent.ant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.concurrent.ant.custom.IDataHandlerWithoutRet;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * 数据处理
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/10/24 下午5:55
 */
public class DataWorkerWithoutRet<DATA> implements Callable<Void> {

    private static final Logger LOG = LoggerFactory.getLogger("ant");

    /**
     * 数据
     */
    private Collection<DATA> datas;

    /**
     * 事件处理器
     */
    private IDataHandlerWithoutRet<DATA> dataHandler;


    public DataWorkerWithoutRet(Collection<DATA> datas, IDataHandlerWithoutRet<DATA> dataHandler) {
        this.datas = datas;
        this.dataHandler = dataHandler;
    }

    public Void call() throws Exception {


        for (DATA data : datas) {

            try {
                doOne(data);

            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }

        return null;
    }

    /**
     * 单个处理
     *
     * @param data
     * @return
     */
    public void doOne(DATA data) {
        dataHandler.handler(data);
    }
}
