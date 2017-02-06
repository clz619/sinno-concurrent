package win.sinno.concurrent.ant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.concurrent.ant.custom.IDataHandler;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * 数据处理
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/10/24 下午5:55
 */
public class DataWorker<DATA, RET> implements Callable<DataCollectionResult<DATA, RET>> {

    private static final Logger LOG = LoggerFactory.getLogger("ant");

    /**
     * 数据
     */
    private Collection<DATA> datas;

    /**
     * 事件处理器
     */
    private IDataHandler<DATA, RET> dataHandler;


    public DataWorker(Collection<DATA> datas, IDataHandler<DATA, RET> dataHandler) {
        this.datas = datas;
        this.dataHandler = dataHandler;
    }

    public DataCollectionResult<DATA, RET> call() throws Exception {

        DataCollectionResult<DATA, RET> ret = new DataCollectionResult<DATA, RET>();

        for (DATA data : datas) {

            try {
                //成功数据
                ret.addSuccessData(new DataCollectionResult.DataResult<DATA, RET>(data, doOne(data)));

            } catch (Exception e) {

                //异常数据
                ret.addExceptionData(new DataCollectionResult.DataResult<DATA, String>(data, e.getMessage()));

                LOG.error(e.getMessage(), e);
            }
        }

        return ret;
    }

    /**
     * 单个处理
     *
     * @param data
     * @return
     */
    public RET doOne(DATA data) {
        return dataHandler.handler(data);
    }
}
