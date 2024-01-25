package schemaReader

import org.apache.spark.sql.types._
import org.yaml.snakeyaml.Yaml

import java.util
import scala.io.Source
import scala.jdk.CollectionConverters.MapHasAsScala

class Schema(yamlPath: String) {
  private val yaml: Yaml = new Yaml()

  private def readYaml(): Map[String, Any] = {
    val yamlFile = Source.fromFile(yamlPath)
    val yamlContent = yamlFile.mkString
    yamlFile.close()
    yaml.load(yamlContent).asInstanceOf[java.util.Map[String, Any]].asScala.toMap
  }

  private def buildSparkSchema(yamlMap: Map[String, Any]): StructType = {
    val propertiesMap = yamlMap("allOf")
      .asInstanceOf[util.ArrayList[util.LinkedHashMap[String, util.LinkedHashMap[String, util.LinkedHashMap[Any, Any]]]]]
      .get(1).get("properties")

    val fieldArray = propertiesMap.keySet().toArray()

    val structFields = fieldArray.map { field =>
      val name = field.asInstanceOf[String]
      val dataType = propertiesMap.get(name).get("type") match {
        case "string" => StringType
        case "integer" => IntegerType
        case "long" => LongType
        case "double" => DoubleType
        case "boolean" => BooleanType
        case _ => StringType
        // case _ => throw new IllegalArgumentException(s"Unsupported data type for field $name")
      }
      StructField(name, dataType, nullable = true)
    }
    StructType(structFields)
  }

  def getSparkSchema: StructType = {
    val yamlMap = readYaml()
    buildSparkSchema(yamlMap)
  }
}
