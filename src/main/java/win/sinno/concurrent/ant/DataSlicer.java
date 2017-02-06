package win.sinno.concurrent.ant;

import win.sinno.concurrent.ant.custom.SliceFunc;

import java.util.Collection;
import java.util.List;

/**
 * 数据分片多线程处理器
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/10/24 上午11:24
 */
public class DataSlicer<DATA> {

    private SliceFunc<DATA> sliceFunc;

    public DataSlicer() {
    }

    public DataSlicer(SliceFunc<DATA> sliceFunc) {
        this.sliceFunc = sliceFunc;
    }

    /**
     * 获取分片集合
     *
     * @param datas     数据
     * @param sliceSize 分片数
     * @return
     */
    public List<Collection<DATA>> getSlice(Collection<DATA> datas, int sliceSize) {
        return sliceFunc.slice(datas, sliceSize);
    }


}
