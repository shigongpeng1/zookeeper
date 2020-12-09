package com.chrishi.zookeeper.example;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

public class GloballyUniqueID implements Watcher {

    String IP = "175.24.100.173:2180";
    CountDownLatch countDownLatch = new CountDownLatch(1);
    static ZooKeeper zooKeeper = null;
    //用户生成序号的节点
    String defaultPath = "/uniqueId";


    @Override
    public void process(WatchedEvent watchedEvent) {
        try{
            if(watchedEvent.getType()==Event.EventType.None){
                if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
                    System.out.println("连接成功");
                    countDownLatch.countDown();
                }else if(watchedEvent.getState()==Event.KeeperState.Disconnected){
                    System.out.println("连接断开");
                }else if(watchedEvent.getState()==Event.KeeperState.Expired){
                    System.out.println("连接超时");
                    zooKeeper = new ZooKeeper(IP,6000,new MyConfigCenter());
                }else if(watchedEvent.getState()==Event.KeeperState.AuthFailed){
                    System.out.println("验证失败");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public GloballyUniqueID(){
        try{
            //打开链接
            zooKeeper = new ZooKeeper(IP,5000,this);
            //阻塞线程，等待连接的创建成功
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //生成id的方法
    public String getUniqueId(){
        String path = "";
        try {
            //创建临时有序节点
            path = zooKeeper.create(defaultPath,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        }catch (Exception e){
            e.printStackTrace();
        }
        //uniqueId
        return path.substring(9);
    }

    public static void main(String[] args) throws InterruptedException {
//        for(int i=0;i<10;i++){
//            String id = globallyUniqueID.getUniqueId();
//            System.out.println(id);
//        }

        new Thread(()->{
            GloballyUniqueID globallyUniqueID = new GloballyUniqueID();
            for(int i=0;i<10;i++){
                String id = globallyUniqueID.getUniqueId();
                System.out.println("no1"+id);
            }
        }).start();
        new Thread(()->{
            GloballyUniqueID globallyUniqueID = new GloballyUniqueID();
            for(int i=0;i<10;i++){
                String id = globallyUniqueID.getUniqueId();
                System.out.println("no2"+id);
            }
        }).start();
        Thread.sleep(5000);
    }

}













