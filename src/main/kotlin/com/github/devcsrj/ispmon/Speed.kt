package com.github.devcsrj.ispmon

import java.time.Duration

/**
 * Speed, such as 24Mbps
 */
data class Speed(

  val size: DataSize,
  val time: Duration
) {

  /**
   * The value in Mbps
   */
  fun value(): Float {
    val megabit = size.toMegabytes() * 8
    return megabit.toFloat() / time.seconds
  }
}
