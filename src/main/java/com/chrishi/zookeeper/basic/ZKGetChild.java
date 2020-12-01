package com.chrishi.zookeeper.basic;

import org.apache.zookeeper.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKGetChild {

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
        /**
         * arg1:节点的路径
         * arg2:
         */
        List<String> data = zk.getChildren("/get",false);
        data.forEach(e-> System.out.println(e));
    }

    @Test
    /**
     * 异步用法
     */
    public void get2() throws InterruptedException {
        zk.getChildren("/get",false, new AsyncCallback.ChildrenCallback() {
            @Override
            public void processResult(int i, String s, Object o, List<String> list) {
                //0表示成功
                System.out.println(i);
                //节点路径
                System.out.println(s);
                //上下文参数
                System.out.println(o);
                //子节点信息
                list.forEach(e-> System.out.println(e));
            }
        },"I am context");
        Thread.sleep(1000);
        System.out.println("the end");
    }
}













