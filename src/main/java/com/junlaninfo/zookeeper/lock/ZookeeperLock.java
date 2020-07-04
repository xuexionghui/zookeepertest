package com.junlaninfo.zookeeper.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.CountDownLatch;

/**
 * Created by 辉 on 2020/6/30.
 */
public class ZookeeperLock  {
    // zk连接地址
    private static final String CONNECTSTRING = "192.168.196.175:2181";
    // 创建zk连接
    protected ZkClient zkClient = new ZkClient(CONNECTSTRING);
    //防止死锁的发生，在创建zk连接的时候可以传入session的失效
    //时间，这样就能在zk断开的时候自动去除这个临时节点
    protected static final String PATH = "/lock";

    public void getLock() {
        if (tryLock()) {
            System.out.println("##获取lock锁的资源####");
        } else {
            // 等待
            waitLock();
            // 重新获取锁资源
            getLock();
        }
    }

    public void unLock() {
        if (zkClient != null) {
            zkClient.close();
            System.out.println("释放锁资源...");
        }
    }

    private CountDownLatch countDownLatch = null;
    boolean tryLock() {
        try {
            zkClient.createEphemeral(PATH);
            return true;  //创建临时节点成功，返回true
        } catch (Exception e) {
//			e.printStackTrace();
            return false; //创建节点失败，那么就会出异常这里就会捕获，这样就会到这里，返回false
        }
    }

     void waitLock() {
        IZkDataListener izkDataListener = new IZkDataListener() {
            public void handleDataDeleted(String path) throws Exception {
                // 唤醒被等待的线程
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }
            public void handleDataChange(String path, Object data) throws Exception {

            }
        };
        // 注册事件
        zkClient.subscribeDataChanges(PATH, izkDataListener);
        if (zkClient.exists(PATH)) {
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 删除监听
        zkClient.unsubscribeDataChanges(PATH, izkDataListener);
    }

}
