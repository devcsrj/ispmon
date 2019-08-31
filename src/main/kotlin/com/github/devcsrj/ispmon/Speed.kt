package com.github.devcsrj.ispmon

import java.util.concurrent.TimeUnit

/**
 * Speed, such as 24MB/s
 */
data class Speed(

  val size: DataSize,
  val time: TimeUnit
)
