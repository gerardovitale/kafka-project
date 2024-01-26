package dotenv

import org.scalatest.funsuite.AnyFunSuite

class DotenvSpec extends AnyFunSuite {
  test("loadDotenv") {
    val actualEnvVariables = DotEnv.envMap

    assert(actualEnvVariables.get("STREAM_URL").contains("https://stream.wikimedia.org/v2/stream/recentchange"))
    assert(actualEnvVariables.get("WIKIMEDIA_SCHEMA_PATH").contains("schemas/wikimedia-recentchange.yaml"))
    assert(actualEnvVariables.get("SPARK_APP_NAME").contains("KafkaStreamExample"))
    assert(actualEnvVariables.get("SPARK_MASTER").contains("local[*]"))
    assert(actualEnvVariables.get("KAFKA_TOPIC").contains("wikimedia.recent_changes"))
    assert(actualEnvVariables.get("KAFKA_BOOTSTRAP_SERVER").contains("localhost:9094"))
    assert(actualEnvVariables.get("KAFKA_REST_PROXY_SERVER").contains("http://localhost:8082"))
    assert(actualEnvVariables.get("KAFKA_LINGER_MS_CONFIG").contains("20"))
    assert(actualEnvVariables.get("KAFKA_BATCH_SIZE_CONFIG").contains("32768"))
    assert(actualEnvVariables.get("KAFKA_COMPRESSION_TYPE_CONFIG").contains("snappy"))
  }
}
