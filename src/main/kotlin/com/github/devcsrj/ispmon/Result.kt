package com.github.devcsrj.ispmon

import java.time.LocalDateTime

/**
 * Represents a single test result
 */
data class Result(

  val timestamp: LocalDateTime,
  val isp: String,
  val ip: String,
  val download: Speed,
  val upload: Speed
)
