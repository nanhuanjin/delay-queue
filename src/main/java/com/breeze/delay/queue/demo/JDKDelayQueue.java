package com.breeze.delay.queue.demo;


import com.breeze.delay.queue.entity.MyOrder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author breeze
 * @date 2020/5/11
 */
public class JDKDelayQueue {
    public static void main(String[] args) {

        Random random = new Random();

        MyOrder myOrder1 = new MyOrder("myOrder1", random.nextInt(10) + 5, TimeUnit.SECONDS);
        MyOrder myOrder2 = new MyOrder("myOrder2", random.nextInt(10) + 5, TimeUnit.SECONDS);
        MyOrder myOrder3 = new MyOrder("myOrder3", random.nextInt(10) + 5, TimeUnit.SECONDS);
        DelayQueue<MyOrder> delayQueue = new DelayQueue<MyOrder>();
        delayQueue.put(myOrder1);
        delayQueue.put(myOrder2);
        delayQueue.put(myOrder3);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        System.out.println("订单延迟队列开始时间:" + simpleDateFormat.format(new Date()));
        while (delayQueue.size() != 0) {

            //取队列头部元素是否过期
            MyOrder task = delayQueue.poll();
            if (task != null) {
                System.out.format("订单:{%s}被取消, 取消时间:{%s}\n", task.getOrderName(), simpleDateFormat.format(new Date()));
            }

            try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }

        }

    }
}
