package dotenv

import scala.io.Source

object DotEnv {
  private val envFile = ".env"
  val envMap: Map[String, String] = loadEnvFromFile(envFile)

  private def loadEnvFromFile(filename: String): Map[String, String] = {
    val source = Source.fromFile(filename)
    val lines = try source.getLines() mkString "\n" finally source.close()
    lines.split("\n").map(_.split("=", 2)).collect {
      case Array(key, value) => key.trim -> value.trim
    }.toMap
  }
}
