package win.sinno.concurrent.balloon;

/**
 * worker
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/14 16:05
 */
public interface IWorker<DATA> {

    void deal(DATA data);
}
