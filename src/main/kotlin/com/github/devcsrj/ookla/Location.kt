package com.github.devcsrj.ookla

import java.lang.Math.toRadians
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class Location(

  val latitude: Double,
  val longitude: Double
) {

  companion object {

    private const val EARTH_RADIUS = 6371 // km
  }

  /**
   * Uses the Haversine formula to compute the distance
   *
   * https://en.wikipedia.org/wiki/Haversine_formula
   *
   * @return the distance in km
   */
  fun distanceFrom(that: Location): Int {
    val dlat = toRadians(that.latitude - this.latitude)
    val dlon = toRadians(that.longitude - this.longitude)
    val a = sin(dlat / 2) * sin(dlat / 2) + (cos(toRadians(this.latitude))
      * cos(toRadians(that.latitude)) * sin(dlon / 2) * sin(dlon / 2))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return (EARTH_RADIUS * c).roundToInt()
  }
}
