package com.ziya05.subtitle.time

import com.ziya05.entities.SubtitleTime

abstract class SubtitleTimeParse extends Serializable{
  def parse(time:String):SubtitleTime
}
