package com.atguigu.gmallpublisher.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmallpublisher.service.DauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Chijago
 * @create 2020-02-21 0:39
 */
@RestController
public class PublisherController {

    @Autowired
    DauService dauService;

    @GetMapping("realtime-total")
    public String getRealTimeTotal(@RequestParam("date") String date) {

        //1.获取总数
        int total = dauService.getTotal(date);

        //获取单日GMV
        Double orderAmountTotal = dauService.getOrderAmountTotal(date);

        //2.创建集合用于存放JSON对象
        ArrayList<Map> result = new ArrayList<>();

        //3.创建Map用于存放日活数据
        HashMap<String, Object> dauMap = new HashMap<>();
        dauMap.put("id","dau");
        dauMap.put("name","新增日活");
        dauMap.put("value",total);

        //4.创建Map用于存放新增数据
        HashMap<String, Object> newMidMap = new HashMap<>();
        newMidMap.put("id", "new_mid");
        newMidMap.put("name", "新增设备");
        newMidMap.put("value", "233");

        //添加交易额数据信息
        HashMap<String, Object> gmvMap = new HashMap<>();
        gmvMap.put("id", "order_amount");
        gmvMap.put("name", "新增交易额");
        gmvMap.put("value",orderAmountTotal);


        //5.将日活数据及新增数据添加到集合中
        result.add(dauMap);
        result.add(newMidMap);
        result.add(gmvMap);

        //6.将集合转换为字符串返回
        return JSON.toJSONString(result);
    }

    @GetMapping("realtime-hours")
    public String getRealTimeHours(@RequestParam("id") String id,@RequestParam("date") String date) {

        //创建集合用于存放查询结果
        HashMap<String, Map> result = new HashMap<>();

        if ("dau".equals(id)) {
            //查询数据
            Map todayMap = dauService.getRealTimeHours(date);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar instance = Calendar.getInstance();
            try {
                instance.setTime(sdf.parse(date));
                instance.add(Calendar.DAY_OF_MONTH, -1);
                String yesterday = sdf.format(new Date(instance.getTimeInMillis()));
                Map yesterdayMap = dauService.getRealTimeHours(yesterday);

                result.put("today", todayMap);
                result.put("yesterday", yesterdayMap);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if ("order_amount".equals(id)) {
            //获取今天的交易总额
            Map todayMap = dauService.getOrderAmountHourMap(date);
            //将传入时间日期减一
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar instance = Calendar.getInstance();
            try {
                instance.setTime(sdf.parse(date));
                instance.add(Calendar.DAY_OF_MONTH,-1);
                String yesterday = sdf.format(new Date(instance.getTimeInMillis()));
                Map yesterdayMap = dauService.getOrderAmountHourMap(yesterday);
                result.put("today", todayMap);
                result.put("yesterday", yesterdayMap);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        return JSON.toJSONString(result);
    }
}
