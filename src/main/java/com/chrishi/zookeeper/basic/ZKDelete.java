package com.chrishi.zookeeper.basic;

import org.apache.zookeeper.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKDelete {
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
    public void delete1() throws KeeperException, InterruptedException {
        /**
         * args1:删除节点的节点路径
         * args2:数据版本信息，-1表示删除节点时不以节点版本作为删除条件
         */
        zk.delete("/delete/node1",-1);
    }

    @Test
    public void delete2() throws InterruptedException {
        zk.delete("/delete/node2", -1, new AsyncCallback.VoidCallback() {
            @Override
            public void processResult(int i, String s, Object o) {
                //0表示删除成功
                System.out.println(i);
                //删除节点路径
                System.out.println(s);
                //上下文参数
                System.out.println(o);
            }
        },"I am context");
        Thread.sleep(1000);
        System.out.println("the end");
    }

}
