package com.junlaninfo.balance;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.data.Id;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;

//##ServerScoekt服务端
public class ZkServerScoekt implements Runnable {
    private static int port = 18082;

    public static void main(String[] args) throws IOException {

        ZkServerScoekt server = new ZkServerScoekt(port);
        Thread thread = new Thread(server);
        thread.start();
    }

    public ZkServerScoekt(int port) {
        this.port = port;
    }

    //注册服务
    public void register() {
        //1、建立连接
        ZkClient zkClient = new ZkClient("192.168.196.175:2181");
        //2、创建父节点  父节点用持久节点
        String root = "/junlan_service";
        if (!zkClient.exists(root)) {
            zkClient.createPersistent(root);
        }
        //3、创建子节点  子节点创建临时的
        String nodeName = root + "/child:" + port;
        String nodeValue = "127.0.0.1:" + port;
        if (zkClient.exists(nodeName)) {
            zkClient.delete(nodeName);
        }
        zkClient.createEphemeral(nodeName,nodeValue);
    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            register();
            System.out.println("Server start port:" + port);
            Socket socket = null;
            while (true) {
                socket = serverSocket.accept();
                new Thread(new ServerHandler(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (Exception e2) {

            }
        }
    }

}