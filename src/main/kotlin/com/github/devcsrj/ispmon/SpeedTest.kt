package com.github.devcsrj.ispmon

interface SpeedTest : Runnable {

  /**
   * Executes a single speed test run
   */
  override fun run()
}
