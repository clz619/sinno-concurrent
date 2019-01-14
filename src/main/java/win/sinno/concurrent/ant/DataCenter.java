package win.sinno.concurrent.ant;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import win.sinno.concurrent.ant.custom.IDataHandler;
import win.sinno.concurrent.ant.custom.IDataHandlerWithoutRet;
import win.sinno.concurrent.ant.custom.SliceFunc;

/**
 * TODO 优化- 可以设置,不需要数据返回,执行完一个便移除，便于内存释放
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/10/25 上午11:13
 */
public class DataCenter {


  //本地执行线程池map
  private static Map<String, LocalExecutorPool> localExecutorPoolMap = new HashMap<String, LocalExecutorPool>();

  private DataCenter() {

  }

  private static class DataCenterHolder {

    private static final DataCenter HOLDER = new DataCenter();
  }

  public static DataCenter getInstance() {
    return DataCenterHolder.HOLDER;
  }

  public LocalExecutorPool addLocalExecutorPool(String name, int poolSize, int poolThreadNum) {
    // 设置本地线程池-
    LocalExecutorPool localExecutorPool = new LocalExecutorPool(poolSize, poolThreadNum);

    localExecutorPoolMap.put(name, localExecutorPool);

    return localExecutorPool;
  }

  //LOCK MAP

  private static Map<String, Object> LOCK_MAP = new HashMap<String, Object>();

  public LocalExecutorPool getLocalExecutorPool(String name, int workerSize, int threadNum) {

    LocalExecutorPool localExecutorPool = localExecutorPoolMap.get(name);

    if (localExecutorPool == null) {
      synchronized (this) {
        localExecutorPool = localExecutorPoolMap.get(name);
        if (localExecutorPool == null) {
          localExecutorPool = addLocalExecutorPool(name, workerSize, threadNum);
        }
      }
    }

    return localExecutorPool;
  }

  /**
   * 分批处理
   *
   * @param name 业务唯独线程池
   * @param datas 处理数据
   * @param workerSize 单个业务的线程池量
   * @param sliceFunc 分段功能
   * @param dataHandler 数据处理器
   * @param <DATA> 数据类型
   * @param <RET> 返回类型
   */
  public <DATA, RET> DataCollectionResult<DATA, RET> exec(String name, Collection<DATA> datas,
      int workerSize, SliceFunc<DATA> sliceFunc, IDataHandler<DATA, RET> dataHandler)
      throws Exception {

    LocalExecutorPool localExecutorPool = getLocalExecutorPool(name, workerSize, 0);

    DataMultiWorker<DATA, RET> dataMultiWorker = new DataMultiWorker<DATA, RET>(datas,
        new DataSlicer<DATA>(sliceFunc), workerSize, dataHandler, localExecutorPool);

    return dataMultiWorker.call();
  }


  /**
   * 执行不返回结果
   */
  public <DATA> void exec(String name, Collection<DATA> datas, int workerSize,
      SliceFunc<DATA> sliceFunc, IDataHandlerWithoutRet<DATA> dataHandler) throws Exception {

    LocalExecutorPool localExecutorPool = getLocalExecutorPool(name, workerSize, 0);

    DataMultiWorkerWithoutRet<DATA> dataMultiWorker = new DataMultiWorkerWithoutRet<DATA>(datas,
        new DataSlicer<DATA>(sliceFunc), workerSize, dataHandler, localExecutorPool);

    dataMultiWorker.call();

  }

  /**
   * 执行单条
   */
  @Deprecated
  private <DATA, RET> RET exec(DATA data) {

    //TODO add data single worker

    return null;
  }


}
