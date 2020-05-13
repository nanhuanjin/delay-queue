package com.breeze.delay.queue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.breeze.delay.queue.consts.AppConst;
import com.breeze.delay.queue.entity.Order;
import com.breeze.delay.queue.mapper.OrderMapper;
import com.breeze.delay.queue.service.ZSetDelayQueueService;
import com.breeze.delay.queue.utils.IdWorkerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author breeze
 * @date 2020/5/12
 *
 * Redis的Zset实现延时队列
 *      1.Redis的数据结构Zset，同样可以实现延迟队列的效果，主要利用它的score属性，
 *        redis通过score来为集合中的成员进行从小到大的排序。
 *      2.通过zadd命令向队列delayqueue 中添加元素，并设置score值表示元素过期的时间；
 *        向delayqueue 添加三个order1、order2、order3，分别是10秒、20秒、30秒后过期。
 *      3.消费端轮询队列delayqueue， 将元素排序后取最小时间与当前时间比对，如小于当前时间代表已经过期移除key。
 */
@Service
@Slf4j
public class ZSetDelayQueueServiceImpl implements ZSetDelayQueueService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public int saveOrder(Order order) {

        //设置订单编号
        order.setOrderCode(IdWorkerUtil.getTimeId());
        //设置延时时间为15s
        order.setOrder(AppConst.ORDER_TIMEOUT, TimeUnit.SECONDS);
        order.setPay(false);

        //存入Zset 键-delay-queue 值-订单名称  score-过期时间
        this.redisTemplate.opsForZSet().add(AppConst.DELAY_QUEUE, order.getOrderName(), order.getDelayTime());
        //启动线程处理延时队列
        this.threadPoolExecutor.execute(this::delayQueue);

        //处理保存的逻辑
        return this.orderMapper.insert(order);
    }

    @Override
    public int payOrder(String orderName) {

        //删除Redis中订单信息
        Long remove = this.redisTemplate.opsForZSet().remove(AppConst.DELAY_QUEUE, orderName);

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

        //获取Redis中订单集合
        Set<String> orderZSet = this.redisTemplate.opsForZSet().range(AppConst.DELAY_QUEUE, 0, -1);

        //获取数据库订单集合
        List<Order> orderList = this.orderMapper.selectList(null);

        //最终返回结果集
        Map<String, Object> result = new HashMap<>(2);
        result.put("orderNameSet", orderZSet);
        result.put("orderList", orderList);

        return result;
    }


    /**
     * Redis ZSet版本 - 延时队列
     */
    private void delayQueue() {

        while (true) {
            //获取Redis中订单集合
            //通过索引区间返回有序集合成指定区间内的成员，其中有序集成员按分数值递增(从小到大)顺序排列
            Set<String> orderZSet = this.redisTemplate.opsForZSet().range(AppConst.DELAY_QUEUE, 0, -1);

            if (CollectionUtils.isEmpty(orderZSet)) {
                return;
            }

            //因为是从小到大排序，所以直接获取第一个最小的
            String orderName = (String) orderZSet.toArray()[0];

            //获取延时队列中 指定key的score
            Double score = this.redisTemplate.opsForZSet().score(AppConst.DELAY_QUEUE, orderName);
            if (score != null) {
                long delayTime = score.longValue();
                long currentTimeMillis = System.currentTimeMillis();
                //当前时间大于延时时间
                if (currentTimeMillis > delayTime) {
                    //删除Redis中订单信息
                    Long remove = this.redisTemplate.opsForZSet().remove(AppConst.DELAY_QUEUE, orderName);
                    log.info("Redis移除过期订单：{}", orderName);

                    //逻辑删除数据库中的订单信息
                    this.orderMapper.updateOrderToDeletedByOrderName(orderName);
                }
            }

            try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

}
