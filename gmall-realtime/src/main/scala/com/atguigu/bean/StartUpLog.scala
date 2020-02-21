package com.atguigu.bean

/**
 * @author Chijago
 * @create 2020-02-20 20:24
 */
case class StartUpLog(mid: String,
                      uid: String,
                      appId: String,
                      area: String,
                      os: String,
                      ch: String,
                      `type`: String,
                      vs: String,
                      var logDate: String,
                      var logHour: String,
                      var ts: Long)
