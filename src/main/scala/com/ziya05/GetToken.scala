package com.ziya05

import com.ziya05.utils.Constants
import org.apache.spark.sql.SparkSession


object GetToken {
  def main(args:Array[String]):Unit = {
    val spark = SparkSession
      .builder()
      .appName("GetToken")
      .getOrCreate()

    import spark.implicits._
    import org.apache.spark.sql.functions.input_file_name
    val ds = spark.read
      .text(Constants.SRC_FILE)
        .select(input_file_name, $"value")
        .as[(String, String)]
        .map(fv => {
          val regEpi = Constants.PTN_EPI.r
          val regEpi(season, episode) = fv._1

          val regContent = Constants.PTN_CONTENT.r
          val regContent(time, content) = fv._2
          (season, episode, time, content.trim)
        })
        .filter(x => !x._4.isEmpty)

    ds.collect().foreach(println)

    spark.close()

  }
}
