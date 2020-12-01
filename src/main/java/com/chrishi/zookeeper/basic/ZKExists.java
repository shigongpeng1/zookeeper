package com.chrishi.zookeeper.basic;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKExists {
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
    public void exists1() throws KeeperException, InterruptedException {
        //arg1：节点路径
        Stat stat = zk.exists("/exists",false);
        System.out.println(stat);
    }

    @Test
    /**
     * 异步方式
     */
    public void exixts2() throws InterruptedException {
        zk.exists("/exists",false, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int i, String s, Object o, Stat stat) {
                //0表示执行成功
                System.out.println(i);
                //节点路径
                System.out.println(s);
                //上下文参数
                System.out.println(o);
                //节点属性信息
                System.out.println(stat);
            }
        },"I am contxt");
        Thread.sleep(1000);
        System.out.println("the end");
    }
}
