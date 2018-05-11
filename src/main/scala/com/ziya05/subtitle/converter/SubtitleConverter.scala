package com.ziya05.subtitle.converter

import com.ziya05.subtitle.time.SubtitleTimeConverter
import com.ziya05.utils.ProjectUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}

abstract class SubtitleConverter(spark:SparkSession)
  extends Serializable {

  def convert(ds:Dataset[(String, String, String)]):RDD[String] = {
    run(ds)
  }

  def getSpeTimeString(stdTimeString:String):String = {
    val time = ProjectUtils.getSubtitleTime(stdTimeString)
    getTimeConverter().convert(time)
  }

  protected def run(ds:Dataset[(String, String, String)]):RDD[String]

  protected def getTimeConverter(): SubtitleTimeConverter
}
