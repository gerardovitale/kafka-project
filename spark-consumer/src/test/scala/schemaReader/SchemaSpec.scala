package schemaReader

import org.apache.spark.sql.types._
import org.scalatest.funsuite.AnyFunSuite

class SchemaSpec extends AnyFunSuite {

  test("buildSparkSchema") {
    val expectedSchema: StructType = StructType(
      Seq(
        StructField("id", StringType, nullable = true),

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

        StructField("log_id", StringType, nullable = true),
        StructField("log_type", StringType, nullable = true),
        StructField("log_action", StringType, nullable = true),
        StructField("log_params", StringType, nullable = true),
        StructField("log_action_comment", StringType, nullable = true),
      )
    )

    val actualSchema = new Schema("schemas/wikimedia-recentchange.yaml").getSparkSchema
    assert(actualSchema === expectedSchema)
  }
}

