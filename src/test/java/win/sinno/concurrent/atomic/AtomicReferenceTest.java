package win.sinno.concurrent.atomic;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

/**
 * atomic reference
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/7 14:10
 */
public class AtomicReferenceTest {

    @Test
    public void test() {

        AtomicReference<User> atomicReference = new AtomicReference<User>();
        User conan = new User("conan", 9);
        atomicReference.set(conan);

        User maoli = new User("maoli", 18);
        atomicReference.compareAndSet(conan, maoli);

        System.out.println(atomicReference.get().getName());
        System.out.println(atomicReference.get().getOld());
    }

    static class User {

        private String name;

        private int old;

        public User(String name, int old) {
            this.name = name;
            this.old = old;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getOld() {
            return old;
        }

        public void setOld(int old) {
            this.old = old;
        }
    }
}
