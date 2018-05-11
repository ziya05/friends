package com.ziya05.subtitle.parser

import com.ziya05.subtitle.time.{LrcSubtitleTimeParser, SubtitleTimeParser}
import com.ziya05.utils.{Constants, ProjectUtils}
import org.apache.spark.sql.functions.input_file_name
import org.apache.spark.sql.{Dataset, SparkSession}

class LrcSubtitleParser(spark: SparkSession)
  extends SubtitleParser(spark) {
  val timeParser = new LrcSubtitleTimeParser

  override protected def run(path: String): Dataset[(String, String, String, String, String)] = {
    import spark.implicits._
    val ds = spark.read
      .text(path)
      .select(input_file_name, $"value")
      .as[(String, String)]
      .map(fv => {
        val regEpi = ProjectUtils.getConfigs.getPtnEpi().r
        val regEpi(season, episode) = fv._1

        val regCtn = ProjectUtils.getConfigs.getPtnContent().r
        val regCtn(time, content) = fv._2
        (season, episode, time, content.trim)
      })

    val timeList = ds.filter(x => x._4.isEmpty)
      .map(x => (x._1, x._2, x._3))
      .collect()
      .toList

    val mainData = ds.filter(x => !x._4.isEmpty)

    mainData.map(x => {
      var endTime = x._3
      val d = timeList.filter(y => y._1 == x._1 && y._2 == x._2 && y._3 > x._3)
        .map(y => y._3)
        .sorted

      if (d.length > 0) {
        endTime = d.head
      }

      (x._1, x._2,
        getStdTimeString(x._3),
        getStdTimeString(endTime),
        x._4)
    })
  }

  override protected def getTimeParser(): SubtitleTimeParser = {
    timeParser
  }

}
