package com.atguigu.app

import java.text.SimpleDateFormat
import java.util.Date

import com.alibaba.fastjson.JSON
import com.atguigu.bean.StartUpLog
import com.atguigu.constants.GmallConstants
import com.atguigu.handler.DauHandler
import com.atguigu.utils.MyKafkaUtil
import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * @author Chijago
 * @create 2020-02-20 20:29
 */
object DauApp {

  def main(args: Array[String]): Unit = {

    //1.创建SparkConf
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("DauApp")

    //2.创建StreamingContext
    val ssc = new StreamingContext(sparkConf, Seconds(4))

    //3.读取Kafka数据并创建DStream
    val kafkaDStream: InputDStream[(String, String)] = MyKafkaUtil.getKafkaStream(ssc, Set(GmallConstants.GMALL_STARTUP_TOPIC))

    //定义时间格式化对象
    val sdf = new SimpleDateFormat("yyyy-MM-dd HH")

    //4.将每一行收据转换为样例类对象
    val startLogDStream: DStream[StartUpLog] = kafkaDStream.map { case (_, value) =>
      //将数据转换成样例类对象
      val log: StartUpLog = JSON.parseObject(value, classOf[StartUpLog])
      //取出时间戳
      val ts: Long = log.ts
      //将时间戳转换为字符串
      val logDateHour: String = sdf.format(new Date(ts))
      //截取字段
      val logDateHourArr: Array[String] = logDateHour.split(" ")
      //赋值日期和小时
      log.logDate = logDateHourArr(0)
      log.logHour = logDateHourArr(1)

      log
    }

    //5.跨批次去重(根据Redis中的数据进行去重)
    val filterByRedis: DStream[StartUpLog] = DauHandler.filterDataByRedis(startLogDStream)

    //6.同批次去重
    val filterByBatch: DStream[StartUpLog] = DauHandler.filterDataByBatch(filterByRedis)

    //7.将数据保存至Redis，以供下一次去重使用
    DauHandler.saveMidToRedis(filterByBatch)

    //8.有效数据(不做计算)写入HBase
    import org.apache.phoenix.spark._
    filterByBatch.foreachRDD(rdd =>
      rdd.saveToPhoenix("GMALL190826_DAU",
        Seq("MID", "UID", "APPID", "AREA", "OS", "CH", "TYPE", "VS", "LOGDATE", "LOGHOUR", "TS") ,
        new Configuration,Some("hadoop102,hadoop103,hadoop104:2181"))
    )

    //启动任务
    ssc.start()
    ssc.awaitTermination()
  }
}
