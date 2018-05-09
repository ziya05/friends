package com.ziya05.Subtitle

import com.ziya05.entities.SubtitleTime

abstract class SubtitleTimeParse {
  def parse(time:String):SubtitleTime
}
