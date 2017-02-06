package win.sinno.concurrent.ant;

import win.sinno.concurrent.ant.custom.SliceFunc;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 默认分片功能器
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/10/24 上午11:48
 */
public class DefaultSliceFunc<DATA> implements SliceFunc<DATA> {

    public DefaultSliceFunc() {
    }

    /**
     * 分片
     *
     * @param datas
     * @param sliceSize
     * @return
     */
    public List<Collection<DATA>> slice(Collection<DATA> datas, int sliceSize) {

        if (CollectionUtils.isEmpty(datas)) {
            //集合
            return Collections.emptyList();
        }

        //集合尺寸
        int collectionSize = datas.size();

        List<Collection<DATA>> ret = new ArrayList<Collection<DATA>>();

        if (collectionSize <= sliceSize) {

            //集合数据
            // collection size
            for (DATA d : datas) {

                Collection<DATA> data = new ArrayList<DATA>();

                data.add(d);

                ret.add(data);

            }

        } else {

            for (int s = 0; s < sliceSize; s++) {
                Collection<DATA> c = new ArrayList<DATA>();
                ret.add(c);
            }

            int i = 0;

            for (DATA d : datas) {

                if (i >= sliceSize) {
                    i = 0;
                }

                Collection<DATA> collection = ret.get(i);

                collection.add(d);

                i++;
            }
        }

        return ret;
    }


}
