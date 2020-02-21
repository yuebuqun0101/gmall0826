package com.atguigu.gmallpublisher.mapper;

import java.util.List;
import java.util.Map;

/**
 * @author Chijago
 * @create 2020-02-21 22:05
 */
public interface OrderMapper {

    public Double selectOrderAmountTotal(String date);

    public List<Map> selectOrderAmountHourMap(String date);
}
