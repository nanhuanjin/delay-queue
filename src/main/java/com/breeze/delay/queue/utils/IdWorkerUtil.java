package com.breeze.delay.queue.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author breeze
 * @date 2020/5/12
 *
 * 生成时间戳 - 订单唯一编号
 */
public class IdWorkerUtil {

    public static String getTimeId() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String format = simpleDateFormat.format(new Date());

        Random random = new Random();
        int prefix = random.nextInt(90000) + 9999;

        return format + prefix;
    }
}
