package com.chrishi.zookeeper.basic;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKCreate {

    ZooKeeper zk;
    String url = "175.24.100.173:2180";

    @Before
    public void before(){
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            zk = new ZooKeeper(url, 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                        System.out.println("连接成功！！!");
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @After
    public void after(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void create1(){
        try{
            /**
             * arg1:节点的路径
             * arg2:节点的数据
             * arg3:权限列表：world:anyone:cdrwa
             * arg4:节点类型   持久化节点
             */
            zk.create("/create/node1","node1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void create2(){
        try{
            //ZooDefs.Ids.READ_ACL_UNSAFE：world:anyone:r
            zk.create("/create/node2","node2".getBytes(),ZooDefs.Ids.READ_ACL_UNSAFE,CreateMode.PERSISTENT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    /**
     * world权限模式
     */
    public void create3(){
        //权限列表
        List<ACL> acls = new ArrayList<>();
        //权限模式和授权对象
        Id id= new Id("world","anyone");
        acls.add(new ACL(ZooDefs.Perms.READ,id));
        acls.add(new ACL(ZooDefs.Perms.WRITE,id));
        try {
            String res = zk.create("/create/node3","node3".getBytes(),acls,CreateMode.PERSISTENT);
            System.out.println("结果为："+res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * ip模式授权
     */
    public void create4(){
        try{
            //权限列表
            List<ACL> acls = new ArrayList<>();
            //授权模板和对象
            Id id = new Id("ip","118.25.58.244");
            acls.add(new ACL(ZooDefs.Perms.ALL,id));
            String path = zk.create("/create/node4","node4".getBytes(),acls,CreateMode.PERSISTENT);
            System.out.println("结果为："+path);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    /**
     * auth授权模式
     */
    public void create5(){
        try {
            //添加授权用户
            zk.addAuthInfo("digest","chrishi:1q23l".getBytes());
            String path = zk.create("/create/node5","node5".getBytes(),ZooDefs.Ids.CREATOR_ALL_ACL,CreateMode.PERSISTENT);
            System.out.println("返回路径为："+path);
        }catch (Exception e){

        }
    }

    @Test
    /**
     * auth授权：特定权限
     */
    public void create6() throws KeeperException, InterruptedException {
        zk.addAuthInfo("digest","chrishi:1q23l".getBytes());
        //权限列表
        List<ACL> acls = new ArrayList<>();
        //权限模式和授权对象
        Id id = new Id("auth","chrishi");
        //权限设置
        acls.add(new ACL(ZooDefs.Perms.READ,id));
        String path =  zk.create("/create/node6","node6".getBytes(),acls,CreateMode.PERSISTENT);
        System.out.println("输出路径为："+path);
    }

    @Test
    /**
     * digest授权模式
     */
    public void  create7() throws KeeperException, InterruptedException {
        //权限列表
        List<ACL> acls = new ArrayList<>();
        //授权模式和授权对象
        Id id = new Id("digest","chrishi:j+GKsyn+N5Z+nAK3FqgvCNF1nnw=");
        //权限设置
        acls.add(new ACL(ZooDefs.Perms.ALL,id));
        String path = zk.create("/create/node7","node7".getBytes(),acls,CreateMode.PERSISTENT);
        System.out.println("输出路径为："+path);
    }

    @Test
    /**
     * 持久化顺序节点
     */
    public void create8() throws KeeperException, InterruptedException {
        //ZooDefs.Ids.OPEN_ACL_UNSAFE:world:anyone:cdrwa
        String path = zk.create("/create/node8","node8".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println(path);
    }

    @Test
    /**
     * 临时节点
     */
    public void create9() throws KeeperException, InterruptedException {
        String result = zk.create("/create/node9","node9".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        System.out.println(result);
    }

    @Test
    /**
     * 临时顺序节点
     */
    public void create10() throws KeeperException, InterruptedException {
        String result = zk.create("/create/node10","node10".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(result);
    }

    @Test
    /**
     * 异步方式创建节点
     */
    public void create11() throws InterruptedException {
        zk.create("/create/node11", "node11".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, new AsyncCallback.StringCallback() {
            @Override
            //最后一个参数被作为上下文参数传递进context中
            public void processResult(int i, String s, Object context, String s1) {
                //0 代表成功
                System.out.println(i);
                //节点的路径
                System.out.println("节点的路径"+s);
                //上下文参数
                System.out.println("上下文参数"+context);
                //节点的路径
                System.out.println("节点的路径"+s1);
            }
        },"I am context");
        Thread.sleep(10000);
        System.out.println("the end");
    }

}
