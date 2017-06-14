package win.sinno.concurrent.atomic;

import org.junit.Test;

/**
 * atomic positive long
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/14 11:26
 */
public class AtomicPositiveLongTest {

    @Test
    public void test() {
        AtomicPositiveLong atomicPositiveLong = new AtomicPositiveLong();

        System.out.println(atomicPositiveLong.addAndGet(2));
        System.out.println(atomicPositiveLong.addAndGet(-1));
        System.out.println(atomicPositiveLong.addAndGet(-3));

        System.out.println(atomicPositiveLong.get());
    }
}
