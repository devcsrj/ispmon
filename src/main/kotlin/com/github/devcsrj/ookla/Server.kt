package com.github.devcsrj.ookla

import java.net.URI

data class Server(

  val uploadUrl: URI,
  val location: Location,
  val countryName: String,
  val countryCode: String,
  val sponsor: String,
  val host: String
)
