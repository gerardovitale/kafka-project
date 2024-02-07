package configuration

import org.scalatest.funsuite.AnyFunSuite

class ConfigurationSpec extends AnyFunSuite {
  test("get destination config") {
    val testYamlPath: String = "spark-consumer/src/test/fixtures/test-pipeline-config.yaml"
    val testPipelineConfig = new PipelineConfigReader(testYamlPath)

    // step: transformation-1
    val expectedStep1DestinationConfig = new DestinationConfig(
      format = "parquet",
      outputMode = "append",
      options = Map("path" -> "data/bronze/wikimedia-recent-change")
    )

    val actualStep1DestinationConfig = testPipelineConfig.getDestinationConfig("transformation-1")

    assert(actualStep1DestinationConfig.getFormat == expectedStep1DestinationConfig.getFormat)
    assert(actualStep1DestinationConfig.getOutputMode == expectedStep1DestinationConfig.getOutputMode)
    assert(actualStep1DestinationConfig.getOptionsMap == expectedStep1DestinationConfig.getOptionsMap)

    // step: transformation-2
    val expectedStep2DestinationConfig = new DestinationConfig(
      format = "delta",
      outputMode = "overwrite",
      options = Map(
        "path" -> "data/silver/wikimedia-recent-change",
        "checkpointLocation" -> "path-to-checkpoint",
      )
    )

    val actualStep2DestinationConfig = testPipelineConfig.getDestinationConfig("transformation-2")

    assert(actualStep2DestinationConfig.getFormat == expectedStep2DestinationConfig.getFormat)
    assert(actualStep2DestinationConfig.getOutputMode == expectedStep2DestinationConfig.getOutputMode)
    assert(actualStep2DestinationConfig.getOptionsMap == expectedStep2DestinationConfig.getOptionsMap)
  }
}
