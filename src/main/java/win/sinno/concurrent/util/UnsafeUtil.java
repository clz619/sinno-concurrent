package win.sinno.concurrent.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/13 11:27
 */
public class UnsafeUtil {

    public static Unsafe getUnsafe() throws IllegalAccessException, NoSuchFieldException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);

        return (Unsafe) f.get(null);
    }

}
