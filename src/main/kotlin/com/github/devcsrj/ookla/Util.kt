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
