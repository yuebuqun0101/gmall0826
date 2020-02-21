package com.atguigu.utils

import java.io.InputStreamReader
import java.util.Properties

/**
 * @author Chijago
 * @create 2020-02-20 19:56
 */
object PropertiesUtil {

  def load(propertiesName: String): Properties = {
    val prop = new Properties()
    prop.load(new InputStreamReader(Thread.currentThread().getContextClassLoader.getResourceAsStream(propertiesName), "UTF-8"))
    prop
  }
}