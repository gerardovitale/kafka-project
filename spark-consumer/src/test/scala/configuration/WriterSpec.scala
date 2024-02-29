package configuration

import configTest.SparkSessionTestWrapper
import org.apache.spark.sql.Row
import org.apache.spark.sql.streaming.DataStreamWriter
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.scalatest.funsuite.AnyFunSuite

class WriterSpec extends AnyFunSuite with SparkSessionTestWrapper {
  test("writer should return DataStreamWriter when it's passed a stream df and a complete destination config") {
    val testOptions = Map("path" -> "test/path-to/table")
    val testConfig = new DestinationConfig("delta", "append", testOptions)
    val testSchema = new StructType()
      .add(StructField("col_1", StringType))
      .add(StructField("col_2", StringType))
    val testStreamDF = spark.readStream
      .format("csv")
      .schema(testSchema)
      .option("header", "true")
      .load("spark-consumer/src/test/fixtures/test-stream-dt.csv")

    val actualWriter = new Writer(testConfig).prepareRun(testStreamDF)

    assert(actualWriter.isInstanceOf[DataStreamWriter[Row]])
  }

  test("writer should return DataStreamWriter when options are missing from destination config") {
    val testConfig = new DestinationConfig("delta", "append")
    val testSchema = new StructType()
      .add(StructField("col_1", StringType))
      .add(StructField("col_2", StringType))
    val testStreamDF = spark.readStream
      .format("csv")
      .schema(testSchema)
      .option("header", "true")
      .load("spark-consumer/src/test/fixtures/test-stream-dt.csv")

    val actualWriter = new Writer(testConfig).prepareRun(testStreamDF)

    assert(actualWriter.isInstanceOf[DataStreamWriter[Row]])
  }
}

