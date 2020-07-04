package com.junlaninfo;

import org.I0Itec.zkclient.ZkClient;

/**
 * Created by 辉 on 2020/6/28.
 * 创建临时节点
 */
public class testzookeeper01 {
    public static void main(String[] args) throws InterruptedException {
        //连接zookeeper
        String serverurl="192.168.196.175:2181";
        ZkClient zkClient = new ZkClient(serverurl);
        if (zkClient.exists("/user")){
            zkClient.delete("/user");
        }
        //创建临时节点
        zkClient.createEphemeral("/user");
        //当连接关闭的时候，节点就会消失
        Thread.sleep(3000);
        zkClient.close();
    }
}
