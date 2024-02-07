package configuration

import org.apache.spark.sql.types._

import java.util
import scala.jdk.CollectionConverters.MapHasAsScala

class Schema(yamlPath: String) {

  def getSparkSchema: StructType = {
    val yamlMap = YamlReader.readAsMap(yamlPath)
    buildSparkSchema(yamlMap)
  }

  private def buildSparkSchema(yamlMap: Map[String, Any]): StructType = {
    val structFields = yamlMap("columns").asInstanceOf[java.util.ArrayList[util.LinkedHashMap[String, String]]]
      .toArray().map(column => {
        val castColumn = column.asInstanceOf[util.LinkedHashMap[String, String]].asScala.toMap
        val name: String = castColumn.getOrElse("name", "")
        val dataType: DataType = getDataType(castColumn)
        val nullable: Boolean = castColumn.getOrElse("nullable", "true").asInstanceOf[Boolean]
        StructField(name, dataType, nullable)
      })
    StructType(structFields)
  }

  private def getDataType(column: Map[String, String]): DataType = {
    column.getOrElse("type", "") match {
      case "string" => StringType
      case "integer" => IntegerType
      case "long" => LongType
      case "double" => DoubleType
      case "boolean" => BooleanType
      case "binary" => BinaryType
      case "timestamp" => TimestampType
      case _ => StringType
    }
  }
}
