package com.github.devcsrj.ispmon

import java.time.Duration

/**
 * Speed, such as 24MB/s
 */
data class Speed(

  val size: DataSize,
  val time: Duration
) {

  /**
   * The value in MB/s
   */
  fun value(): Float {
    return size.toMegabytes().toFloat() / time.seconds
  }
}
