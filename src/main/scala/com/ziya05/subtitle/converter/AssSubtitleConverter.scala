package com.ziya05.subtitle.converter

import com.ziya05.subtitle.time.{AssSubtitleTimeConverter, SubtitleTimeConverter}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}

class AssSubtitleConverter(spark: SparkSession)
  extends SubtitleConverter(spark) {
  val scriptInfo =
    """Script Info
      |; Script generated by Ziya
      |; http://www.ziya05.github.io/
      |Title: Friends
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
      |Video File: null
      |Video Aspect Ratio: c1.77778
      |Video Position: 771
      |YCbCr Matrix: TV.601""".stripMargin

  val v4Styles =
    """V4+ Styles
      |Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding
      |Style: chinese,黑体,40,&H00E3E2E1,&H000000FF,&H006E2500,&H00000000,-1,0,0,0,100,100,0,0,1,2,1,2,10,10,10,1
      |Style: eng,Ebrima,36,&H00FFFFFF,&H00040405,&H003A3A3C,&H00050404,-1,0,0,0,100,100,0,0,1,1,2,2,10,10,5,1
      |Style: F6-标题,华文行楷,53,&H00FFFFFF,&H000000FF,&H00000000,&H00000000,-1,0,0,0,100,100,0,0,1,2,0,2,10,10,10,1
      |Style: F6-英文字体,Gabriel Weiss' Friends Font,35,&H00EDEDEF,&H00040405,&H001B314E,&H00050404,-1,0,0,0,100,100,0,0,1,0,2,2,10,10,20,1
      |Style: 歌词,微软雅黑,40,&H00EDEDEF,&H00040405,&H005C3203,&H00050404,-1,-1,0,0,100,100,0,0,1,0,2,8,10,10,5,134
      |Style: 片头,方正黑体简体,43,&H00FFFFFF,&H000000FF,&H001B314E,&H00000000,-1,0,0,0,100,100,0,0,1,0,1,2,10,10,10,1
      |Style: 滚动,SimHei,28,&H00FFFFFF,&H000000FF,&H00606062,&H004A4A4C,-1,-1,0,0,100,100,0,0,1,0,3,2,10,10,10,1
      |Style: name,微软雅黑,30,&H00EDEDEF,&H00040405,&H005C3203,&H00050404,-1,0,0,0,100,100,0,0,1,0,2,2,10,10,5,134
      |Style: note,微软雅黑,50,&H00EDEDEF,&H00040405,&H005C3203,&H00050404,-1,0,0,0,100,100,0,0,1,0,2,2,10,10,5,134""".stripMargin

  val format = "Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text"

  val timeConverter = new AssSubtitleTimeConverter

  override def run(ds:Dataset[(String, String, String)]): RDD[String] = {
    import spark.implicits._
    val strDialogue =
      ds
        .sort("_1")
        .map(x => {
          generateDialogue(
            getSpeTimeString(x._1),
            getSpeTimeString(x._2),
            x._3)
        })
        .collect()
        .mkString("\n")

    spark
      .sparkContext
      .parallelize(Seq(s"$scriptInfo\n\n$v4Styles\n\nEvents\n$format$strDialogue"), 1)
  }

  override protected def getTimeConverter(): SubtitleTimeConverter = {
    timeConverter
  }

  def generateDialogue(beginTime: String, endTime: String, text: String): String = {
    s"Dialogue: 0,$beginTime,$endTime,eng,,0,0,0,,$text"
  }
}
