package com.breeze.delay.queue.demo;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author breeze
 * @date 2020/5/11
 */
public class NettyDelayQueue {
    public static void main(String[] args) {

        final Timer timer = new HashedWheelTimer(Executors.defaultThreadFactory(), 5, TimeUnit.SECONDS, 2);

        //定时任务
        TimerTask task1 = new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                System.out.println("order1  5s 后执行 ");
                timer.newTimeout(this, 5, TimeUnit.SECONDS);//结束时候再次注册
            }
        };
        timer.newTimeout(task1, 5, TimeUnit.SECONDS);


        TimerTask task2 = new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                System.out.println("order2  10s 后执行");
                timer.newTimeout(this, 10, TimeUnit.SECONDS);//结束时候再注册
            }
        };

        timer.newTimeout(task2, 10, TimeUnit.SECONDS);

        //延迟任务
        timer.newTimeout(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                System.out.println("order3  15s 后执行一次");
            }
        }, 15, TimeUnit.SECONDS);

    }
}
