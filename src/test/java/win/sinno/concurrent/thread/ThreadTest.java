package win.sinno.concurrent.thread;

import org.junit.Test;

import java.util.Date;

/**
 * thread test
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/7 16:17
 */
public class ThreadTest {

    @Test
    public void testDaemon() {
        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    System.out.println(new Date());

                    try {
                        Thread.sleep(3000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }

    @Test
    public void testYield() throws InterruptedException {

        Thread t1 = new Thread() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("t1:" + new Date());

                    try {
                        Thread.sleep(1000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.start();

        Thread t2 = new Thread() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("t2:" + new Date());

                    try {
                        Thread.sleep(2000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t2.start();

        // 理论上，yield意味着放手，放弃，投降。
        // 一个调用yield()方法的线程告诉虚拟机它乐意让其他线程占用自己的位置。
        // 这表明该线程没有在做一些紧急的事情。
        // 注意，这仅是一个暗示，并不能保证不会产生任何影响。
        /**
         * A hint to the scheduler that the current thread is willing to yield
         * its current use of a processor. The scheduler is free to ignore this
         * hint.
         *
         * <p> Yield is a heuristic attempt to improve relative progression
         * between threads that would otherwise over-utilise a CPU. Its use
         * should be combined with detailed profiling and benchmarking to
         * ensure that it actually has the desired effect.
         *
         * <p> It is rarely appropriate to use this method. It may be useful
         * for debugging or testing purposes, where it may help to reproduce
         * bugs due to race conditions. It may also be useful when designing
         * concurrency control constructs such as the ones in the
         * {@link java.util.concurrent.locks} package.
         */
        t1.yield();

        Thread.sleep(10000l);
    }

    @Test
    public void testJoin() throws InterruptedException {
        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    System.out.println(new Date());

                    try {
                        Thread.sleep(3000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
        //使得一个线程在另一个线程结束后再执行。如果join()方法在一个线程实例上调用，当前运行着的线程将阻塞直到这个线程实例完成了执行。
        t.join();

        // 因为线程t永远不会结算，所以这里永远不会执行到
        Thread t1 = new Thread() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("t1:" + new Date());

                    try {
                        Thread.sleep(3000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.start();

        System.out.println("finish");
    }

    @Test
    public void testInterrupted() throws InterruptedException {

        // interrupted 是作用于当前线程，isInterrupted 是作用于调用该方法的线程对象所对应的线程。
        // 线程对象对应的线程不一定是当前运行的线程。
        // 例如我们可以在A线程中去调用B线程对象的isInterrupted方法。
        // 这两个方法最终都会调用同一个方法，只不过参数一个是true，一个是false；
        final Thread t1 = new Thread() {

            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("t1: " + new Date() + " isInterrupted:" + isInterrupted());
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        System.out.println("interrupt..." + " isInterrupted:" + isInterrupted());
//                        interrupted();
                    }
                }
            }
        };
        t1.setDaemon(true);
        t1.start();

        try {
            Thread.sleep(2000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t1.interrupt();

        System.out.println("after interrupt:" + t1.isInterrupted());

        Thread.sleep(100000l);

    }

    /**
     * When a Java Virtual Machine starts up, there is usually a single
     * non-daemon thread (which typically calls the method named
     * <code>main</code> of some designated class). The Java Virtual
     * Machine continues to execute threads until either of the following
     * occurs:
     * <ul>
     * <li>The <code>exit</code> method of class <code>Runtime</code> has been
     * called and the security manager has permitted the exit operation
     * to take place.
     * <li>All threads that are not daemon threads have died, either by
     * returning from the call to the <code>run</code> method or by
     * throwing an exception that propagates beyond the <code>run</code>
     * method.
     * </ul>
     */
    public static void main(String[] args) {

        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    System.out.println(new Date());

                    try {
                        Thread.sleep(3000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        //        t.setDaemon(true);
        t.start();

        //        Exception in thread "main" java.lang.IllegalThreadStateException
        //        at java.lang.Thread.start(Thread.java:705)
        //        at win.sinno.concurrent.thread.ThreadTest.main(ThreadTest.java:70)
        // --
        //    public synchronized void start() {
        //        /**
        //         * This method is not invoked for the main method thread or "system"
        //         * group threads created/set up by the VM. Any new functionality added
        //         * to this method in the future may have to also be added to the VM.
        //         *
        //         * A zero status value corresponds to state "NEW".
        //         */
        //        if (threadStatus != 0)
        //            throw new IllegalThreadStateException();
        t.start();
    }
}
