package win.sinno.concurrent.ant;

import win.sinno.concurrent.ant.custom.IDataHandlerWithoutRet;

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
public class DataMultiWorkerWithoutRet<DATA> implements Callable<Void> {


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
    private IDataHandlerWithoutRet<DATA> dataHandler;

    /**
     * 本地线程池
     */
    private final LocalExecutorPool localExecutorPool;


    public DataMultiWorkerWithoutRet(Collection<DATA> datas, DataSlicer<DATA> dataSlicer, int workerSize, IDataHandlerWithoutRet<DATA> dataHandler, LocalExecutorPool localExecutorPool) {
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

    public Void call() throws Exception {

        // 数据分片集合
        List<Collection<DATA>> dataSlices = dataSlicer.getSlice(datas, workerSize);


        Collection<Future<Void>> futures = new ArrayList<Future<Void>>();

        // 主控线程
        LocalExecutorPool.NamedExecutorService mainNamedExecutorService = localExecutorPool.getMainExecutorService();

        for (final Collection<DATA> datas1 : dataSlices) {

            Future<Void> f1 = mainNamedExecutorService.getExecutorService().submit(new Callable<Void>() {

                //主线程控制
                public Void call() throws Exception {

                    LocalExecutorPool.NamedExecutorService namedExecutorService = localExecutorPool.getAvailableExecutorService();

                    //线程池执行服务
                    ExecutorService executorService = namedExecutorService.getExecutorService();

                    try {
                        DataWorkerWithoutRet dataWorker = new DataWorkerWithoutRet(datas1, dataHandler);

                        Future<Void> future = executorService.submit(dataWorker);

                        future.get();

                    } finally {
                        //设置池为可用
                        localExecutorPool.setAvailableExecutorService(namedExecutorService);
                    }

                    return null;
                }

            });

            futures.add(f1);
        }

        for (Future<Void> f2 : futures) {
            //等待
            f2.get();
        }

        return null;
    }


}
