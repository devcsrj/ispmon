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

import com.github.devcsrj.ookla.Speedtest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration

object Module {

  fun logger(): Logger {
    return LoggerFactory.getLogger("Ispmon")
  }

  fun resultsRepository(): ResultRepository {
    val resultsDir = Paths.get("results")
    Files.createDirectories(resultsDir)
    return DiskResultRepository(resultsDir)
  }

  fun monitorInterval(): Duration {
    val interval = System.getenv("ISPMON_INTERVAL")?.toLong() ?: 15L
    return Duration.ofMinutes(interval)
  }

  fun monitorDuration(): Duration {
    val duration = System.getenv("ISPMON_DURATION")?.toLong() ?: 30L
    return Duration.ofSeconds(duration)
  }

  fun speedtestTask(repo: ResultRepository): Runnable {
    return Runnable {
      val test = Speedtest(monitorDuration())
      try {
        val result = test.call()
        repo.save(result)
      } catch (e: Throwable) {
        LoggerFactory
          .getLogger("Ispmon")
          .error("Could not finish speedtest due to exception", e)
      }
    }
  }
}
