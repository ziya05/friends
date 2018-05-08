package com.ziya05.utils

object Constants {
  val SRC_FILE = "hdfs://master:9000/friends/*_EN.lrc"
  val TAR_FILE = "hdfs://master:9000/friends_new/"

  val PTN_EPI = """.*(\d+)[xX](\d+).*"""
  val PTN_CONTENT = """(\[.*?\])(.*)"""
  val PTN_CTN_SPLIT_CHARS = """[ ,.!?:]"""
}
