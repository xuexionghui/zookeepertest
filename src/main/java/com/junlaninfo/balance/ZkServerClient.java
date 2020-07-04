package com.junlaninfo.balance;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ZkServerClient {
    public static List<String> listServer = new ArrayList<String>();

    public static void main(String[] args) {
        initServer();
        ZkServerClient client = new ZkServerClient();
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String name;
            try {
                name = console.readLine();
                if ("exit".equals(name)) {
                    System.exit(0);
                }
                client.send(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 注册所有server
    public static void initServer() {
        listServer.clear();
        //1、 建立连接
        final ZkClient zkClient = new ZkClient("192.168.196.175:2181");
        //2、读取父节点
        final String root = "/junlan_service";
        final List<String> children = zkClient.getChildren(root);//读取到父节点下属的子节点
        for (String p : children) {
            String childname = root + "/" + p;  //拼接子节点的路径
            String childvalue = zkClient.readData(childname); //读取到子节点的value
            listServer.add(childvalue);            //将值放入到集合中做负载均衡
        }
        serverCount=listServer.size();          //服务器的总数就等于list的长度
        System.out.println("服务发现："+listServer.toString());

        //监听服务器节点的改变 做到动态负载均衡
        // 监听事件
        zkClient.subscribeChildChanges(root, new IZkChildListener() {
            public void handleChildChange(String root, List<String> currentChilds) throws Exception {
                listServer.clear();
                for (String p : currentChilds) {
                    String childname = root + "/" + p;  //拼接子节点的路径
                    String childvalue = zkClient.readData(childname); //读取到子节点的value
                    listServer.add(childvalue);            //将值放入到集合中做负载均衡
                }
                serverCount=listServer.size();          //服务器的总数就等于list的长度
                System.out.println("服务发现："+listServer.toString());
            }
        });
    }
    //请求总数
    private  static Integer reqCount=1;
    //服务器
    private static Integer serverCount=0;

    // 获取当前server信息
    public static String getServer() {
        //本地负载均衡轮训算法
        String  rightServer=listServer.get(reqCount%serverCount);  //确定本次请求的处理服务器
        System.out.println("请求的次数："+reqCount+"  本地请求对应的服务器："+rightServer);
        reqCount++;  //请求的数量加1
        return  rightServer;
    }

    public void send(String name) {

        String server = ZkServerClient.getServer();
        String[] cfg = server.split(":");

        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket(cfg[0], Integer.parseInt(cfg[1]));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(name);
            while (true) {
                String resp = in.readLine();
                if (resp == null)
                    break;
                else if (resp.length() > 0) {
                    System.out.println("Receive : " + resp);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}