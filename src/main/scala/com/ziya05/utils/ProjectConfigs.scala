package com.ziya05.utils

abstract class ProjectConfigs {
  protected val SRC_FILE = "SRC_FILE"
  protected val TAR_FILE = "TAR_FILE"
  protected val PTN_EPI = "PTN_EPI"
  protected val PTN_CONTENT = "PTN_CONTENT"
  protected val PTN_CTN_SPLIT_CHARS = "PTN_CTN_SPLIT_CHARS"

  private val cs = Map[String, String](
    SRC_FILE -> s"${Constants.HDFS}friends/*_EN.lrc",
    TAR_FILE -> s"${Constants.HDFS}/friends_new/",
    PTN_EPI -> """.*(\d+)[xX](\d+).*""",
    PTN_CONTENT -> """(\[.*?\])(.*)""",
    PTN_CTN_SPLIT_CHARS -> """[ ,.!?:]""")

  var configMap:Map[String, String] = null

  def getSrcFile():String = {
    getValue(SRC_FILE)
  }

  def getTarFile():String = {
    getValue(TAR_FILE)
  }

  def getPtnEpi():String = {
    getValue(PTN_EPI)
  }

  def getPtnContent():String = {
    getValue(PTN_CONTENT)
  }

  def getPtnCtnSplitChars():String = {
    getValue(PTN_CTN_SPLIT_CHARS)
  }

  def getValue(key:String):String = {
    if (configMap == null) {
      configMap = cs ++ getMap()
    }

    configMap.getOrElse(key, "")
  }

  protected def getMap():Map[String, String]
}
