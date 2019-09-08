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

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Uses the file system to persist results.
 *
 * Results are stored in a CSV, whose name matches the current date.
 * Each line in the file is stored in the form of:
 * ```
 * HH:mm:ss,ip,isp,uploaded_bytes,duration,downloaded_bytes,duration
 * ```
 *
 */
class DiskResultRepository(private val dir: Path) : ResultRepository {

  private val datePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  private val timePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

  init {
    require(Files.isDirectory(dir)) { "Expecting a directory, but got: $dir" }
  }

  override fun save(result: Result) {
    val date = result.timestamp.toLocalDate()
    val name = datePattern.format(date)
    val file = dir.resolve("$name.csv")
    Files.newBufferedWriter(file,
      StandardOpenOption.CREATE,
      StandardOpenOption.APPEND).use {
      val c1 = timePattern.format(result.timestamp.toLocalTime())
      val c2 = result.ip
      val c3 = result.isp
      val c4 = result.upload.size.toBytes()
      val c5 = result.upload.time.seconds
      val c6 = result.download.size.toBytes()
      val c7 = result.download.time.seconds
      it.write("$c1,$c2,$c3,$c4,$c5,$c6,$c7")
      it.write(System.lineSeparator())
    }
  }

  override fun findSince(timestamp: LocalDate): Collection<Result> {
    val results = mutableListOf<Result>()
    val today = LocalDate.now()
    var current = timestamp
    while (current.isBefore(today) || current.isEqual(today)) {

      val name = datePattern.format(current)
      val file = dir.resolve("$name.csv")
      if (!Files.isRegularFile(file)) {
        current = current.plusDays(1L)
        continue
      }

      Files.newBufferedReader(file).forEachLine {
        val tokens = it.split(",")
        val time = LocalTime.parse(tokens[0], timePattern)
        val ip = tokens[1]
        val isp = tokens[2]
        val upload = Speed(
          size = DataSize.ofBytes(tokens[3].toLong()),
          time = Duration.ofSeconds(tokens[4].toLong())
        )
        val download = Speed(
          size = DataSize.ofBytes(tokens[5].toLong()),
          time = Duration.ofSeconds(tokens[6].toLong())
        )
        results.add(Result(
          timestamp = LocalDateTime.of(current, time),
          ip = ip,
          isp = isp,
          upload = upload,
          download = download
        ))
      }

      current = current.plusDays(1L)
    }
    return results
  }
}
