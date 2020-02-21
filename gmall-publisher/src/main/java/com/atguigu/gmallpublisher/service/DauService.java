package com.atguigu.gmallpublisher.service;

import java.util.Map;

/**
 * @author Chijago
 * @create 2020-02-21 0:46
 */
public interface DauService {

    //获取总数
    public int getTotal(String date);

    //获取分时统计的数据
    public Map getRealTimeHours(String date);


    public Double getOrderAmountTotal(String date);
    public Map getOrderAmountHourMap(String date);
}
