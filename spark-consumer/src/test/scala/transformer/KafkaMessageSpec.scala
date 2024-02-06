package transformer

import configTest.SparkSessionTestWrapper
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
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

    val expectedSchema = new StructType()
      .add("name", StringType, nullable = true)
      .add("type", StringType, nullable = true)
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

  test("aggregate kafka message") {
    val testSchema = new StructType()
      .add("id", IntegerType, nullable = true)
      .add("type", StringType, nullable = true)
      .add("timestamp", IntegerType, nullable = true)
    val testData = Seq(
      // Row("ID", "type" "timestamp"),
      Row(1234567890, "edit", 1707227685),
      Row(1234567891, "edit", 1707227685),
      Row(1234567892, "edit", 1707227685 + 60),
      Row(1234567892, "edit", 1707227685 + 61),
      Row(1234567892, "edit", 1707227685 + 62),
      Row(1234567892, "edit", 1707227685 + 120),
    )
    val testDF = spark.createDataFrame(spark.sparkContext.parallelize(testData), testSchema)

    val expectedTimeWindowSchema = new StructType()
      .add("start", TimestampType, nullable = true)
      .add("end", TimestampType, nullable = true)
    val expectedSchema = new StructType()
      .add("time_window", expectedTimeWindowSchema, nullable = false)
      .add("count", LongType, nullable = false)
    val expectedData = Seq(
      // Row("time_window", "count"),
      Row(Row(Timestamp.valueOf("2024-02-06 14:54:00"), Timestamp.valueOf("2024-02-06 14:55:00")), 2L),
      Row(Row(Timestamp.valueOf("2024-02-06 14:55:00"), Timestamp.valueOf("2024-02-06 14:56:00")), 3L),
      Row(Row(Timestamp.valueOf("2024-02-06 14:56:00"), Timestamp.valueOf("2024-02-06 14:57:00")), 1L),
    )
    val expectedDF = spark.createDataFrame(spark.sparkContext.parallelize(expectedData), expectedSchema)

    val actualDF = KafkaMessage.getMessageRatePerMinutes(testDF)

    assert(expectedDF.schema == actualDF.schema)
    assert(expectedDF.exceptAll(actualDF).isEmpty)
  }
}

