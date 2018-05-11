package com.ziya05.utils

import com.ziya05.entities.SubtitleTime

object ProjectUtils extends Serializable {
  private val configs = new LrcProjectConfigs

  def getConfigs: ProjectConfigs = {
    configs
  }

  def getStdTimeString(time: SubtitleTime): String = {
    "%02d:%02d:%02d.%03d".format(time.hour, time.minute, time.second, time.millisecond)
  }

  def getSubtitleTime(timeStr: String): SubtitleTime = {
    val reg = """^(\d{2}):(\d{2}):(\d{2})\.(\d{3})$""".r
    val reg(hour, minute, second, millisecond) = timeStr

    SubtitleTime(hour.toInt, minute.toInt, second.toInt, millisecond.toInt)
  }
}
