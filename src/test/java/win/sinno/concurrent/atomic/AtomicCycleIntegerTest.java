package win.sinno.concurrent.atomic;

import org.junit.Test;

/**
 * 原子 循环整数测试
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/13 11:12
 */
public class AtomicCycleIntegerTest {

    @Test
    public void testIncrementAndGet() {
        AtomicCycleInteger aci = new AtomicCycleInteger(0, 10);
        for (int i = 0; i < 30; i++) {
            System.out.println(aci.incrementAndGet());
        }
    }

    @Test
    public void testGetAndDecrement() {
        AtomicCycleInteger aci = new AtomicCycleInteger(5, 3, 9);
        for (int i = 0; i < 30; i++) {
            System.out.println(aci.getAndDecrement());
        }
    }

    @Test
    public void testAddAndGet() {
        AtomicCycleInteger aci = new AtomicCycleInteger(3, 2, 9);
        for (int i = 0; i < 30; i++) {
            System.out.println(aci.addAndGet(1));
        }

        for (int i = 0; i < 30; i++) {
            System.out.println(aci.addAndGet(3));
        }
    }

    @Test
    public void testGetAndAdd() {
        AtomicCycleInteger aci = new AtomicCycleInteger(6, 4, 12);
        for (int i = 0; i < 30; i++) {
            System.out.println(aci.getAndAdd(1));
        }

        for (int i = 0; i < 30; i++) {
            System.out.println(aci.getAndAdd(3));
        }
    }
}
