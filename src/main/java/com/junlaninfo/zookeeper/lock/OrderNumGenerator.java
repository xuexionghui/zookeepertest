package com.junlaninfo.zookeeper.lock;

import java.text.SimpleDateFormat;
import java.util.Date;

//生成订单类
public class OrderNumGenerator {
    //全局订单id
    public static int count = 0;

    public   String getNumber() {
        try {
            Thread.sleep(200);
        } catch (Exception e) {
        }
        SimpleDateFormat simpt = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return simpt.format(new Date()) + "-" + ++count;
    }
}