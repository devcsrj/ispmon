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

internal object Util {

  /**
   * Converts a string in the form of:
   * ```
   * key1="value1" key2="value2" key3="value3"
   * ```
   *
   * ...into a Map
   */
  fun mapFromAttributeLine(str: String): Map<String, String> {
    val map = mutableMapOf<String, String>()
    val keyBuffer = StringBuffer()
    val it = str.iterator()
    while (it.hasNext()) {
      val k = it.nextChar()
      if (k == '=') {
        val key = keyBuffer.toString()
        val value = readAttributeValue(it)
        map[key] = value
        keyBuffer.setLength(0)
        continue
      }
      if (k == ' ') {
        keyBuffer.setLength(0)
        continue
      }
      keyBuffer.append(k)
    }
    return map
  }

  private fun readAttributeValue(it: CharIterator): String {
    val valueBuffer = StringBuffer()
    var opened = false
    while (it.hasNext()) {
      val v = it.nextChar()
      if (v == '"') {
        if (opened)
          break // closed

        opened = true
        continue
      }
      valueBuffer.append(v)
    }
    return valueBuffer.toString()
  }
}
