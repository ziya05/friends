package com.ziya05.Subtitle
import com.ziya05.entities.SubtitleTime

class AssSubtitleTimeConvert extends SubtitleTimeConvert {
  override def convert(time: SubtitleTime): String = {
    "%d:%02d:%02d.%02d".format(time.hour, time.minute, time.second, time.millisecond)
  }
}
