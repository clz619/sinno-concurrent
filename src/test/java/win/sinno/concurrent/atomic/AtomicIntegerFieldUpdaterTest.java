package win.sinno.concurrent.atomic;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * field updater
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/7 14:17
 */
public class AtomicIntegerFieldUpdaterTest {


    @Test
    public void test() {

        AtomicIntegerFieldUpdater<User> a = AtomicIntegerFieldUpdater.newUpdater(User.class, "old");
        User conan = new User("conan", 9);
        System.out.println(a.getAndIncrement(conan));

        System.out.println(a.get(conan));

    }

    static class User {

        private String name;

        //必需是 public volatile 的参数 才能使用Atomic*FieldUpdater
        //java.lang.IllegalArgumentException: Must be volatile type
        public volatile int old;

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
