package com.chrishi.zookeeper.example;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class MyLock {

    String IP = "175.24.100.173:2180";
    CountDownLatch countDownLatch = new CountDownLatch(1);
    static ZooKeeper zooKeeper;
    private static final String LOCK_ROOT_PATH = "/Locks";
    private static final String LOCK_NODE_NAME = "Lock_";
    private String lockPath;
    //创建zookeeper连接
    public MyLock(){
        try {
            zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getType() == Event.EventType.None){
                        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                            System.out.println("连接创建成功");
                            countDownLatch.countDown();
                        }
                    }
                }
            });
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //获取锁
    public void acquireLock() throws KeeperException, InterruptedException {
        //创建锁节点
        createLock();
        //尝试获取锁
        attemptLock();
    }

    //创建锁节点
    private void createLock() throws KeeperException, InterruptedException {
        //判断Locks是否存在，不存在则创建
        Stat stat = zooKeeper.exists(LOCK_ROOT_PATH,false);
        if(stat == null){
            zooKeeper.create(LOCK_ROOT_PATH,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        }
        //创建临时有序节点
        lockPath = zooKeeper.create(LOCK_ROOT_PATH+"/"+LOCK_NODE_NAME,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("节点创建成功："+lockPath);
    }
    //尝试获取锁
    private void attemptLock(){

    }
    //释放锁
    public void releaseLock(){

    }

    public static void main(String[] args) throws KeeperException, InterruptedException {
        MyLock myLock = new MyLock();
        myLock.createLock();
    }
}






