/**
 * MIT License
 *
 * Copyright (c) 2019 Reijhanniel Jearl Campos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
