package win.sinno.concurrent.lock;

import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * link block
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/12 10:21
 */
public class LinkedBlockingQueueTest {

    @Test
    public void testBlocking() throws InterruptedException {

        LinkedBlockingQueue<Integer> linkedBlockingQueue = new LinkedBlockingQueue<Integer>(10);

        for (int i = 0; i < 20; i++) {
            Integer it = new Integer(i);
            linkedBlockingQueue.add(it);
            System.out.println("add integer it:" + i);
        }
    }
}
