package schemaReader

import org.apache.spark.sql.types._
import org.scalatest.funsuite.AnyFunSuite

class SchemaSpec extends AnyFunSuite {

  test("buildSparkSchema for kafka-message") {
    val expectedSchema: StructType = StructType(
      Seq(
        StructField("key", BinaryType, nullable = true),
        StructField("value", BinaryType, nullable = true),
        StructField("topic", StringType, nullable = true),
        StructField("partition", IntegerType, nullable = true),
        StructField("offset", LongType, nullable = true),
        StructField("timestamp", TimestampType, nullable = true),
        StructField("timestampType", IntegerType, nullable = true),
      )
    )

    val actualSchema = new Schema("schemas/kafka-message.yaml").getSparkSchema
    assert(actualSchema === expectedSchema)
  }

  test("buildSparkSchema for wikimedia-recent-changes") {
    val expectedSchema: StructType = StructType(
      Seq(
        StructField("id", IntegerType, nullable = true),
        StructField("type", StringType, nullable = true),
        StructField("title", StringType, nullable = true),
        StructField("namespace", IntegerType, nullable = true),
        StructField("comment", StringType, nullable = true),
        StructField("parsedcomment", StringType, nullable = true),
        StructField("timestamp", IntegerType, nullable = true),
        StructField("user", StringType, nullable = true),
        StructField("bot", BooleanType, nullable = true),
        StructField("server_url", StringType, nullable = true),
        StructField("server_name", StringType, nullable = true),
        StructField("server_script_path", StringType, nullable = true),
        StructField("wiki", StringType, nullable = true),
        StructField("minor", BooleanType, nullable = true),
        StructField("patrolled", BooleanType, nullable = true),
        StructField("length", StringType, nullable = true),
        StructField("revision", StringType, nullable = true),
        StructField("log_id", IntegerType, nullable = true),
        StructField("log_type", StringType, nullable = true),
        StructField("log_action", StringType, nullable = true),
        StructField("log_params", StringType, nullable = true),
        StructField("log_action_comment", StringType, nullable = true),
      )
    )

    val actualSchema = new Schema("schemas/wikimedia-recent-change.yaml").getSparkSchema
    assert(actualSchema === expectedSchema)
  }
}

