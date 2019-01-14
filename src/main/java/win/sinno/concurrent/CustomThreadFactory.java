package win.sinno.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * custom thread factory
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/3/7 下午2:45
 */
public class CustomThreadFactory implements ThreadFactory {

  private static final AtomicInteger poolNumber = new AtomicInteger(1);
  private final ThreadGroup group;
  private final AtomicInteger threadNumber = new AtomicInteger(1);
  private final String namePrefix;


  public CustomThreadFactory(String name) {

    SecurityManager s = System.getSecurityManager();

    group = (s != null) ? s.getThreadGroup() :
        Thread.currentThread().getThreadGroup();

    namePrefix = "sinno-" + name + "-pool-" +
        poolNumber.getAndIncrement() +
        "-thread-";
  }

  @Override
  public Thread newThread(Runnable r) {

    Thread t = new Thread(group, r,
        namePrefix + threadNumber.getAndIncrement(),
        0);

    if (t.isDaemon()) {
      t.setDaemon(false);
    }
    if (t.getPriority() != Thread.NORM_PRIORITY) {
      t.setPriority(Thread.NORM_PRIORITY);
    }
    return t;
  }

}
