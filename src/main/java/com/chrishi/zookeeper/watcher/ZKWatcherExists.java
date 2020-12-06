package com.chrishi.zookeeper.watcher;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKWatcherExists {

    String IP = "175.24.100.173:2180";
    ZooKeeper zooKeeper = null;

    @Before
    public void before() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //连接zookeeper客户端
        zooKeeper = new ZooKeeper(IP, 6000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("连接对象的参数！！！");
                //连接成功
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    countDownLatch.countDown();
                }
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType="+watchedEvent.getType());
            }
        });
        countDownLatch.await();
    }

    @After
    public void after() throws InterruptedException {
        zooKeeper.close();
    }

    @Test
    public void watcherExists1() throws KeeperException, InterruptedException {
        //arg1:节点的路径
        //arg2:使用连接对象中的watcher对象
        zooKeeper.exists("/watcher1",true);
        Thread.sleep(50000);
        System.out.println("the end");
    }

    @Test
    public void watcherExists2() throws KeeperException, InterruptedException {
        //arg1:节点路径
        //arg2:自定watcher对象
        zooKeeper.exists("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("自定义watcher");
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType="+watchedEvent.getType());
            }
        });
        Thread.sleep(50000);
        System.out.println("The end");
    }

    @Test
    public void watcherExists3() throws InterruptedException, KeeperException {
        //watcher是一次性的,对对应节点有改动，则会自动失效
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try {
                    System.out.println("自定义watcher");
                    System.out.println("path:"+watchedEvent.getPath());
                    System.out.println("eventType="+watchedEvent.getType());
                    //加上此句，可以重复接收
                    zooKeeper.exists("/watcher1",this);
                }catch (Exception e){

                }
            }
        };
        zooKeeper.exists("/watcher1",watcher);
        Thread.sleep(50000);
        System.out.println("The end");
    }

    @Test
    public void watcherExists4() throws KeeperException, InterruptedException {
        //注册多个监听器对象
        zooKeeper.exists("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("自定义watcher1");
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType="+watchedEvent.getType());
            }
        });
        zooKeeper.exists("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("自定义watcher2");
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType="+watchedEvent.getType());
            }
        });
        Thread.sleep(5000);
        System.out.println("The end");
    }
}






