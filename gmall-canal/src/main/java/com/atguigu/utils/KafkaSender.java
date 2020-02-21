package com.atguigu.utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * @author Chijago
 * @create 2020-02-21 20:45
 */
public class KafkaSender {

    private static KafkaProducer<String,String> kafkaProducer;

    //使用Kafka生产者发送数据至Kafka
    public static void send(String topic, String data) {

        if (kafkaProducer == null) {
            kafkaProducer = createKafkaProducer();
        }

        //发送数据
        kafkaProducer.send(new ProducerRecord<>(topic,data));
    }

    //创建生产者对象
    private static KafkaProducer<String, String> createKafkaProducer() {

        //创建Kafka生产者配置信息
        Properties properties = PropertiesUtil.load("kafka.config.properties");

        //创建生产者
        kafkaProducer = new KafkaProducer<>(properties);

        return kafkaProducer;
    }
}
