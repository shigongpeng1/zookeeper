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

public class ZKWatcherGetData {

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
    public void watcherGetData1() throws KeeperException, InterruptedException {
        //arg1:节点的路径
        //arg2:是否使用连接对象中的watcher
        //arg3:
        zooKeeper.getData("/watcher1",true,null);
        Thread.sleep(5000);
        System.out.println("the end");
    }

    @Test
    public void watcherGetData2() throws KeeperException, InterruptedException {
        //arg1:节点路径
        //arg2:自定义watcher对象
        zooKeeper.getData("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("自定义watcher");
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType="+watchedEvent.getType());
            }
        },null);
        Thread.sleep(8000);
        System.out.println("The end");
    }

    @Test
    public void watcherGetData3() throws KeeperException, InterruptedException {
        //一次性
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try{
                    System.out.println("自定义watcher");
                    System.out.println("path:"+watchedEvent.getPath());
                    System.out.println("eventType="+watchedEvent.getType());
                    //节点属性是变化时注册监视器，删除注册监视器会报错
                    if(watchedEvent.getType()==Event.EventType.NodeDataChanged){
                        zooKeeper.getData("/watcher1",this,null);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        zooKeeper.getData("/watcher1",watcher,null);
        Thread.sleep(50000);
        System.out.println("The end");
    }

    @Test
    public void watcherGetData5() throws KeeperException, InterruptedException {
        //注册多个监视器对象
        zooKeeper.getData("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("no1");
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType="+watchedEvent.getType());
            }
        },null);
        zooKeeper.getData("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("no2");
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType="+watchedEvent.getType());
            }
        },null);
        Thread.sleep(5000);
        System.out.println("the end");
    }
}



