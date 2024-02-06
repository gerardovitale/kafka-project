package configuration

import scala.io.Source

object DotEnv {
  def loadEnvFromFile(filename: String): Map[String, String] = {
    val source = Source.fromFile(filename)
    val lines = try source.getLines() mkString "\n" finally source.close()
    lines.split("\n").map(_.split("=", 2)).collect {
      case Array(key, value) => key.trim -> value.trim
    }.toMap
  }
}
