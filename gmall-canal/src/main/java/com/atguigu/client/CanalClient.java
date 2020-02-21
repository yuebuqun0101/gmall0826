package com.atguigu.client;

import ch.qos.logback.classic.db.names.TableName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.atguigu.constants.GmallConstants;
import com.atguigu.utils.KafkaSender;
import com.google.protobuf.InvalidProtocolBufferException;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author Chijago
 * @create 2020-02-21 20:06
 */
public class CanalClient {

    public static void main(String[] args) {

        //获取连接
        CanalConnector canalConnector = CanalConnectors
                .newSingleConnector(new InetSocketAddress("hadoop102", 11111), "example", "", "");

        while (true) {

            //连接canal
            canalConnector.connect();

            //订阅监控的数据库
            canalConnector.subscribe("gmall.*");

            //抓取数据
            Message message = canalConnector.get(100);

            //判断当前是否有数据更新
            if (message.getEntries().size()==0) {

                System.out.println("没有数据，休息一下...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {

                //解析数据
                for (CanalEntry.Entry entry : message.getEntries()) {

                    //判断当前的Entry类型,过滤掉类似于事务开启与关闭的操作
                    if (CanalEntry.EntryType.ROWDATA.equals(entry.getEntryType())) {

                        //解析entry获取表名
                        String tableName = entry.getHeader().getTableName();

                        try {
                            //获取并解析StoreValue
                            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                            //行数据集
                            List<CanalEntry.RowData> rowDatasList = rowChange.getRowDatasList();
                            //INSERT UPDATE ALTER DELETE
                            CanalEntry.EventType eventType = rowChange.getEventType();

                            //处理数据并发送至Kafka
                            handler(tableName,rowDatasList,eventType);

                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    private static void handler(String tableName, List<CanalEntry.RowData> rowDatasList, CanalEntry.EventType eventType) {

        if ("order_info".equals(tableName) && CanalEntry.EventType.INSERT.equals(eventType)) {

            //解析rowDatasList
            for (CanalEntry.RowData rowData : rowDatasList) {

                JSONObject jsonObject = new JSONObject();

                //解析每一行数据
                for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                    jsonObject.put(column.getName(), column.getValue());
                }

                //测试打印
                System.out.println(jsonObject.toString());

                //发送至Kafka
                KafkaSender.send(GmallConstants.GMALL_ORDER_TOPIC,jsonObject.toString());
            }
        }
    }
}
