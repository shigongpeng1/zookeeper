package com.chrishi.zookeeper.example;

import org.apache.zookeeper.KeeperException;

public class TicketSeller {

    private void sell(){
        System.out.println("开始售票");
        //线程随机休眠毫秒数，模拟现实中的费时操作
        int sleepMillis = 5000;
        try {
            //代表执行复杂逻辑的时间
            Thread.sleep(sleepMillis);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("售票结束");
    }

    public void sellTicketWithLock() throws KeeperException, InterruptedException {
        MyLock myLock = new MyLock();
        //获取锁
        myLock.acquireLock();
        //释放锁
        myLock.releaseLock();
    }


    public static void main(String[] args) throws KeeperException, InterruptedException {
        new Thread(()->{
            TicketSeller ticketSeller = new TicketSeller();
            for(int i=0;i<10;i++){
                try {
                    ticketSeller.sellTicketWithLock();
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(()->{
            TicketSeller ticketSeller = new TicketSeller();
            for(int i=0;i<10;i++){
                try {
                    ticketSeller.sellTicketWithLock();
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}











