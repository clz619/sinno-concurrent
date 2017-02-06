package win.sinno.concurrent.earthworm;

import win.sinno.concurrent.earthworm.custom.IDataHandler;

import java.util.concurrent.Callable;

/**
 * 数据队列工作者
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/11/4 下午7:40
 */
public class DataQueueWorker<DATA> implements Callable<Void> {


    private DATA data;

    /**
     * 数据处理器
     */
    private IDataHandler<DATA> dataHandler;


    public DataQueueWorker(IDataHandler<DATA> dataHandler, DATA data) {
        this.dataHandler = dataHandler;
        this.data = data;
    }

    @Override
    public Void call() throws Exception {
        dataHandler.handler(data);
        return null;
    }
}
