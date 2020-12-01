package com.chrishi.zookeeper.basic;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKGet {

    ZooKeeper zk = null;
    String url = "175.24.100.173:2180";

    @Before
    public void before() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zk = new ZooKeeper(url, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    countDownLatch.countDown();
                    System.out.println("连接成功！！！");
                }
            }
        });
        countDownLatch.await();
    }

    @After
    public void after() throws InterruptedException {
        zk.close();
    }

    @Test
    public void get1() throws KeeperException, InterruptedException {
        Stat stat = new Stat();
        /**
         * arg1:节点的路径
         * arg3:读取节点属性的对象
         */
        byte[] bytes = zk.getData("/get/node1",false,stat);
        String res = new String(bytes);
        System.out.println(res);
        System.out.println(stat.getVersion());
    }

    @Test
    public void get2() throws InterruptedException {
        zk.getData("/get/node1", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                //0表示读取成功
                System.out.println(i);
                //节点的路径
                System.out.println(s);
                //上下文参数
                System.out.println(o);
                //数据
                System.out.println(new String(bytes));
                //属性对象
                System.out.println(stat.getVersion());
            }
        },"I am context");
        Thread.sleep(1000);
        System.out.println("the end");
    }
}
