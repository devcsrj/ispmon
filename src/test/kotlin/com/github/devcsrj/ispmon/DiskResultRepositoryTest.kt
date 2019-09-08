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

import org.junit.jupiter.api.Assertions.assertEquals
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

object DiskResultRepositoryTest : Spek({

  describe("repository") {
    lateinit var tmpDir: Path
    lateinit var repo: ResultRepository

    beforeEach {
      tmpDir = Files.createTempDirectory("")
      repo = DiskResultRepository(tmpDir)

      val f1 = tmpDir.resolve("2019-08-29.csv")
      Files.newBufferedWriter(f1).use {
        it.write("""
      11:30:00,192.168.0.1,Gov,${16 * 1024 * 1024},1,${8 * 1024 * 1024},1
      12:00:01,192.168.0.1,Gov,${12 * 1024 * 1024},1,${8 * 1024 * 1024},1
      12:30:02,192.168.0.1,Gov,${16 * 1024 * 1024},1,${8 * 1024 * 1024},1
      
      """.trimIndent())
      }
      val f2 = tmpDir.resolve("2019-08-30.csv")
      Files.newBufferedWriter(f2).use {
        it.write("""
      17:15:02,192.168.0.1,Gov,${5 * 1024 * 1024},1,${11 * 1024 * 1024},1
      
      """.trimIndent())
      }
    }

    it("should find results by date") {
      val results = repo.findSince(LocalDate.of(2019, 8, 30))
      val actual = results.iterator().next()
      assertEquals(actual, Result(
        timestamp = LocalDateTime.of(2019, 8, 30, 17, 15, 2),
        ip = "192.168.0.1",
        isp = "Gov",
        upload = Speed(DataSize.ofMegabytes(5), Duration.ofSeconds(1L)),
        download = Speed(DataSize.ofMegabytes(11), Duration.ofSeconds(1L))
      ))
    }

    it("should find results from multiple dates") {
      val results = repo.findSince(LocalDate.of(2019, 8, 29))
      assertEquals(4, results.size)
    }

    it("should save results") {
      repo.save(Result(
        timestamp = LocalDateTime.of(2019, 6, 1, 17, 15, 2),
        ip = "192.168.0.1",
        isp = "Gov",
        upload = Speed(DataSize.ofMegabytes(5), Duration.ofSeconds(2L)),
        download = Speed(DataSize.ofMegabytes(11), Duration.ofSeconds(2L))
      ))
      repo.save(Result(
        timestamp = LocalDateTime.of(2019, 6, 1, 17, 45, 18),
        ip = "192.168.0.1",
        isp = "Gov",
        upload = Speed(DataSize.ofMegabytes(25), Duration.ofSeconds(2L)),
        download = Speed(DataSize.ofMegabytes(30), Duration.ofSeconds(2L))
      ))

      val file = tmpDir.resolve("2019-06-01.csv")
      val actual = Files.newBufferedReader(file).readText()
      assertEquals("""
      17:15:02,192.168.0.1,Gov,${5 * 1024 * 1024},2,${11 * 1024 * 1024},2
      17:45:18,192.168.0.1,Gov,${25 * 1024 * 1024},2,${30 * 1024 * 1024},2
      
      """.trimIndent(), actual)
    }

    afterEach {
      tmpDir.toFile().deleteRecursively()
    }
  }

})
