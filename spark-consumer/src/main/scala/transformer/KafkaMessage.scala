package transformer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.types.StructType

object KafkaMessage {

  def denormalize(df: DataFrame, schema: StructType): DataFrame =
    df.select(from_json(col("value").cast("string"), schema).as("data"))
      .select("data.*")
}
