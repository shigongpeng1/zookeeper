package com.chrishi.zookeeper.basic;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKSet {
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
    public void set1() throws KeeperException, InterruptedException {
        /**
         * args1:节点的路径
         * args2:修改的数据
         * args3:版本号,表示节点的当前版本号，而不是要更新成的版本号，不一致会报错，-1表示版本号不参与更新
         */
        Stat path = zk.setData("/set/node1","node15".getBytes(),3);
        System.out.println("当前版本号："+path.getVersion());
    }

    @Test
    public void set2() throws InterruptedException {
        /**
         *
         */
        zk.setData("/set/node1", "node3".getBytes(), -1, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int i, String s, Object o, Stat stat) {
                //0表示修改成功
                System.out.println(i);
                //节点路径
                System.out.println(s);
                //上下文参数
                System.out.println(o);
                //属性描述对象
                System.out.println(stat);
            }
        },"I am context");
        Thread.sleep(10000);
        System.out.println();
    }

}
























