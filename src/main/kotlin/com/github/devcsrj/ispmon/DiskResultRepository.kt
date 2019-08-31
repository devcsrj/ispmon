package com.github.devcsrj.ispmon

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * Uses the file system to persist results.
 *
 * Results are stored in a CSV, whose name matches the current date.
 * Each line in the file is stored in the form of:
 * ```
 * HH:mm:ss,upload_in_bytes,download_in_bytes
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
      val c2 = result.upload.size.toBytes()
      val c3 = result.download.size.toBytes()
      it.write("$c1,$c2,$c3")
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
        val upload = DataSize.ofBytes(tokens[1].toLong())
        val download = DataSize.ofBytes(tokens[2].toLong())
        results.add(Result(
          timestamp = LocalDateTime.of(current, time),
          upload = Speed(upload, TimeUnit.SECONDS),
          download = Speed(download, TimeUnit.SECONDS)
        ))
      }

      current = current.plusDays(1L)
    }
    return results
  }
}
