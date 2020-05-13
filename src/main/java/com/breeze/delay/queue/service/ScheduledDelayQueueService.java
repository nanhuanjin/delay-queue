package com.breeze.delay.queue.service;

import com.breeze.delay.queue.entity.Order;

import java.util.Map;

/**
 * @author breeze
 * @date 2020/5/11
 */
public interface ScheduledDelayQueueService {

    /**
     * 保存订单
     * @param order
     */
    int saveOrder(Order order);

    /**
     * 订单支付
     * @param orderName
     * @return
     */
    int payOrder(String orderName);

    /**
     * 获取订单列表
     * @return
     */
    Map<String, Object> listOrder();
}
