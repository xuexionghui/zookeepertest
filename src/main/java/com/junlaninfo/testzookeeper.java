package com.junlaninfo;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.I0Itec.zkclient.ZkClient;

/**
 * Created by 辉 on 2020/6/26.
 * 创建持久节点
 */
public class testzookeeper {
    public static void main(String[] args) {
        String zkServer = "192.168.196.175:2181";
        ZkClient zkClient = new ZkClient(zkServer, 6000);
        if (zkClient.exists("/user")) {
            zkClient.delete("/user");
        }
        zkClient.createPersistent("/user", "xuexionghui");
        zkClient.close();
    }
}
