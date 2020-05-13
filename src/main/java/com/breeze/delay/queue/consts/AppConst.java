package com.breeze.delay.queue.consts;


/**
 * @author breeze
 * @date 2020/5/11
 */
public interface AppConst {

    /**订单超时关闭时间*/
    Integer ORDER_TIMEOUT = 15;

    /**redis中Zset延时队列的键*/
    String DELAY_QUEUE = "delay-queue";
}
