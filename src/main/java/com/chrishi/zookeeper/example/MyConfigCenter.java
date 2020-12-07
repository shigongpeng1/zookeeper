package com.chrishi.zookeeper.example;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import java.util.concurrent.CountDownLatch;

public class MyConfigCenter implements Watcher {

    String IP = "175.24.100.173:2180";
    CountDownLatch countDownLatch = null;
    static ZooKeeper zooKeeper = null;

    private String url;
    private String username;
    private String password;

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
            }else if(watchedEvent.getType()== Event.EventType.NodeDataChanged){
                initValue();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public MyConfigCenter(){
        initValue();
    }

    //连接zookeeper服务器，读取配置信息
    public void initValue(){
        try{
            countDownLatch = new CountDownLatch(1);
            //创建连接对象
            zooKeeper = new ZooKeeper(IP,5000,this);
            //阻塞线程，等待连接的创建成功
            countDownLatch.await();
            //读取配置信息
            this.url = new String(zooKeeper.getData("/config/url",true,null));
            this.username = new String(zooKeeper.getData("/config/username",true,null));
            this.password = new String(zooKeeper.getData("/config/password",true,null));
        }catch (Exception e){

        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyConfigCenter myConfigCenter = new MyConfigCenter();
        while(true){
            Thread.sleep(5000);
            System.out.println("url:"+myConfigCenter.getUrl());
            System.out.println("username:"+myConfigCenter.getUsername());
            System.out.println("password:"+myConfigCenter.getPassword());
            System.out.println("***************************");
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
