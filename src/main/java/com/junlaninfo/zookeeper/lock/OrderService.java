package com.junlaninfo.zookeeper.lock;

import com.sun.org.apache.bcel.internal.generic.NEW;

//使用多线程模拟生成订单号
public class OrderService implements Runnable {
    private OrderNumGenerator orderNumGenerator = new OrderNumGenerator();
     ZookeeperLock zkLock=new ZookeeperLock();

	public void run() {
		getNumber();
	}

	public   void   getNumber() {
		try {
			zkLock.getLock();
			String number = orderNumGenerator.getNumber();
			System.out.println(Thread.currentThread().getName() + ",生成订单ID:" + number);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			zkLock.unLock();
		}
	}

	public static void main(String[] args) {
		System.out.println("####生成唯一订单号###");
		for (int i = 0; i < 100; i++) {
			new Thread(new OrderService()).start();
		}

	}
}