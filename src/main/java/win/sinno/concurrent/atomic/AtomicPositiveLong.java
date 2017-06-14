package win.sinno.concurrent.atomic;

import org.apache.commons.lang3.Validate;
import sun.misc.Unsafe;
import win.sinno.concurrent.util.UnsafeUtil;

/**
 * 原子循环整型
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/13 10:47
 */
public class AtomicPositiveLong extends Number implements java.io.Serializable {

    private static final Unsafe unsafe;
    private static final long valueOffset;

    static {
        try {
            unsafe = UnsafeUtil.getUnsafe();
            valueOffset = unsafe.objectFieldOffset(AtomicPositiveLong.class.getDeclaredField("value"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private volatile long value;


    public AtomicPositiveLong() {
    }

    public AtomicPositiveLong(int initValue) {
        this.value = initValue;
        Validate.isTrue(initValue >= 0, "initValue:[" + initValue + "] is not positive number");
    }


    /**
     * Gets the current value.
     *
     * @return the current value
     */
    public final long get() {
        return value;
    }

    /**
     * Sets to the given value.
     *
     * @param newValue the new value
     */
    public final void set(long newValue) {
        Validate.isTrue(newValue >= 0, "newValue:[" + newValue + "] is not positive number");

        value = newValue;
    }

    /**
     * Eventually sets to the given value.
     *
     * @param newValue the new value
     * @since 1.6
     */
    public final void lazySet(long newValue) {
        Validate.isTrue(newValue >= 0, "newValue:[" + newValue + "] is not positive number");

        unsafe.putOrderedLong(this, valueOffset, newValue);
    }

    /**
     * Atomically sets to the given value and returns the old value.
     *
     * @param newValue the new value
     * @return the previous value
     */
    public final long getAndSet(long newValue) {
        Validate.isTrue(newValue >= 0, "newValue:[" + newValue + "] is not positive number");

        for (; ; ) {
            long current = get();
            if (compareAndSet(current, newValue))
                return current;
        }
    }

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     *
     * @param expect the expected value
     * @param update the new value
     * @return true if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public final boolean compareAndSet(long expect, long update) {
        return unsafe.compareAndSwapLong(this, valueOffset, expect, update);
    }

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     * <p>
     * <p>May <a href="package-summary.html#Spurious">fail spuriously</a>
     * and does not provide ordering guarantees, so is only rarely an
     * appropriate alternative to {@code compareAndSet}.
     *
     * @param expect the expected value
     * @param update the new value
     * @return true if successful.
     */
    public final boolean weakCompareAndSet(long expect, long update) {
        return unsafe.compareAndSwapLong(this, valueOffset, expect, update);
    }

    /**
     * Atomically increments by one the current value.
     *
     * @return the previous value
     */
    public final long getAndIncrement() {
        for (; ; ) {
            long current = get();
            long next = current + 1;

            if (compareAndSet(current, next))
                return current;
        }
    }

    /**
     * Atomically decrements by one the current value.
     *
     * @return the previous value
     */
    public final long getAndDecrement() {
        for (; ; ) {
            long current = get();
            long next = current - 1;

            if (next < 0) {
                return -1;
            }

            if (compareAndSet(current, next))
                return current;
        }
    }

    /**
     * Atomically adds the given value to the current value.
     *
     * @param delta the value to add
     * @return the previous value
     */
    public final long getAndAdd(long delta) {
        for (; ; ) {
            long current = get();
            long next = current + delta;

            if (next < 0) {
                return -1;
            }

            if (compareAndSet(current, next))
                return current;
        }
    }

    /**
     * Atomically increments by one the current value.
     *
     * @return the updated value
     */
    public final long incrementAndGet() {
        for (; ; ) {
            long current = get();
            long next = current + 1;

            if (compareAndSet(current, next))
                return next;
        }
    }

    /**
     * Atomically decrements by one the current value.
     *
     * @return the updated value
     */
    public final long decrementAndGet() {
        for (; ; ) {
            long current = get();
            long next = current - 1;

            if (next < 0) {
                return -1;
            }

            if (compareAndSet(current, next))
                return next;
        }
    }

    /**
     * Atomically adds the given value to the current value.
     *
     * @param delta the value to add
     * @return the updated value
     */
    public final long addAndGet(long delta) {
        for (; ; ) {
            long current = get();
            long next = current + delta;

            if (next < 0) {
                return -1;
            }

            if (compareAndSet(current, next))
                return next;
        }
    }

    /**
     * Returns the String representation of the current value.
     *
     * @return the String representation of the current value.
     */
    public String toString() {
        return Long.toString(get());
    }

    public int intValue() {
        return (int) get();
    }

    public long longValue() {
        return get();
    }

    public float floatValue() {
        return (float) get();
    }

    public double doubleValue() {
        return (double) get();
    }
}
