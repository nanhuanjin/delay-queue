package com.breeze.delay.queue.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author breeze
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("dream_order")
@ApiModel(value="Order对象", description="订单表")
public class Order extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单编号")
    private String orderCode;

    @ApiModelProperty(value = "订单名称")
    private String orderName;

    @ApiModelProperty(value = "超时时间")
    private Long delayTime;

    @ApiModelProperty(value = "是否支付 0-未支付 1-已支付")
    @TableField("is_pay")
    private Boolean pay;

    @ApiModelProperty(value = "是否删除 0-未删除 1-已删除")
    @TableLogic
    @TableField("is_deleted")
    private Boolean deleted;

    public void setOrder(long time, TimeUnit unit) {
        this.delayTime = System.currentTimeMillis() + (time > 0 ? unit.toMillis(time) : 0);
    }

}
