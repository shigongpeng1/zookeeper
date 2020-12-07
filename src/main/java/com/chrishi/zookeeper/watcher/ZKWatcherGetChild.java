package com.chrishi.zookeeper.watcher;

import lombok.SneakyThrows;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKWatcherGetChild {

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
    public void watcherGetChild1() throws KeeperException, InterruptedException {
        //arg1:节点路径
        //arg2:使用连接对象中的watcher对象
        zooKeeper.getChildren("/watcher1",true);
        Thread.sleep(50000);
        System.out.println("the end");
    }

    @Test
    public void watcherGetChild2() throws KeeperException, InterruptedException {
        //arg1:节点路径
        //arg2:自定义watcher
        zooKeeper.getChildren("/watcher1", new Watcher() {
            @SneakyThrows
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("path1:"+watchedEvent.getPath());
                System.out.println("eventType1="+watchedEvent.getType());
                zooKeeper.getChildren("/watcher1",this);
            }
        });
        Thread.sleep(50000);
        System.out.println("the end");
    }


    @Test
    public void watcherGetChild3() throws KeeperException, InterruptedException {
        //watcher是一次性的
        Watcher watcher = new Watcher() {
            @SneakyThrows
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("path1:"+watchedEvent.getPath());
                System.out.println("eventType1="+watchedEvent.getType());
                //如果是子节点发生变化，则注册监视器
                if(watchedEvent.getType()==Event.EventType.NodeChildrenChanged){
                    zooKeeper.getChildren("/watcher1",this);
                }
            }
        };
        //arg1:节点路径
        //arg2:自定义watcher
        zooKeeper.getChildren("/watcher1", watcher);
        Thread.sleep(50000);
        System.out.println("the end");
    }

    @Test
    public void watcherGetChild4() throws KeeperException, InterruptedException {
        zooKeeper.getChildren("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("path1:"+watchedEvent.getPath());
                System.out.println("eventType1:"+watchedEvent.getType());
            }
        });
        zooKeeper.getChildren("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("path2:"+watchedEvent.getPath());
                System.out.println("eventType2:"+watchedEvent.getType());
            }
        });
        Thread.sleep(5000);
        System.out.println("the end");
    }
}
















