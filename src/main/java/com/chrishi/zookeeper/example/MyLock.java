package com.chrishi.zookeeper.example;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

    //监视器对象，监视上一个节点是否被删除
    Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
               if(watchedEvent.getType()==Event.EventType.NodeDeleted){
                   synchronized (this){
                       notifyAll();
                   }
               }
        }
    };

    //尝试获取锁
    private void attemptLock() throws KeeperException, InterruptedException {
        //获取locks节点下的所有子节点
        List<String> list = zooKeeper.getChildren(LOCK_ROOT_PATH,false);
        //对子节点进行排序
        Collections.sort(list);
        // /Locks/Lock_00000001
        int index = list.indexOf(lockPath.substring(LOCK_ROOT_PATH.length()+1));
        if(index==0){
            System.out.println("获取锁成功");
            return;
        }else{
            //上一个节点的路径
            String path = list.get(index-1);
            Stat stat =  zooKeeper.exists(LOCK_ROOT_PATH+"/"+path,watcher);
            if(stat == null){
                attemptLock();
            }else {
                synchronized (watcher){
                    watcher.wait();
                }
                attemptLock();
            }
        }
    }
    //释放锁
    public void releaseLock() throws KeeperException, InterruptedException {
        //删除临时有序节点
        zooKeeper.delete(this.lockPath,-1);
        zooKeeper.close();
        System.out.println("锁已经释放："+this.lockPath);
    }

    public static void main(String[] args) throws KeeperException, InterruptedException {
        MyLock myLock = new MyLock();
        myLock.createLock();
    }
}






