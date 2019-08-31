package com.github.devcsrj.ispmon

import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.reflect.KProperty


/**
 * A speedtest implementation that makes use of [sivel/speedtest-cli] python library.
 *
 * On first run, this downloads a copy of the python script. Then
 * uses the GraalVM Polyglot API to execute it.
 */
class PythonSpeedTest : SpeedTest {

  private val scriptSource: Source by ScriptSource()

  override fun run() {
    val out = ByteArrayOutputStream()
    val context = Context.newBuilder("python")
      .out(out)
      .allowIO(true)
      .allowAllAccess(true)
      .build()
    context.eval(scriptSource)

    println(out.toString()) // TODO
  }

  class ScriptSource {

    operator fun getValue(
      thisRef: PythonSpeedTest?,
      property: KProperty<*>
    ): Source {
      val script = downloadScript()
      val content = Files.newBufferedReader(script).readText()
      return Source.newBuilder("python", content, "speedtest")
        .cached(true)
        .build()
    }

    private fun downloadScript(): Path {
      val py = Paths.get("speedtest.py")
      if (Files.isRegularFile(py))
        return py

      val url = "https://raw.githubusercontent.com/sivel/speedtest-cli/master/speedtest.py"
      var httpConn: HttpURLConnection? = null
      try {
        httpConn = URL(url).openConnection() as HttpURLConnection
        val responseCode = httpConn.responseCode

        // always check HTTP response code first
        if (responseCode == HTTP_OK) {
          // opens input stream from the HTTP connection
          httpConn.inputStream.use { src ->
            Files.newOutputStream(py,
              StandardOpenOption.CREATE_NEW,
              StandardOpenOption.TRUNCATE_EXISTING).use { sink ->
              src.copyTo(sink)
            }
          }
        } else {
          throw IllegalStateException("Could not download '$url'. Server responded with $responseCode")
        }
        return py
      } finally {
        httpConn?.disconnect()
      }
    }
  }
}
