package com.ziya05.subtitle.parser

import com.ziya05.subtitle.time.SubtitleTimeParser
import com.ziya05.utils.ProjectUtils
import org.apache.spark.sql.{Dataset, SparkSession}

abstract class SubtitleParser(spark: SparkSession) extends Serializable {
  def parse(path: String): Dataset[(String, String, String, String, String)] = {
    run(path)
  }

  def getStdTimeString(srcTimeStr:String):String = {
    val timeParser = getTimeParser()
    ProjectUtils.getStdTimeString(timeParser.parse(srcTimeStr))
  }

  protected def run(path: String): Dataset[(String, String, String, String, String)]

  protected def getTimeParser(): SubtitleTimeParser
}
