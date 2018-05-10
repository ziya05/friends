package com.ziya05.subtitle.converter

import com.ziya05.subtitle.time.SubtitleTimeParser
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}

abstract class SubtitleConverter(spark:SparkSession)
  extends Serializable {
  var dialogueList:Dataset[(String, String, String)] = null
  var timeParse:SubtitleTimeParser = null

  def setDialogue(ds:Dataset[(String, String, String)]) = {
    dialogueList = ds
  }

  def setTimeParse(parse:SubtitleTimeParser) = {
    timeParse = parse
  }

  def convert():RDD[String] = {
    if (dialogueList == null) {
      throw new NullPointerException("dialogueList cannot be null")
    }

    if (timeParse == null) {
      throw new NullPointerException("timeParse cannot be null")
    }

    run()
  }

  protected def run():RDD[String]
}
