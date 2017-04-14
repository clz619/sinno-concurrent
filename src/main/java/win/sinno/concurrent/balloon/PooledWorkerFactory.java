package win.sinno.concurrent.balloon;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * pooled worker factory
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/14 16:08
 */
public class PooledWorkerFactory<DATA> implements PooledObjectFactory<IWorker<DATA>> {

//    private static final Logger LOG = LoggerFactory.getLogger("pooledFactory");

    private IWokerFactory<DATA> wokerFactory;

    private AtomicInteger makeCount = new AtomicInteger();

    private AtomicInteger destroyCount = new AtomicInteger();

    public PooledWorkerFactory(IWokerFactory<DATA> wokerFactory) {
        this.wokerFactory = wokerFactory;
    }

    @Override
    public PooledObject<IWorker<DATA>> makeObject() throws Exception {
//        LOG.info("makeObject.{}", makeCount.incrementAndGet());

        IWorker<DATA> worker = wokerFactory.create();
        PooledObject<IWorker<DATA>> p = new DefaultPooledObject<IWorker<DATA>>(worker);
        return p;
    }

    @Override
    public void destroyObject(PooledObject<IWorker<DATA>> p) throws Exception {

//        LOG.info("destroy.{}", destroyCount.incrementAndGet());
    }

    @Override
    public boolean validateObject(PooledObject<IWorker<DATA>> p) {
        return true;
    }


    @Override
    public void activateObject(PooledObject<IWorker<DATA>> p) throws Exception {
        //
    }

    @Override
    public void passivateObject(PooledObject<IWorker<DATA>> p) throws Exception {
        //
    }
}
