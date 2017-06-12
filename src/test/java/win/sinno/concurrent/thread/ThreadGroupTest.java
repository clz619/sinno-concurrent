package win.sinno.concurrent.thread;

import org.junit.Test;

import java.util.Date;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/7 16:38
 */
public class ThreadGroupTest {

    @Test
    public void test() {
        ThreadGroup tg = new ThreadGroup("searcher");

        for (int i = 0; i < 3; i++) {
            String name = "s" + i;
            Searcher searcher = new Searcher(name);
            Thread thread = new Thread(tg, searcher, name);
            thread.start();
        }

        System.out.println("activeCount:" + tg.activeCount());
        System.out.println("activeGroupCount:" + tg.activeGroupCount());

        Thread[] tArray = new Thread[tg.activeCount()];
        tg.enumerate(tArray);

        for (Thread t : tArray) {
            if (t != null) {
                System.out.println(t.getName());
            }
        }

        try {
            Thread.sleep(11000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        tg.interrupt();

        try {
            Thread.sleep(10000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Searcher implements Runnable {

        private String name;

        public Searcher(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println(name + " search in " + new Date());
                    Thread.sleep(3000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

    }
}
