package com.breeze.delay.queue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.breeze.delay.queue.consts.AppConst;
import com.breeze.delay.queue.entity.Order;
import com.breeze.delay.queue.mapper.OrderMapper;
import com.breeze.delay.queue.service.RedisDelayQueueService;
import com.breeze.delay.queue.utils.IdWorkerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author breeze
 * @date 2020/5/13
 */
@Service
public class RedisDelayQueueServiceImpl implements RedisDelayQueueService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public int saveOrder(Order order) {
        //设置订单编号
        order.setOrderCode(IdWorkerUtil.getTimeId());
        //设置延时时间为15s
        order.setOrder(AppConst.ORDER_TIMEOUT, TimeUnit.SECONDS);
        order.setPay(false);

        //保存到Redis中，并设置过期时间 15s
        this.redisTemplate.opsForValue().set(order.getOrderName(), order.getOrderCode(), AppConst.ORDER_TIMEOUT, TimeUnit.SECONDS);

        //处理保存的逻辑
        return this.orderMapper.insert(order);
    }

    @Override
    public int payOrder(String orderName) {

        //删除Redis中订单
        this.redisTemplate.delete(orderName);

        //更新订单状态为已支付
        Order order = new Order();
        order.setOrderName(orderName);
        order.setPay(true);

        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_name", orderName);

        return this.orderMapper.update(order, wrapper);
    }

    @Override
    public Map<String, Object> listOrder() {
        System.out.println();
        //获取Redis中订单集合
        Set<String> orderNameSet = this.redisTemplate.keys("order*");

        //获取数据库订单集合
        List<Order> orderList = this.orderMapper.selectList(null);

        //最终返回结果集
        Map<String, Object> result = new HashMap<>(2);
        result.put("orderNameSet", orderNameSet);
        result.put("orderList", orderList);

        return result;
    }
}
