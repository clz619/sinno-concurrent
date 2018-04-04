package win.sinno.concurrent.deadloop;

import java.util.HashMap;
import java.util.UUID;
import org.junit.Test;

/**
 * win.sinno.concurrent.deadloop.HashMapDeadTest
 *
 * @author chenlizhong@qipeng.com
 * @date 2018/3/28
 */
public class HashMapDeadTest {

  /**
   * jdk1.8修改了resize() 方法 不会造成node循环指向造成死循环
   */
  @Test
  public void test() throws InterruptedException {
    final HashMap<String, String> map = new HashMap<>();
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < 10000; i++) {
          new Thread(new Runnable() {
            @Override
            public void run() {
              map.put(UUID.randomUUID().toString(), "");
              System.out.println("done");
            }
          }, "ftf" + i).start();
        }
      }
    });

    t.start();
    t.join();

    System.out.println("finish");
  }

}
