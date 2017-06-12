package win.sinno.concurrent.lock;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/6 15:22
 */
public class TwinsLockTest {

    @Test
    public void testLock() throws InterruptedException {
        final TwinsLock twinsLock = new TwinsLock();

        class Work extends Thread {
            @Override
            public void run() {
                try {
                    twinsLock.lock();
                    System.out.println(Thread.currentThread().getName() + ":begin");
                    Thread.sleep(3000l);
                    System.out.println(Thread.currentThread().getName() + ":end");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    twinsLock.unlock();
                }
            }
        }
        List<Work> threadList = new ArrayList<Work>();

        for (int i = 0; i < 5; i++) {
            threadList.add(new Work());
        }

        for (int i = 0; i < threadList.size(); i++) {
            Thread t = threadList.get(i);
            t.start();
        }

        Thread.sleep(1000000l);

    }
}
