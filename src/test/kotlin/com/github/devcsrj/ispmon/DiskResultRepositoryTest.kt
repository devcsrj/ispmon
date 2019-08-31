package com.github.devcsrj.ispmon

import org.junit.jupiter.api.Assertions.assertEquals
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

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
      11:30:00,${16 * 1024 * 1024},${8 * 1024 * 1024}
      12:00:01,${12 * 1024 * 1024},${8 * 1024 * 1024}
      12:30:02,${16 * 1024 * 1024},${8 * 1024 * 1024}
      
      """.trimIndent())
      }
      val f2 = tmpDir.resolve("2019-08-30.csv")
      Files.newBufferedWriter(f2).use {
        it.write("""
      17:15:02,${5 * 1024 * 1024},${11 * 1024 * 1024}
      
      """.trimIndent())
      }
    }

    it("should find results by date") {
      val results = repo.findSince(LocalDate.of(2019, 8, 30))
      val actual = results.iterator().next()
      assertEquals(actual, Result(
        timestamp = LocalDateTime.of(2019, 8, 30, 17, 15, 2),
        upload = Speed(DataSize.ofMegabytes(5), TimeUnit.SECONDS),
        download = Speed(DataSize.ofMegabytes(11), TimeUnit.SECONDS)
      ))
    }

    it("should find results from multiple dates") {
      val results = repo.findSince(LocalDate.of(2019, 8, 29))
      assertEquals(4, results.size)
    }

    it("should save results") {
      repo.save(Result(
        timestamp = LocalDateTime.of(2019, 6, 1, 17, 15, 2),
        upload = Speed(DataSize.ofMegabytes(5), TimeUnit.SECONDS),
        download = Speed(DataSize.ofMegabytes(11), TimeUnit.SECONDS)
      ))
      repo.save(Result(
        timestamp = LocalDateTime.of(2019, 6, 1, 17, 45, 18),
        upload = Speed(DataSize.ofMegabytes(25), TimeUnit.SECONDS),
        download = Speed(DataSize.ofMegabytes(30), TimeUnit.SECONDS)
      ))

      val file = tmpDir.resolve("2019-06-01.csv")
      val actual = Files.newBufferedReader(file).readText()
      assertEquals("""
      17:15:02,${5 * 1024 * 1024},${11 * 1024 * 1024}
      17:45:18,${25 * 1024 * 1024},${30 * 1024 * 1024}
      
      """.trimIndent(), actual)
    }

    afterEach {
      tmpDir.toFile().deleteRecursively()
    }
  }

})
