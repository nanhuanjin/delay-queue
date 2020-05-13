package com.breeze.delay.queue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.breeze.delay.queue.consts.AppConst;
import com.breeze.delay.queue.entity.Order;
import com.breeze.delay.queue.mapper.OrderMapper;
import com.breeze.delay.queue.service.ScheduledDelayQueueService;
import com.breeze.delay.queue.utils.IdWorkerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author breeze
 * @date 2020/5/11
 */
@Service
@EnableScheduling
@Slf4j
public class ScheduledDelayQueueServiceImpl implements ScheduledDelayQueueService {

    @Autowired
    private OrderMapper orderMapper;

    //本地缓存，存放未支付订单
    private static final Map<String, Object> ORDER_MAP = new HashMap<>();

    @Override
    public int saveOrder(Order order) {
        //设置订单编号
        order.setOrderCode(IdWorkerUtil.getTimeId());
        //设置延时时间为15s
        order.setOrder(AppConst.ORDER_TIMEOUT, TimeUnit.SECONDS);
        order.setPay(false);

        //处理保存的逻辑(模拟数据库)
        int count = this.orderMapper.insert(order);

        String orderName = order.getOrderName();
        ORDER_MAP.put(orderName, order);

        return count;
    }

    @Override
    public int payOrder(String orderName) {
        //订单支付成功，直接删除本地缓存的订单信息
        ORDER_MAP.remove(orderName);

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

        //获取本地缓存中订单集合
        Set<String> orderNameSet = ORDER_MAP.keySet();

        //获取数据库订单集合
        List<Order> orderList = this.orderMapper.selectList(null);

        //最终返回结果集
        Map<String, Object> result = new HashMap<>(2);
        result.put("orderNameSet", orderNameSet);
        result.put("orderList", orderList);

        return result;
    }

    /**
     * 定时任务 - 延时队列
     */
    @Scheduled(cron = "0/1 * * * * ?")
    //@Scheduled(fixedDelay = 1000) 这个是当前任务完成后一秒再次执行
    private void orderDelayQueue() {

        //存放过期的订单名字
        List<String> nameList = new ArrayList<>();

        ORDER_MAP.forEach((s, o) -> {
            Order order = (Order) o;
            //如果没有支付
            if (!order.getPay()) {
                long delayTime = order.getDelayTime();
                long now = System.currentTimeMillis();
                //如果当前时间大于创建时间+延时时间
                if (now > delayTime) {
                    log.info("删除延时订单：{}", order.getOrderName());
                    nameList.add(order.getOrderName());
                }
            }
        });

        //移除过期的订单
        nameList.forEach(ORDER_MAP::remove);

        //任务走完之后1秒，再次执行
        try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    /**
     * 定时任务 - 逻辑删除未支付的订单  每15秒一次
     */
    //@Scheduled(cron = "0/15 * * * * ?")
    private void removeOrder() {
        //查询延时时间小于等于当前时间，未删除，未支付的订单
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.lt("delay_time", System.currentTimeMillis());
        wrapper.eq("is_pay", false);

        List<Order> orderList = this.orderMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        orderList.forEach(order -> {
            log.info("超时删除未支付的订单：{}", order.getOrderName());
            this.orderMapper.updateOrderToDeletedById(order.getId());

        });

    }
}
