package com.ziya05.subtitle.time

import com.ziya05.entities.SubtitleTime

class AssSubtitleTimeConverter extends SubtitleTimeConverter {
  override def convert(time: SubtitleTime): String = {
    "%d:%02d:%02d.%02d".format(time.hour, time.minute, time.second, time.millisecond)
  }
}
