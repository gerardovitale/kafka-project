package transformer

import configTest.SparkSessionTestWrapper
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.scalatest.funsuite.AnyFunSuite
import schemaReader.Schema

import java.sql.Timestamp

class KafkaMessageSpec extends AnyFunSuite with SparkSessionTestWrapper {

  test("denormalize kafka message") {
    val testSchema = new Schema(kafkaSchemaPath).getSparkSchema
    val testData = Seq(
      // Row("key", "value", "topic", "partition", "offset", "timestamp", "timestampType"),
      Row("key1".getBytes, "{\"name\": \"test_name1\", \"type\": \"test_type1\"}".getBytes, "topic1", 0, 100L, new Timestamp(System.currentTimeMillis), 0),
      Row("key2".getBytes, "{\"name\": \"test_name2\", \"type\": \"test_type2\"}".getBytes, "topic2", 1, 200L, new Timestamp(System.currentTimeMillis), 0),
      Row("key3".getBytes, "{\"name\": \"test_name3\", \"type\": \"test_type3\"}".getBytes, "topic3", 2, 300L, new Timestamp(System.currentTimeMillis), 0),
    )
    val testDF = spark.createDataFrame(spark.sparkContext.parallelize(testData), testSchema)

    val expectedSchema = StructType(Seq(
      StructField("name", StringType, nullable = true),
      StructField("type", StringType, nullable = true),
    ))
    val expectedData = Seq(
      Row("test_name1", "test_type1"),
      Row("test_name2", "test_type2"),
      Row("test_name3", "test_type3"),
    )
    val expectedDF = spark.createDataFrame(spark.sparkContext.parallelize(expectedData), expectedSchema)

    val actualDF = KafkaMessage.denormalize(testDF, expectedSchema)

    assert(expectedDF.schema == actualDF.schema)
    assert(expectedDF.exceptAll(actualDF).isEmpty)
  }
}

