package com.ziya05

import com.ziya05.utils.Constants
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.functions.{count, desc, input_file_name}

object GetToken {
  def main(args:Array[String]):Unit = {
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
        .filter(x => !x._4.isEmpty)
        .map(x => {
          (x._1, x._2, x._3,
            x._4.split(Constants.PTN_CTN_SPLIT_CHARS))
        })

    val statDf = ds.flatMap(x => {
      for (word <- x._4)
        yield (x._1, x._2, x._3, word)
    }).filter(x => !x._4.isEmpty)
      .toDF("season", "episode", "time", "word")

//    val statByEpiDf = statDf
//        .groupBy("season", "episode", "word")
//        .agg(count("word").alias("quantity"))
//        .select("season", "episode", "word", "quantity")
//        .sort("season", "episode", "quantity")

    val statBySeaDs = statDf
        .groupBy("season", "word")
        .agg(count("word").alias("quantity"))
        .select("season", "word", "quantity")
        .orderBy($"season", $"quantity")
        .filter($"quantity" < 5)

    //println(statBySeaDs.schema)
      .map{
        case Row(season:String, word:String, quantity:Long) => {
          (season, word)
        }
      }
      .rdd.groupByKey()
      .map(x => (x._1, x._2.toSeq))
      .toDS()

    val result = ds.joinWith(statBySeaDs,
      ds("_1") === statBySeaDs("_1"))
        .map(x => {
          val data = x._1._4.intersect(x._2._2)
          (x._1._1, x._1._2, x._1._3, data.mkString(" "))
        })


    result
        .filter(x => x._1 == "1" && x._2 == "01")
      .collect()
      .sortBy(x => x._3)
      .foreach(println)

    spark.close()
  }
}
