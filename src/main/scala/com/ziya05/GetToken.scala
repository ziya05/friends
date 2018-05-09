package com.ziya05

import com.ziya05.Subtitle.{AssSubtitleTimeConvert, LrcSubtitleTimeParse, SubtitleTimeConvert, SubtitleTimeParse}
import com.ziya05.utils.Constants
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.functions.{count, input_file_name}

object GetToken {
  def main(args:Array[String]):Unit = {
    val quantityStd = 10
    val spark = SparkSession
      .builder()
      .appName("GetToken")
      .getOrCreate()

    import spark.implicits._
    val ds = spark.read
      .text(Constants.SRC_FILE)
        .select(input_file_name, $"value")
        .as[(String, String)]
        .map(fv => {
          val regEpi = Constants.PTN_EPI.r
          val regEpi(season, episode) = fv._1

          val regCtn = Constants.PTN_CONTENT.r
          val regCtn(time, content) = fv._2
          (season, episode, time, content.trim)
        })


    val timeList = ds.filter(x => x._4.isEmpty)
      .map(x => (x._1, x._2, x._3))
      .collect()
      .toList

    val mainData = ds.filter(x => !x._4.isEmpty)
      .map(x => {
        (x._1, x._2, x._3,
          x._4.split(Constants.PTN_CTN_SPLIT_CHARS))
      })

    val statDf = mainData.flatMap(x => {
      for (word <- x._4)
        yield (x._1, x._2, x._3, word)
    }).filter(x => !x._4.isEmpty)
      .toDF("season", "episode", "time", "word")

    val statBySeaDs = statDf
        .groupBy("season", "word")
        .agg(count("word").alias("quantity"))
        .select("season", "word", "quantity")
        .orderBy($"season", $"quantity")
        .filter($"quantity" < quantityStd)
        .map{
          case Row(season:String, word:String, quantity:Long) => {
            (season, word)
          }
        }
        .rdd.groupByKey()
        .map(x => (x._1, x._2.toSeq))
        .toDS()

    val result = mainData.joinWith(statBySeaDs,
      mainData("_1") === statBySeaDs("_1"))
        .map(x => {
          val data = x._1._4.intersect(x._2._2)

          var endTime = "----"
          val et = timeList.filter(y => x._1._1 == y._1 && x._1._2 == y._2 && x._1._3 < y._3)
            .map(y => y._3)
            .sorted

          if (et.length > 0)
            endTime = et.head

          (x._1._1, x._1._2, x._1._3, endTime, data.mkString(" "))
        }).filter(x => x._1 == "1" && x._2 == "01")
      .map(x => (x._3, x._4, x._5))
      .sort("_1")
      .map(x => generateDialogue(x._1, x._2, x._3))
      .repartition(1)

    result.persist()

    result
      .rdd
      .saveAsTextFile(s"${Constants.TAR_FILE}1x01_ENG")

    result
      .collect()
        .foreach(println)

    result.unpersist()

    spark.close()
  }

  def generateDialogue(beginTime:String, endTime:String, text:String):String = {
    val timeParse:SubtitleTimeParse = new LrcSubtitleTimeParse
    val timeConvert: SubtitleTimeConvert = new AssSubtitleTimeConvert

    val bt = timeConvert.convert(timeParse.parse(beginTime))
    val et = timeConvert.convert(timeParse.parse(endTime))

    s"Dialogue: 0,$bt,$et,eng,,0,0,0,,$text"
  }
}
