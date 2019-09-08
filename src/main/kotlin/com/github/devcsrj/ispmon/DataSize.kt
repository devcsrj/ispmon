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


/**
 * A data size, such as '12MB'.
 */
data class DataSize(private val bytes: Long) : Comparable<DataSize> {

  companion object {
    /**
     * Bytes per Kilobyte.
     */
    private const val BYTES_PER_KB: Long = 1024

    /**
     * Bytes per Megabyte.
     */
    private const val BYTES_PER_MB = BYTES_PER_KB * 1024

    /**
     * Bytes per Gigabyte.
     */
    private const val BYTES_PER_GB = BYTES_PER_MB * 1024

    /**
     * Obtain a [DataSize] representing the specified number of bytes.
     * @param bytes the number of bytes, positive or negative
     * @return a [DataSize]
     */
    fun ofBytes(bytes: Long): DataSize {
      return DataSize(bytes)
    }

    /**
     * Obtain a [DataSize] representing the specified number of kilobytes.
     * @param kilobytes the number of kilobytes, positive or negative
     * @return a [DataSize]
     */
    fun ofKilobytes(kilobytes: Long): DataSize {
      return DataSize(Math.multiplyExact(kilobytes, BYTES_PER_KB))
    }

    /**
     * Obtain a [DataSize] representing the specified number of megabytes.
     * @param megabytes the number of megabytes, positive or negative
     * @return a [DataSize]
     */
    fun ofMegabytes(megabytes: Long): DataSize {
      return DataSize(Math.multiplyExact(megabytes, BYTES_PER_MB))
    }

    /**
     * Obtain a [DataSize] representing the specified number of gigabytes.
     * @param gigabytes the number of gigabytes, positive or negative
     * @return a [DataSize]
     */
    fun ofGigabytes(gigabytes: Long): DataSize {
      return DataSize(Math.multiplyExact(gigabytes, BYTES_PER_GB))
    }

    /**
     * Obtain a [DataSize] representing an amount in the specified [DataUnit].
     * @param amount the amount of the size, measured in terms of the unit,
     * positive or negative
     * @return a corresponding [DataSize]
     */
    fun of(amount: Long, unit: DataUnit): DataSize {
      return DataSize(Math.multiplyExact(amount, unit.size().toBytes()))
    }
  }

  /**
   * Return the number of bytes in this instance.
   * @return the number of bytes
   */
  fun toBytes(): Long {
    return this.bytes
  }

  /**
   * Return the number of kilobytes in this instance.
   * @return the number of kilobytes
   */
  fun toKilobytes(): Long {
    return this.bytes / BYTES_PER_KB
  }

  /**
   * Return the number of megabytes in this instance.
   * @return the number of megabytes
   */
  fun toMegabytes(): Long {
    return this.bytes / BYTES_PER_MB
  }

  /**
   * Return the number of gigabytes in this instance.
   * @return the number of gigabytes
   */
  fun toGigabytes(): Long {
    return this.bytes / BYTES_PER_GB
  }

  override fun compareTo(other: DataSize): Int {
    return this.bytes.compareTo(other.bytes)
  }
}
