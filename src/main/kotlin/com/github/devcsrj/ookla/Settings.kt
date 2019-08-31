package com.github.devcsrj.ookla

import java.net.InetAddress

/**
 * Maps to https://www.speedtest.net/speedtest-config.php
 */
data class Settings(
  val address: InetAddress,
  val location: Location,
  val isp: String,
  val countryCode: String
)
