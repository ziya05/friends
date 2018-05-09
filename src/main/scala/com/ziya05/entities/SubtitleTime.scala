package com.ziya05.entities

class SubtitleTime(val hour:Int, val minute:Int, val second:Int, val millisecond:Int) {
}

object SubtitleTime {
  def apply(hour:Int, minute:Int, second:Int, millisecond:Int) = {
    new SubtitleTime(hour, minute, second, millisecond)
  }
}