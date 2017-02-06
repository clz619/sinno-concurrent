package win.sinno.concurrent.ant;

import win.sinno.concurrent.ant.custom.IDataHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 数据多线程处理器
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/10/24 下午5:28
 */
public class DataMultiWorker<DATA, RET> implements Callable<DataCollectionResult<DATA, RET>> {


    /**
     * 默认worker size
     */
    private static final int DEFAULT_WORK_SIZE = 1;

    /**
     * 处理器大小
     */
    private int workerSize;

    /**
     * 数据集合
     */
    private Collection<DATA> datas;

    /**
     * 数据分段处理器
     */
    private DataSlicer<DATA> dataSlicer;

    /**
     * 数据处理器
     */
    private IDataHandler<DATA, RET> dataHandler;

    /**
     * 本地线程池
     */
    private final LocalExecutorPool localExecutorPool;


    public DataMultiWorker(Collection<DATA> datas, DataSlicer<DATA> dataSlicer, int workerSize, IDataHandler<DATA, RET> dataHandler, LocalExecutorPool localExecutorPool) {
        this.datas = datas;
        this.dataSlicer = dataSlicer;

        if (workerSize > 0) {
            this.workerSize = workerSize;
        } else {
            this.workerSize = DEFAULT_WORK_SIZE;
        }

        //数据处理
        this.dataHandler = dataHandler;
        //本地线程池
        this.localExecutorPool = localExecutorPool;
    }

    public DataCollectionResult<DATA, RET> call() throws Exception {

        // 数据分片集合
        List<Collection<DATA>> dataSlices = dataSlicer.getSlice(datas, workerSize);

        DataCollectionResult<DATA, RET> ret = new DataCollectionResult<DATA, RET>();

        Collection<Future<DataCollectionResult<DATA, RET>>> futures = new ArrayList<Future<DataCollectionResult<DATA, RET>>>();

        // 主控线程
        LocalExecutorPool.NamedExecutorService mainNamedExecutorService = localExecutorPool.getMainExecutorService();

        for (final Collection<DATA> datas1 : dataSlices) {

            Future<DataCollectionResult<DATA, RET>> f1 = mainNamedExecutorService.getExecutorService().submit(new Callable<DataCollectionResult<DATA, RET>>() {

                //主线程控制
                public DataCollectionResult<DATA, RET> call() throws Exception {

                    LocalExecutorPool.NamedExecutorService namedExecutorService = localExecutorPool.getAvailableExecutorService();

                    //线程池执行服务
                    ExecutorService executorService = namedExecutorService.getExecutorService();

                    DataCollectionResult<DATA, RET> ret = null;

                    try {
                        DataWorker dataWorker = new DataWorker(datas1, dataHandler);

                        Future<DataCollectionResult<DATA, RET>> future = executorService.submit(dataWorker);

                        ret = future.get();

                    } finally {
                        //设置池为可用
                        localExecutorPool.setAvailableExecutorService(namedExecutorService);
                    }

                    return ret;
                }

            });

            futures.add(f1);
        }

        for (Future<DataCollectionResult<DATA, RET>> f2 : futures) {
            //获取返回结果
            DataCollectionResult<DATA, RET> ret2 = f2.get();
            ret.addDataCollectionResult(ret2);
        }

        return ret;
    }


}
