package streamConsumer

import configuration.{Configuration, PipelineConfigReader, Schema, Writer}
import org.apache.spark.sql.SparkSession
import transformer.KafkaMessage

object CryptoComStreamConsumer {

  private def run(): Unit = {
    val config = new Configuration
    val pipelineConfig = new PipelineConfigReader("pipelines/cryptocom.yaml")
    val spark = SparkSession.builder
      .appName(config.sparkAppName)
      .master(config.sparkMaster)
      .getOrCreate()

    val kafkaConfig = Map(
      "kafka.bootstrap.servers" -> "localhost:9094",
      "subscribe" -> "crypto.ticker.btc_eur",
      "startingOffsets" -> "latest")
    val kafkaDF = spark.readStream
      .format("kafka")
      .options(kafkaConfig)
      .load()

    val cryptocomSchema = new Schema("schemas/cryptocom_ticker.yaml").getSparkSchema
    val processedDF = KafkaMessage.denormalize(kafkaDF, cryptocomSchema)

    val writer = new Writer(pipelineConfig.getDestinationConfig("ingest"))
    val query = writer.prepareRun(processedDF).option("truncate", "false").start()

    query.awaitTermination()
  }

  def main(args: Array[String]): Unit = {
    run()
  }
}
