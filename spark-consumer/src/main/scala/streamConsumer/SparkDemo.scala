package streamConsumer

import org.apache.spark.sql.SparkSession
import schemaReader.Schema
import transformation.KafkaMessage

object SparkDemo {

  private def exampleStreaming(config: SparkDemoConfig): Unit = {
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
    val query = processedDF.writeStream
      .outputMode("append")
      .format("console")
      .option("truncate", value = false)
      .start()

    query.awaitTermination()
  }

  def main(args: Array[String]): Unit = {
    val config = new SparkDemoConfig
    exampleStreaming(config)
  }
}
