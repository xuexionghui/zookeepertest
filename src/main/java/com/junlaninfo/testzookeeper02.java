package com.junlaninfo;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by 辉 on 2020/6/28.
 * 使用信号量，在zookeeper连接创建的时候，才能创建节点，防止还没有连接就去创建节点报错
 */
public class testzookeeper02 {
    //连接地址
    private static final String ADDRES = "192.168.196.175:2181";
    //session 会话
    private static final int SESSION_OUTTIME = 2000;
    //信号量,阻塞程序执行,用户等待zookeeper连接成功,发送成功信号，
    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZooKeeper zk = new ZooKeeper(ADDRES, SESSION_OUTTIME, new Watcher() {

            public void process(WatchedEvent event) {
                // 获取事件状态
                Event.KeeperState keeperState = event.getState();
                // 获取事件类型
                Event.EventType eventType = event.getType();
                if (Event.KeeperState.SyncConnected == keeperState) {
                    if (Event.EventType.None == eventType) {
                        countDownLatch.countDown();
                        System.out.println("zk 启动连接...");
                    }

                }
            }
        });
        // 进行阻塞
        countDownLatch.await();
        String result = zk.create("/test", "test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);
        System.out.println(result);
        zk.close();
    }
}
