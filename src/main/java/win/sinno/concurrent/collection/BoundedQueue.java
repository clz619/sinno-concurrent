package win.sinno.concurrent.collection;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 有界队列
 * <p>
 * 当队列为空时，队列的获取操作将会阻塞获取线程，直到队列中有新增元素；
 * 当队列已满时，队列的插入操作将会阻塞插入线程，直到队列出现"空位"
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/7 09:41
 */
public class BoundedQueue<T> {

    private Object[] items;

    private int addIndex;

    private int removeIndex;

    private int count;

    private Lock lock = new ReentrantLock();

    private Condition notEmpty = lock.newCondition();

    private Condition notFull = lock.newCondition();

    public BoundedQueue(int size) {
        items = new Object[size];
    }

    public void add(T t) throws InterruptedException {
        lock.lock();

        try {
            while (count == items.length) {
                notFull.await();
            }
            items[addIndex] = t;
            if (++addIndex == items.length) {
                addIndex = 0;
            }
            ++count;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T remove() throws InterruptedException {
        lock.lock();

        try {
            while (count == 0) {
                notEmpty.await();
            }
            Object x = items[removeIndex];
            if (++removeIndex == items.length) {
                removeIndex = 0;
            }
            --count;
            notFull.signal();
            return (T) x;
        } finally {
            lock.unlock();
        }
    }

}
