package com.breeze.delay.queue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.breeze.delay.queue.entity.Order;
import com.breeze.delay.queue.mapper.OrderMapper;
import com.breeze.delay.queue.service.OrderService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author breeze
 * @since 2020-05-12
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

}
