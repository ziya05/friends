package com.ziya05.Subtitle

import com.ziya05.entities.SubtitleTime

class LrcSubtitleTimeParse extends SubtitleTimeParse {
  override def parse(time: String): SubtitleTime = {
    val reg = """\[(\d{2}):(\d{2})\.(\d{2})\]""".r
    val reg(minute, second, millisecond) = time

    SubtitleTime(0, minute.toInt, second.toInt, millisecond.toInt)
  }
}
