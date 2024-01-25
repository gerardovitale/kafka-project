package streamConsumer

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, from_json}
import schemaReader.Schema

object SparkDemo {

  private def exampleStreaming(): Unit = {
    val spark = SparkSession
      .builder
      .appName("KafkaStreamExample")
      .master("local[*]")
      .getOrCreate()

    // Define Kafka parameters
    val kafkaParams = Map(
      "kafka.bootstrap.servers" -> "127.0.0.1:9094",
      "subscribe" -> "wikimedia.recent_changes",
    )

    // Define the schema for incoming Kafka messages
    val wikimediaSchema = new Schema("schemas/wikimedia-recentchange.yaml").getSparkSchema

    // Read data from Kafka
    val kafkaDF = spark
      .readStream
      .format("kafka")
      .options(kafkaParams)
      .option("startingOffsets", "latest")
      .load()

    // Convert Kafka key-value messages to DataFrame
    val processedDF = kafkaDF.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .select(from_json(col("value"), wikimediaSchema).as("data"))
      .select("data.*")

    // Define your processing logic here, for example, writing to console
    val query = processedDF.writeStream
      .outputMode("append")
      .format("console")
      .option("truncate", value = false)
      .start()

    query.awaitTermination()
  }

  def main(args: Array[String]): Unit = {
    exampleStreaming()
  }
}
