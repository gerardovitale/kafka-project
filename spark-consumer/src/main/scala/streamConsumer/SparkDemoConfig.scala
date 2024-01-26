package streamConsumer

import dotenv.DotEnv

class SparkDemoConfig {
  private val dotEnv = DotEnv.envMap
  val sparkAppName: String = dotEnv.getOrElse("SPARK_APP_NAME", "")
  val sparkMaster: String = dotEnv.getOrElse("SPARK_MASTER", "")
  val kafkaServer: String = dotEnv.getOrElse("KAFKA_BOOTSTRAP_SERVER", "")
  val kafkaTopic: String = dotEnv.getOrElse("KAFKA_TOPIC", "")
  val wikimediaSchemaPath: String = dotEnv.getOrElse("WIKIMEDIA_SCHEMA_PATH", "")
}
