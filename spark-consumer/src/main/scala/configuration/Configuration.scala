package configuration

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
