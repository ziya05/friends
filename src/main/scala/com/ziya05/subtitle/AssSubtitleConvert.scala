package com.ziya05.subtitle

import com.ziya05.subtitle.time.{AssSubtitleTimeConvert, LrcSubtitleTimeParse, SubtitleTimeConvert, SubtitleTimeParse}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

class AssSubtitleConvert(spark:SparkSession)
  extends SubtitleConvert(spark) {
  val scriptInfo =
    """Script Info
      |; Script generated by Aegisub 3.0.2
      |; http://www.aegisub.org/
      |Title: Default Aegisub file
      |ScriptType: v4.00+
      |WrapStyle: 0
      |ScaledBorderAndShadow: yes
      |Collisions: Normal
      |PlayResX: 1280
      |PlayResY: 720
      |Video Zoom Percent: 0.625
      |Scroll Position: 15
      |Active Line: 20
      |Last Style Storage: Default
      |Video File: F:\\utorrent\\1994-Friends S01-720p\\1994-Friends-S01E01.mp4
      |Video Aspect Ratio: c1.77778
      |Video Position: 771
      |YCbCr Matrix: TV.601""".stripMargin

  val format = "Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text"

  override def run():String = {
    import spark.implicits._
    val strDialogue =
      dialogueList
          .sort("_1")
        .map(x => {
          val timeConvert: SubtitleTimeConvert = new AssSubtitleTimeConvert
          //val bt = timeConvert.convert(timeParse.parse(x._1))
          //val et = timeConvert.convert(timeParse.parse(x._2))

          generateDialogue(x._1, x._2, x._3)
        })
      .collect()
      .mkString("\n")

    s"$scriptInfo\n\nEvents\n$format$strDialogue"
    //spark.sparkContext.parallelize(Seq(s"$scriptInfo\n\nEvents\n$format$strDialogue"), 1)
  }

  def generateDialogue(beginTime:String, endTime:String, text:String):String = {
    s"Dialogue: 0,$beginTime,$endTime,eng,,0,0,0,,$text"
  }
}
