package com.ziya05.subtitle.time

import com.ziya05.entities.SubtitleTime

abstract class SubtitleTimeConvert extends Serializable{
  def convert(time:SubtitleTime):String
}
