package transformer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, from_json, to_timestamp, window}
import org.apache.spark.sql.types.StructType

object KafkaMessage {

  def denormalize(dataFrame: DataFrame, schema: StructType): DataFrame =
    dataFrame.select(from_json(col("value").cast("string"), schema).as("data"))
      .select("data.*")

  def getMessageRatePerMinutes(dataFrame: DataFrame): DataFrame =
    dataFrame.withColumn("timestamp", to_timestamp(col("timestamp")))
      .withWatermark("timestamp", "30 Minutes")
      .groupBy(window(col("timestamp"), "1 Minutes").alias("time_window"))
      .count()
}
