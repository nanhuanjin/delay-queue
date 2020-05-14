package com.breeze.delay.queue.service;

import com.breeze.delay.queue.entity.Order;

import java.util.Map;

/**
 * @author breeze
 * @date 2020/5/14
 */
public interface RabbitMQService {

    /**
     * 保存订单
     * @param order
     * @return
     */
    int saveOrder(Order order);

    /**
     * 订单支付
     * @param orderName
     * @return
     */
    int payOrder(String orderName);

    /**
     * 查询订单列表
     * @return
     */
    Map<String, Object> listOrder();

    /**
     * 超时逻辑删除订单
     * @param orderCode
     * @return
     */
    int closeOrder(String orderCode);
}
