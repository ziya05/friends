package com.ziya05

import org.apache.spark.sql.SparkSession

object GetToken {
  def main(args:Array[String]):Unit = {
    val spark = SparkSession
      .builder()
      .appName("GetToken")
      .getOrCreate()

    val ds = spark.read.textFile("hdfs://master:9000/friends/*_EN.lrc")

    ds.collect().foreach(println)

    spark.close()

  }
}
