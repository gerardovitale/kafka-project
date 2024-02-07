package configuration

import java.util
import scala.annotation.tailrec
import scala.jdk.CollectionConverters.MapHasAsScala

class Configuration {
  private val envFile = ".env"
  private val dotEnv = DotEnv.loadEnvFromFile(envFile)

  val sparkAppName: String = dotEnv.getOrElse("SPARK_APP_NAME", "")
  val sparkMaster: String = dotEnv.getOrElse("SPARK_MASTER", "")

  val wikimediaSchemaPath: String = dotEnv.getOrElse("WIKIMEDIA_SCHEMA_PATH", "")
  val kafkaSchemaPath: String = dotEnv.getOrElse("KAFKA_SCHEMA_PATH", "")

  val kafkaReadStreamMapOptions: Map[String, String] = Map(
    "kafka.bootstrap.servers" -> dotEnv.getOrElse("KAFKA_BOOTSTRAP_SERVER", ""),
    "subscribe" -> dotEnv.getOrElse("KAFKA_TOPIC", ""),
  )
}

class DestinationConfig(format: String, outputMode: String, options: Map[String, String]) {
  def getFormat: String = format

  def getOutputMode: String = outputMode

  def getOptionsMap: Map[String, String] = options
}

class PipelineConfigReader(yamlPath: String) {
  val yamlMap: Map[String, Any] = YamlReader.readAsMap(yamlPath)

  private def getDestinationConfigAsMap(stepName: String): util.LinkedHashMap[String, Any] = {
    val pipelineSteps = yamlMap.getOrElse("pipeline", null)
      .asInstanceOf[java.util.ArrayList[util.LinkedHashMap[String, String]]]
      .toArray()

    @tailrec
    def loop(index: Int): util.LinkedHashMap[String, Any] = {
      val step = pipelineSteps(index)
        .asInstanceOf[util.LinkedHashMap[String, String]]
        .getOrDefault("step", null)

      if (index >= pipelineSteps.length) null
      else if (step == stepName)
        pipelineSteps(index)
          .asInstanceOf[util.LinkedHashMap[String, util.LinkedHashMap[String, Any]]]
          .get("destination")
      else loop(index + 1)
    }

    loop(0)
  }

  def getDestinationConfig(stepName: String): DestinationConfig = {
    val destinationConfigMap = getDestinationConfigAsMap(stepName)
    if (destinationConfigMap == null) return null
    val format = destinationConfigMap.get("format").asInstanceOf[String]
    val outputMode = destinationConfigMap.get("outputMode").asInstanceOf[String]
    val options = destinationConfigMap.get("options").asInstanceOf[util.LinkedHashMap[String, String]].asScala.toMap
    new DestinationConfig(format, outputMode, options)
  }
}
