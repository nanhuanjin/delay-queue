package com.breeze.delay.queue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.breeze.delay.queue.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author breeze
 * @since 2020-05-12
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    int updateOrderToDeletedById(Integer id);

    int updateOrderToDeletedByOrderName(String orderName);
}
