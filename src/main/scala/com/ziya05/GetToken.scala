package com.ziya05

import com.ziya05.subtitle.converter.AssSubtitleConverter
import com.ziya05.subtitle.parser.LrcSubtitleParser
import com.ziya05.utils.ProjectUtils
import org.apache.spark.sql.functions.count
import org.apache.spark.sql.{Row, SparkSession}

object GetToken {
  def main(args: Array[String]): Unit = {
    val quantityStd = 100
    val spark = SparkSession
      .builder()
      .appName("GetToken")
      .getOrCreate()

    val parser = new LrcSubtitleParser(spark)
    val ds = parser.parse(ProjectUtils.getConfigs.getSrcFile())

    import spark.implicits._
    val mainData = ds
      .map(x => {
        (x._1, x._2, x._3, x._4,
          x._5.split(ProjectUtils.getConfigs.getPtnCtnSplitChars()))
      })

    val statDf = mainData.flatMap(x => {
      for (word <- x._5)
        yield (x._1, x._2, x._3, x._4, word)
    }).filter(x => !x._5.isEmpty)
      .toDF("season", "episode", "beginTime", "endTime", "word")

    val statBySeaDs = statDf
      .groupBy("season", "word")
      .agg(count("word").alias("quantity"))
      .select("season", "word", "quantity")
      .orderBy($"season", $"quantity")
      .filter($"quantity" < quantityStd)
      .map {
        case Row(season: String, word: String, quantity: Long) => {
          (season, word)
        }
      }
      .rdd.groupByKey()
      .map(x => (x._1, x._2.toSeq))
      .toDS()

    val result = mainData.joinWith(statBySeaDs,
      mainData("_1") === statBySeaDs("_1"))
      .map(x => {
        val data = x._1._5.intersect(x._2._2)

        (x._1._1, x._1._2, x._1._3, x._1._4, data.mkString(" "))
      }).filter(x => x._1 == "1" && x._2 == "01")
      .map(x => (x._3, x._4, x._5))

    val convert = new AssSubtitleConverter(spark)
    val rdd = convert.convert(result)
    rdd.persist()
    rdd.saveAsTextFile(s"${ProjectUtils.getConfigs.getTarFile()}1x01_ENG")
    rdd.collect()
      .foreach(println)
    rdd.unpersist()

    spark.close()
  }
}
