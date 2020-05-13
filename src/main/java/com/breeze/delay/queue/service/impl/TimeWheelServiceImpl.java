package com.breeze.delay.queue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.breeze.delay.queue.consts.AppConst;
import com.breeze.delay.queue.entity.Order;
import com.breeze.delay.queue.mapper.OrderMapper;
import com.breeze.delay.queue.service.TimeWheelService;
import com.breeze.delay.queue.utils.IdWorkerUtil;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author breeze
 * @date 2020/5/13
 *
 * kafka、netty都有基于时间轮算法实现延时队列，这里采用Netty的
 *      1.Netty构建延时队列主要用HashedWheelTimer，HashedWheelTimer底层数据结构依然是使用DelayedQueue，
 *       只是采用时间轮的算法来实现。
 *      2.参数：
 *          ThreadFactory ：表示用于生成工作线程，一般采用线程池；
 *          tickDuration和unit：每格的时间间隔，默认100ms；
 *          ticksPerWheel：一圈下来有几格，默认512，而如果传入数值的不是2的N次方，则会调整为大于等于该参数的一个2的N次方数值，有利于优化hash值的计算。
 *
 *      3.TimerTask：一个定时任务的实现接口，其中run方法包装了定时任务的逻辑。
 *      4.Timeout：一个定时任务提交到Timer之后返回的句柄，通过这个句柄外部可以取消这个定时任务，并对定时任务的状态进行一些基本的判断。
 *      5.Timer：是HashedWheelTimer实现的父接口，仅定义了如何提交定时任务和如何停止整个定时机制。
 */
@Service
@Slf4j
public class TimeWheelServiceImpl implements TimeWheelService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    //每隔5s 每圈2格子
    private final Timer timer = new HashedWheelTimer(Executors.defaultThreadFactory(),
            5, TimeUnit.SECONDS, 2);

    @Override
    public int saveOrder(Order order) {
        //设置订单编号
        order.setOrderCode(IdWorkerUtil.getTimeId());
        //设置延时时间为15s
        order.setOrder(AppConst.ORDER_TIMEOUT, TimeUnit.SECONDS);
        order.setPay(false);

        //开启线程，执行时间轮延时队列
        threadPoolExecutor.execute(() -> this.nettyDelayQueue(order.getOrderName()));

        //处理保存的逻辑
        return this.orderMapper.insert(order);
    }

    @Override
    public int payOrder(String orderName) {

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

        //获取数据库订单集合
        List<Order> orderList = this.orderMapper.selectList(null);

        //最终返回结果集
        Map<String, Object> result = new HashMap<>(2);
        result.put("orderList", orderList);

        return result;
    }

    /**
     * 使用Netty实现延时队列 - 底层是采用时间轮的算法
     * @param orderName 订单名
     */
    private void nettyDelayQueue(String orderName) {

        log.info("定时任务开始时间：{}", new Date());

        //定时任务 - 15s后执行
        timer.newTimeout((timeout) -> {

            //查询数据库
            QueryWrapper<Order> wrapper = new QueryWrapper<>();
            wrapper.eq("order_name", orderName);
            Order order = this.orderMapper.selectOne(wrapper);

            //如果15s后未支付，取消订单
            if (!order.getPay()) {
                log.info("超时时间：{}，超时取消订单：{}", new Date(), orderName);
                this.orderMapper.updateOrderToDeletedById(order.getId());
            }
        }, AppConst.ORDER_TIMEOUT, TimeUnit.SECONDS);
    }
}
