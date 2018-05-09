package com.ziya05.subtitle

import com.ziya05.subtitle.time.SubtitleTimeParse
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}

abstract class SubtitleConvert(spark:SparkSession)
  extends Serializable {
  var dialogueList:Dataset[(String, String, String)] = null
  var timeParse:SubtitleTimeParse = null

  def setDialogue(ds:Dataset[(String, String, String)]) = {
    dialogueList = ds
  }

  def setTimeParse(parse:SubtitleTimeParse) = {
    timeParse = parse
  }

  def convert():String = {
    if (dialogueList == null) {
      throw new NullPointerException("dialogueList cannot be null")
    }

    if (timeParse == null) {
      throw new NullPointerException("timeParse cannot be null")
    }

    run()
  }

  protected def run():String
}
