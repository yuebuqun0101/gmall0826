package com.atguigu.gmallpublisher.service.impl;

import com.atguigu.gmallpublisher.mapper.DauMapper;
import com.atguigu.gmallpublisher.mapper.OrderMapper;
import com.atguigu.gmallpublisher.service.DauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Chijago
 * @create 2020-02-21 0:48
 */

@Service
public class DauServiceImpl implements DauService {

    @Autowired
    DauMapper dauMapper;

    @Autowired
    OrderMapper orderMapper;

    @Override
    public int getTotal(String date) {
        return dauMapper.getTotal(date);
    }

    @Override
    public Map getRealTimeHours(String date) {

        //查询数据
        List<Map> list = dauMapper.selectDauTotalHourMap(date);

        //创建Map存放结果数据
        HashMap<String, Long> map = new HashMap<>();

        //遍历集合，将集合中的数据改变结构存放至map中
        for (Map map1 : list) {
            map.put((String) map1.get("LH"), (Long) map1.get("CT"));
        }

        return map;
    }

    @Override
    public Double getOrderAmountTotal(String date) {
        return orderMapper.selectOrderAmountTotal(date);
    }

    @Override
    public Map getOrderAmountHourMap(String date) {

        //获取分时统计的GMV数据
        List<Map> list = orderMapper.selectOrderAmountHourMap(date);

        //构建Map接收结构数据
        HashMap<String, Double> result = new HashMap<>();

        //遍历list将结构转换存入result
        for (Map map : list) {
            result.put((String) map.get("CREATE_HOUR"), (Double) map.get("SUM_AMOUNT"));
        }
        return result;
    }
}
