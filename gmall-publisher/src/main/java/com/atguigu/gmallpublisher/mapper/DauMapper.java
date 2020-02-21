package com.atguigu.gmallpublisher.mapper;

import java.util.List;
import java.util.Map;

/**
 * @author Chijago
 * @create 2020-02-21 0:50
 */
public interface DauMapper {

    //获取总数
    public int getTotal(String date);

    //获取分时统计
    public List<Map> selectDauTotalHourMap(String date);
}
