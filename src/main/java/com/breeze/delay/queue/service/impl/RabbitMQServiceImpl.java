package com.breeze.delay.queue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.breeze.delay.queue.consts.AppConst;
import com.breeze.delay.queue.entity.Order;
import com.breeze.delay.queue.mapper.OrderMapper;
import com.breeze.delay.queue.service.RabbitMQService;
import com.breeze.delay.queue.utils.IdWorkerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author breeze
 * @date 2020/5/14
 */
@Service
@Slf4j
public class RabbitMQServiceImpl implements RabbitMQService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public int saveOrder(Order order) {
        //设置订单编号
        order.setOrderCode(IdWorkerUtil.getTimeId());
        //设置延时时间为15s
        order.setOrder(AppConst.ORDER_TIMEOUT, TimeUnit.SECONDS);
        order.setPay(false);

        //发送消息到MQ - 到延时队列
        log.info(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        this.amqpTemplate.convertAndSend("ORDER-EXCHANGE", "order.close", order.getOrderCode());

        //处理保存的逻辑
        return this.orderMapper.insert(order);
    }

    @Override
    public int payOrder(String orderName) {

        //查询订单
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_name", orderName);

        Order order = this.orderMapper.selectOne(wrapper);
        order.setPay(true);

        //更新订单为已支付
        int count = this.orderMapper.update(order, wrapper);

        //发送消息到MQ - 到订单支付成功的队列
        if (count == 1) {
            this.amqpTemplate.convertAndSend("ORDER-EXCHANGE", "order.pay", order.getOrderCode());
        }

        return count;
    }

    @Override
    public Map<String, Object> listOrder() {

        //获取数据库订单集合
        List<Order> orderList = this.orderMapper.selectList(null);

        //最终返回结果集
        Map<String, Object> result = new HashMap<>(2);
        result.put("orderList", orderList);

        return result;
    }

    /**
     * 超时逻辑删除订单
     * @param orderCode
     * @return
     */
    public int closeOrder(String orderCode) {
        //先查询数据库
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_code", orderCode);
        Order order = this.orderMapper.selectOne(wrapper);

        //如果已经支付
        if (order.getPay()) {
            return 0;
        } else {
            return this.orderMapper.updateOrderToDeletedByOrderCode(orderCode);
        }
    }
}
