package com.github.devcsrj.ispmon

import java.time.LocalDate

interface ResultRepository {

  /**
   * Fetches results starting from the specified date, up to the
   * present
   */
  fun findSince(timestamp: LocalDate): Collection<Result>

  /**
   * Persists the provided result
   */
  fun save(result: Result)
}
