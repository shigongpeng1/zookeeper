package com.chrishi.zookeeper.watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZKConnectionWatcher implements Watcher {

    //计数器对象
    static CountDownLatch countDownLatch = new CountDownLatch(1);
    //连接对象
    static ZooKeeper zooKeeper;

    @Override
    public void process(WatchedEvent watchedEvent) {
        try{
            //时间类型
            if(watchedEvent.getType()==Event.EventType.None){
                if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
                    System.out.println("连接创建成功");
                    countDownLatch.countDown();
                }else if(watchedEvent.getState()==Event.KeeperState.Disconnected){
                    System.out.println("断开连接");
                }else if(watchedEvent.getState()==Event.KeeperState.Expired){
                    System.out.println("会话超时");
                    zooKeeper = new ZooKeeper("175.24.100.173:2180",5000,new ZKConnectionWatcher());
                }else if(watchedEvent.getState()==Event.KeeperState.AuthFailed){
                    System.out.println("认证失败");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            zooKeeper = new ZooKeeper("175.24.100.173:2180",5000,new ZKConnectionWatcher());
            //阻塞线程，等待连接的创建
            countDownLatch.await();
            System.out.println("当前会话id为："+zooKeeper.getSessionId());
            Thread.sleep(5000);
            System.out.println("The end");
            zooKeeper.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
