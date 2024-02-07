package configuration

import org.yaml.snakeyaml.Yaml

import scala.io.Source
import scala.jdk.CollectionConverters.MapHasAsScala

object YamlReader {
  def readAsMap(yamlPath: String): Map[String, Any] = {
    val yamlFile = Source.fromFile(yamlPath)
    val yamlContent = try yamlFile.mkString finally yamlFile.close()
    new Yaml()
      .load(yamlContent)
      .asInstanceOf[java.util.Map[String, Any]]
      .asScala.toMap
  }
}
