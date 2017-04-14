package win.sinno.concurrent.ballon;

import org.junit.Test;
import win.sinno.concurrent.balloon.Boss;
import win.sinno.concurrent.balloon.BossConfig;
import win.sinno.concurrent.balloon.IWokerFactory;
import win.sinno.concurrent.balloon.IWorker;

import java.util.Date;

/**
 * boss test
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/14 17:03
 */
public class BossTest {

    @Test
    public void testBoss() throws InterruptedException {

        IWokerFactory wokerFactory = new IWokerFactory() {

            public IWorker create() {
                return new IWorker<String>() {
                    @Override
                    public void deal(String s) {
                        System.out.println(new Date() + ":" + s);
                        try {
                            Thread.sleep(500l);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
            }
        };

        BossConfig bossConfig = new BossConfig();
        bossConfig.setMaxTotal(10);
        bossConfig.setMaxIdle(6);
        bossConfig.setMinIdle(2);
        bossConfig.setCheckTs(5000);
        bossConfig.setSoftCheck(true);
        bossConfig.setCheckIdleTs(10000);
//        bossConfig.set
        bossConfig.setBlockWhenExhausted(true);

        Boss<String> boss = new Boss<String>("test", wokerFactory, bossConfig);

        for (int i = 0; i < 20; i++) {
            try {
                boss.deal("" + i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Thread.sleep(20000l);

        for (int i = 0; i < 100; i++) {
            try {
                boss.deal("" + i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Thread.sleep(1000000l);

    }
}
