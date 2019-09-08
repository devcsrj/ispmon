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
package com.github.devcsrj.ispmon

import java.util.Objects

/**
 * A standard set of data size units.
 */
enum class DataUnit(
  private val suffix: String, private val size: DataSize) {

  /**
   * Bytes.
   */
  BYTES("B", DataSize.ofBytes(1)),

  /**
   * Kilobytes.
   */
  KILOBYTES("KB", DataSize.ofKilobytes(1)),

  /**
   * Megabytes.
   */
  MEGABYTES("MB", DataSize.ofMegabytes(1)),

  /**
   * Gigabytes.
   */
  GIGABYTES("GB", DataSize.ofGigabytes(1));

  internal fun size(): DataSize {
    return this.size
  }

  companion object {

    /**
     * Return the [DataUnit] matching the specified `suffix`.
     * @param suffix one of the standard suffix
     * @return the [DataUnit] matching the specified `suffix`
     * @throws IllegalArgumentException if the suffix does not match any
     * of this enum's constants
     */
    fun fromSuffix(suffix: String): DataUnit {
      for (candidate in values()) {
        if (Objects.equals(candidate.suffix, suffix)) {
          return candidate
        }
      }
      throw IllegalArgumentException("Unknown unit '$suffix'")
    }
  }
}
