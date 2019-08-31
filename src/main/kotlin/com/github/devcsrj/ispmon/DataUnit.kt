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
