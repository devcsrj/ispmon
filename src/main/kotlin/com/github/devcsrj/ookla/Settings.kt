package com.github.devcsrj.ookla

/**
 * Maps to https://www.speedtest.net/speedtest-config.php
 */
data class Settings(
  val address: String,
  val location: Location,
  val isp: String,
  val countryCode: String
)
