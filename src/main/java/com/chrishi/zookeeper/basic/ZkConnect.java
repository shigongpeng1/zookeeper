package com.chrishi.zookeeper.basic;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZkConnect {

    public static void main(String[] args){
        ZooKeeper zk = null;
        try{
            CountDownLatch countDownLatch = new CountDownLatch(1);
            /**
             * arg1:服务器的ip和端口
             * arg2:客户端与服务器之间的会话超时时间，以毫秒为单位
             * arg3:监视器对象
             */
            zk = new ZooKeeper("175.24.100.173:2180", 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                        System.out.println("连接创建成功！！！");
                        countDownLatch.countDown();
                    }
                }
            });
            //主线程阻塞等待连接对象的创建成功
            countDownLatch.await();
            //会话id
            System.out.println(zk.getSessionId());
        }catch (Exception e){
            e.printStackTrace();
            try {
                zk.close();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

    }
}
