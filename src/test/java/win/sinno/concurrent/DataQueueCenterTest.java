package win.sinno.concurrent;

import org.junit.Test;
import win.sinno.concurrent.earthworm.DataQueueCenter;
import win.sinno.concurrent.earthworm.DataQueueTeam;
import win.sinno.concurrent.earthworm.custom.AbsDataTeamConf;
import win.sinno.concurrent.earthworm.custom.IDataHandler;
import win.sinno.concurrent.earthworm.custom.IDataTeamConf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2016/11/7 上午10:53
 */
public class DataQueueCenterTest {


    //    @Test
    public void test1() {
        final DataQueueCenter dataQueueCenter = new DataQueueCenter(2);

        final AtomicInteger num1 = new AtomicInteger(0);

        final AtomicInteger num2 = new AtomicInteger(0);

        //创建数据队列团队
        dataQueueCenter.createDataQueueTeam("sinno", 32, new IDataHandler<Integer>() {

            //处理
            public void handler(Integer data) {
//                System.out.println(new Date() + ":" + data + " + " + data + " = " + (data + data));
                num1.addAndGet(1);
            }

        });

        //创建数据队列团队
        dataQueueCenter.createDataQueueTeam("str", 4, new IDataHandler<String>() {

            //处理
            public void handler(String data) {
//                System.out.println(data);
                num2.addAndGet(1);
            }

        });

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    dataQueueCenter.addTask("str", new Date().toString());
                    try {
                        Thread.sleep(10l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                for (int i = 1; i < 500; i++) {
                    dataQueueCenter.addTask("sinno", i);
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                int i = 50000;
                while (true) {
                    dataQueueCenter.addTask("sinno", i++);
                    try {
                        Thread.sleep(2l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                int i = 2000;
                List<Integer> lists = new ArrayList<Integer>();
                for (int j = 0; j < 100; j++) {
                    lists.add(i + j);
                }
                dataQueueCenter.addTasks("sinno", lists);
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                int i = 5000;
                List<Integer> lists = new ArrayList<Integer>();
                for (int j = 0; j < 100; j++) {
                    lists.add(i + j);
                }
                dataQueueCenter.addTasks("sinno", lists);
            }
        }.start();

        while (true) {
            System.out.println("num1:" + num1.get());
            System.out.println("num2:" + num2.get());
            try {
                Thread.sleep(10000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * test data team conf
     */
    @Test
    public void test2() {

        DataQueueCenter dataQueueCenter = new DataQueueCenter(1);

        final String preName = "dq_";

        IDataTeamConf<DataQueue> dataTeamConf = new AbsDataTeamConf<DataQueue>() {
            @Override
            public String getTeamName(DataQueue dataQueue) {
                return preName + dataQueue.getName();
            }

            @Override
            public int getWorkerNum() {
                return 4;
            }

            @Override
            public int getQueueSize() {
                return 2;
            }

            /**
             * 获取数据处理器
             *
             * @return
             */
            @Override
            public IDataHandler<DataQueue> getDataHandler() {
                return new IDataHandler<DataQueue>() {
                    @Override
                    public void handler(DataQueue data) throws InterruptedException {
                        int num = data.getNum();

                        if (num % 2 == 0) {
                            // 余数

                            Thread.sleep(1000l);

                        }

                        System.out.println(data.toString());
                    }
                };
            }

            @Override
            public Boolean getUseTimeout() {
                return Boolean.TRUE;
            }

            @Override
            public Long getTimeout() {
                //超时时间5秒
                return 5000l;
            }
        };

        List<DataQueue> dataQueueList = new ArrayList<DataQueue>();

        for (int i = 0; i < 100; i++) {
            DataQueue dataQueue = new DataQueue();
            dataQueue.setName(String.valueOf(i % 2));
            dataQueue.setNum(i);

            dataQueueList.add(dataQueue);
        }

        //数据queue
        dataQueueCenter.addTasks(dataQueueList, dataTeamConf);

        System.out.println("add tasks finish.......");
        try {
            Thread.sleep(10000l);

            for (int i = 0; i < 10; i++) {
                DataQueueTeam dataQueueTeam = dataQueueCenter.getDataQueueTeam(preName + i);
                System.out.println();
                System.out.println(dataQueueTeam.status());

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据queue
     */
    private static class DataQueue {
        private String name;

        private int num;

        public DataQueue() {
        }

        public DataQueue(String name, int num) {
            this.name = name;
            this.num = num;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        @Override
        public String toString() {
            return "DataQueue{" +
                    "name='" + name + '\'' +
                    ", num=" + num +
                    '}';
        }
    }

}
