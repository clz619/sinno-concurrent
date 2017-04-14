package win.sinno.concurrent.balloon;

/**
 * worker factory
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/14 16:11
 */
public interface IWokerFactory<DATA> {

    IWorker<DATA> create();
}
