package com.ziya05.subtitle.time

import com.ziya05.entities.SubtitleTime

class LrcSubtitleTimeParser extends SubtitleTimeParser {
  override def parse(time: String): SubtitleTime = {
    val reg = """\[(\d{2}):(\d{2})\.(\d{2})\]""".r
    val reg(minute, second, millisecond) = time

    SubtitleTime(0, minute.toInt, second.toInt, millisecond.toInt)
  }
}
