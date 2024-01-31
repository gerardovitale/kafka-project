package configTest

import dotenv.DotEnv
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterAll, Suite}

trait SparkSessionTestWrapper extends BeforeAndAfterAll {
  self: Suite =>
  @transient var spark: SparkSession = _
  @transient var wikimediaSchemaPath: String = _
  @transient var kafkaSchemaPath: String = _

  override def beforeAll(): Unit = {
    super.beforeAll()

    val dotEnv = DotEnv.loadEnvFromFile(".env")
    wikimediaSchemaPath = dotEnv.getOrElse("WIKIMEDIA_SCHEMA_PATH", "")
    kafkaSchemaPath = dotEnv.getOrElse("KAFKA_SCHEMA_PATH", "")

    spark = SparkSession.builder()
      .appName("unit-testing")
      .master("local[2]")
      .getOrCreate()
  }

  override def afterAll(): Unit = {
    if (spark != null) {
      spark.stop()
      spark = null
    }
    super.afterAll()
  }
}

