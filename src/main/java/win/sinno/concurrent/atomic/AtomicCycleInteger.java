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
public class AtomicCycleInteger extends Number implements java.io.Serializable {

    private static final Unsafe unsafe;
    private static final long valueOffset;

    static {
        try {
            unsafe = UnsafeUtil.getUnsafe();
            valueOffset = unsafe.objectFieldOffset(AtomicCycleInteger.class.getDeclaredField("value"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private volatile int value;

    private int minValue = 0;
    private int maxValue = Integer.MAX_VALUE;

    public AtomicCycleInteger() {
    }

    public AtomicCycleInteger(int initValue) {
        Validate.isTrue(initValue >= minValue, "initValue:[" + initValue + "] < minValue:[" + minValue + "]");
        Validate.isTrue(initValue <= maxValue, "initValue:[" + initValue + "] > maxValue:[" + maxValue + "]");

        this.value = initValue;
    }

    public AtomicCycleInteger(int initValue, int maxValue) {
        Validate.isTrue(initValue >= minValue, "initValue:[" + initValue + "] < minValue:[" + minValue + "]");
        Validate.isTrue(initValue <= maxValue, "initValue:[" + initValue + "] > maxValue:[" + maxValue + "]");

        this.value = initValue;
        this.maxValue = maxValue;
    }

    public AtomicCycleInteger(int initValue, int minValue, int maxValue) {
        Validate.isTrue(initValue >= minValue, "initValue:[" + initValue + "] < minValue:[" + minValue + "]");
        Validate.isTrue(initValue <= maxValue, "initValue:[" + initValue + "] > maxValue:[" + maxValue + "]");

        this.value = initValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Gets the current value.
     *
     * @return the current value
     */
    public final int get() {
        return value;
    }

    /**
     * Sets to the given value.
     *
     * @param newValue the new value
     */
    public final void set(int newValue) {
        Validate.isTrue(newValue >= minValue, "newValue:[" + newValue + "] < minValue:[" + minValue + "]");
        Validate.isTrue(newValue <= maxValue, "newValue:[" + newValue + "] > maxValue:[" + maxValue + "]");

        value = newValue;
    }

    /**
     * Eventually sets to the given value.
     *
     * @param newValue the new value
     * @since 1.6
     */
    public final void lazySet(int newValue) {
        Validate.isTrue(newValue >= minValue, "newValue:[" + newValue + "] < minValue:[" + minValue + "]");
        Validate.isTrue(newValue <= maxValue, "newValue:[" + newValue + "] > maxValue:[" + maxValue + "]");

        unsafe.putOrderedInt(this, valueOffset, newValue);
    }

    /**
     * Atomically sets to the given value and returns the old value.
     *
     * @param newValue the new value
     * @return the previous value
     */
    public final int getAndSet(int newValue) {
        Validate.isTrue(newValue >= minValue, "newValue:[" + newValue + "] < minValue:[" + minValue + "]");
        Validate.isTrue(newValue <= maxValue, "newValue:[" + newValue + "] > maxValue:[" + maxValue + "]");

        for (; ; ) {
            int current = get();
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
    public final boolean compareAndSet(int expect, int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
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
    public final boolean weakCompareAndSet(int expect, int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
    }

    /**
     * Atomically increments by one the current value.
     *
     * @return the previous value
     */
    public final int getAndIncrement() {
        for (; ; ) {
            int current = get();
            int next = minValue;
            if (current != maxValue) {
                next = current + 1;
            }
            if (compareAndSet(current, next))
                return current;
        }
    }

    /**
     * Atomically decrements by one the current value.
     *
     * @return the previous value
     */
    public final int getAndDecrement() {
        for (; ; ) {
            int current = get();
            int next = current - 1;
            if (current == minValue) {
                next = maxValue;
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
    public final int getAndAdd(int delta) {
        for (; ; ) {
            int current = get();
            int next = current + delta;
            if (next > maxValue) {
                next = next - maxValue - 1 + minValue;
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
    public final int incrementAndGet() {
        for (; ; ) {
            int current = get();
            int next = minValue;
            if (current != maxValue) {
                next = current + 1;
            }
            if (compareAndSet(current, next))
                return next;
        }
    }

    /**
     * Atomically decrements by one the current value.
     *
     * @return the updated value
     */
    public final int decrementAndGet() {
        for (; ; ) {
            int current = get();
            int next = current - 1;
            if (current == minValue) {
                next = maxValue;
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
    public final int addAndGet(int delta) {
        for (; ; ) {
            int current = get();
            int next = current + delta;
            if (next > maxValue) {
                next = next - maxValue - 1 + minValue;
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
        return Integer.toString(get());
    }

    public int intValue() {
        return get();
    }

    public long longValue() {
        return (long) get();
    }

    public float floatValue() {
        return (float) get();
    }

    public double doubleValue() {
        return (double) get();
    }
}
