package com.atguigu.app

import com.alibaba.fastjson.JSON
import com.atguigu.bean.OrderInfo
import com.atguigu.constants.GmallConstants
import com.atguigu.utils.MyKafkaUtil
import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * @author Chijago
 * @create 2020-02-21 21:23
 */
object GMVApp {

  def main(args: Array[String]): Unit = {

    //1.创建SparkConf
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("GMVApp")

    //2.创建StreamingContext
    val ssc = new StreamingContext(sparkConf, Seconds(3))

    //3.读取Kafka数据创建流
    val kafkaDStream: InputDStream[(String, String)] = MyKafkaUtil.getKafkaStream(ssc, Set(GmallConstants.GMALL_ORDER_TOPIC))

    //4.将数据转换为样例类对象:手机号脱敏+生成数据的日期及小时
    val orderInfoDStream: DStream[OrderInfo] = kafkaDStream.map { case (_, value) =>

      //将数据转换为样例类对象
      val info: OrderInfo = JSON.parseObject(value, classOf[OrderInfo])

      //手机号脱敏
      val tuple: (String, String) = info.consignee_tel.splitAt(3)
      val tuple1: (String, String) = tuple._2.splitAt(4)

      //拼接新的手机号
      info.consignee_tel = s"${tuple._1}****${tuple1._2}"

      //创建日期和小时
      val create_time: String = info.create_time

      val dateHourArr: Array[String] = create_time.split(" ")

      info.create_date = dateHourArr(0)
      info.create_hour = dateHourArr(1).split(":")(0)

      info
    }

    //5.将数据写入HBase
    orderInfoDStream.foreachRDD(rdd=>{
      import org.apache.phoenix.spark._
      rdd.saveToPhoenix("GMALL190826_ORDER_INFO",
        Seq("ID","PROVINCE_ID", "CONSIGNEE", "ORDER_COMMENT", "CONSIGNEE_TEL", "ORDER_STATUS", "PAYMENT_WAY", "USER_ID","IMG_URL", "TOTAL_AMOUNT", "EXPIRE_TIME", "DELIVERY_ADDRESS", "CREATE_TIME","OPERATE_TIME","TRACKING_NO","PARENT_ORDER_ID","OUT_TRADE_NO", "TRADE_BODY", "CREATE_DATE", "CREATE_HOUR"),
        new Configuration,
        Some("hadoop102,hadoop103,hadoop104:2181"))

    })

    //6.启动任务
    ssc.start()
    ssc.awaitTermination()

  }
}
