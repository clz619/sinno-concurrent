package win.sinno.concurrent.ant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * 数据处理结果
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/10/24 下午6:05
 */
public class DataCollectionResult<DATA, RET> {
    private Collection<DataResult<DATA, String>> exceptionDatas;

    private Collection<DataResult<DATA, RET>> successDatas;

    public DataCollectionResult() {
        exceptionDatas = new ArrayList<DataResult<DATA, String>>();
        successDatas = new ArrayList<DataResult<DATA, RET>>();
    }

    public Collection<DataResult<DATA, String>> getExceptionDatas() {
        return Collections.unmodifiableCollection(exceptionDatas);
    }

    public Collection<DataResult<DATA, RET>> getSuccessDatas() {
        return Collections.unmodifiableCollection(successDatas);
    }

    public void addExceptionData(DataResult<DATA, String> data) {
        exceptionDatas.add(data);
    }

    public void addAllExceptionData(Collection<DataResult<DATA, String>> datas) {
        exceptionDatas.addAll(datas);
    }

    public void addSuccessData(DataResult<DATA, RET> dataResult) {
        successDatas.add(dataResult);
    }

    //增加所有成功的数据
    public void addAllSuccessData(Collection<DataResult<DATA, RET>> dataResults) {
        successDatas.addAll(dataResults);
    }

    public void addDataCollectionResult(DataCollectionResult<DATA, RET> dataCollectionResult) {
        this.exceptionDatas.addAll(dataCollectionResult.getExceptionDatas());
        this.successDatas.addAll(dataCollectionResult.getSuccessDatas());
    }


    /**
     * 单数据处理结果
     *
     * @param <DATA>
     * @param <RET>
     */
    public static class DataResult<DATA, RET> {
        private DATA data;
        private RET ret;

        public DataResult(DATA data, RET ret) {
            this.data = data;
            this.ret = ret;
        }

        public DATA getData() {
            return data;
        }

        public void setData(DATA data) {
            this.data = data;
        }

        public RET getRet() {
            return ret;
        }

        public void setRet(RET ret) {
            this.ret = ret;
        }
    }
}
