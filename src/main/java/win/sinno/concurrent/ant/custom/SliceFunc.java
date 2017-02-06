package win.sinno.concurrent.ant.custom;

import java.util.Collection;
import java.util.List;

/**
 * 分段功能
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/10/24 上午11:50
 */
public interface SliceFunc<DATA> {

    /**
     * 分片
     *
     * @param sliceSize
     * @return
     */
    List<Collection<DATA>> slice(Collection<DATA> datas, int sliceSize);

}
