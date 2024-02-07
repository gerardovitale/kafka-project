package streamConsumer

import configuration.{Configuration, DestinationConfig, PipelineConfigReader, Schema, Writer}
import org.apache.spark.sql.SparkSession
import transformer.KafkaMessage

object WikimediaRecentChangeConsumer {

  private def run(): Unit = {
    val config = new Configuration
    val pipelineConfig = new PipelineConfigReader("pipeline.yaml")
    val spark = SparkSession.builder
      .appName(config.sparkAppName)
      .master(config.sparkMaster)
      .getOrCreate()

    // Read data from Kafka
    val kafkaDF = spark.readStream
      .format("kafka")
      .options(config.kafkaReadStreamMapOptions)
      .option("startingOffsets", "latest")
      .load()

    // Convert Kafka key-value messages to DataFrame
    val wikimediaSchema = new Schema(config.wikimediaSchemaPath).getSparkSchema
    val processedDF = KafkaMessage.denormalize(kafkaDF, wikimediaSchema)

    // Define your processing logic here, for example, writing to console
    val writer = new Writer(pipelineConfig.getDestinationConfig("ingest"))
    val query = writer.prepareRun(processedDF).start()

    query.awaitTermination()
  }

  def main(args: Array[String]): Unit = {
    run()
  }
}
